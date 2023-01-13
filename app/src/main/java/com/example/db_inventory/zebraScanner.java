package com.example.db_inventory;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.StatusData.ScannerStates;

import java.util.ArrayList;

public abstract class zebraScanner extends AppCompatActivity implements EMDKListener, StatusListener, DataListener, BarcodeManager.ScannerConnectionListener {
    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    public String scanResult;
    public void onOpened(EMDKManager emdkManager) {
        // Get a reference to EMDKManager
        this.emdkManager =  emdkManager;
        // Get a  reference to the BarcodeManager feature object
        initBarcodeManager();
        // Initialize the scanner
        initScanner();
    }

    @Override
    public void onClosed() {
        if (emdkManager != null) {
            // Remove connection listener
            if (barcodeManager != null){
                barcodeManager.removeConnectionListener(this);
                barcodeManager = null;
            }
            // Release all the resources
            emdkManager.release();
            emdkManager = null;
        }
    }


    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        String scanResult = "";
        if ((scanDataCollection != null) &&   (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData =  scanDataCollection.getScanData();
            // Iterate through scanned data and prepare the data.
            for (ScanDataCollection.ScanData data :  scanData) {
                // Get the scanned data
                scanResult =  data.getData();
            }
            // Update EditText with scanned data and type of label on UI thread.
            if (!scanResult.isEmpty()) {
                this.scanResult = scanResult;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //start EMDK service
        askForEMDKConnection();
    }

    @Override
    protected void onPause() {
        // De-initialize scanner
        deInitScanner();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // De-initialize scanner
        deInitScanner();

        super.onDestroy();
    }

    /**
     * start the EMDK service connection
     */
    private void askForEMDKConnection() {
        try{
            EMDKManager.getEMDKManager(getApplicationContext(), this);
        }catch(Exception e){
            System.out.println(e);
        }finally {
            Toast.makeText(this,"Non Zebra Devices.", Toast.LENGTH_LONG);
        }


    }

    @Override
    public void onConnectionChange(ScannerInfo scannerInfo,
                                   BarcodeManager.ConnectionState connectionState) {
        //we don't need to performs logic here currently
        Log.d("connection changed", connectionState.name());
    }

    @Override
    public void onStatus(StatusData statusData) {

        // The status will be returned on multiple cases. Check the state and take the action.
// Get the current state of scanner in background
        ScannerStates state =  statusData.getState();
        String statusStr = "";
// Different states of Scanner
        switch (state) {
            case IDLE:
                // Scanner is idle and ready to change configuration and submit read.
                statusStr = statusData.getFriendlyName()+" is   enabled and idle...";
                // Change scanner configuration. This should be done while the scanner is in IDLE state.
                setConfig();
                try {
                    // Starts an asynchronous Scan. The method will NOT turn ON the scanner beam,
                    //but puts it in a  state in which the scanner can be turned on automatically or by pressing a hardware trigger.
                    scanner.read();
                }
                catch (ScannerException e)   {
                    System.out.println(e.getMessage());
                }
                break;
            case WAITING:
                // Scanner is waiting for trigger press to scan...
                statusStr = "Scanner is waiting for trigger press...";
                break;
            case SCANNING:
                // Scanning is in progress...
                statusStr = "Scanning...";
                break;
            case DISABLED:
                // Scanner is disabledstatusStr = statusData.getFriendlyName()+" is disabled.";
                break;
            case ERROR:
                // Error has occurred during scanning
                statusStr = "An error has occurred.";
                break;
            default:
                break;
        }
    }

    private void setConfig() {
        if (scanner != null) {try {
            // Get scanner config
            ScannerConfig config = scanner.getConfig();
            // Enable haptic feedback
            if (config.isParamSupported("config.scanParams.decodeHapticFeedback")) {
                config.scanParams.decodeHapticFeedback = true;
            }
            // Set scanner config
            scanner.setConfig(config);
        } catch (ScannerException e)   {
            System.out.println(e.getMessage());
        }
        }
    }

    private void initBarcodeManager() {
        // Get the feature object such as BarcodeManager object for accessing the feature.
        barcodeManager =  (BarcodeManager)emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        // Add external scanner connection listener.
        if (barcodeManager == null) {
            System.out.println("Barcode scanning is not supported.");
        }
    }

    private void initScanner() {
        if (scanner == null) {
            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            if (scanner != null) {
                // Implement the DataListener interface and pass the pointer of this object to get the data callbacks.
                scanner.addDataListener(this);

                // Implement the StatusListener interface and pass the pointer of this object to get the status callbacks.
                scanner.addStatusListener(this);

                // Hard trigger. When this mode is set, the user has to manually
                // press the trigger on the device after issuing the read call.
                // NOTE: For devices without a hard trigger, use TriggerType.SOFT_ALWAYS.
                scanner.triggerType = Scanner.TriggerType.HARD;

                try {
                    // Enable the scanner
                    // NOTE: After calling enable(), wait for IDLE status before calling other scanner APIs
                    // such as setConfig() or read().
                    scanner.enable();

                } catch (ScannerException e) {
                    deInitScanner();
                }
            } else {
                Toast.makeText(this, "Failed to initialize the scanner device.", Toast.LENGTH_LONG);
            }
        }
    }

    private void deInitScanner() {
        if (scanner != null) {
            //here we are separating the various try/catch because they are grouped by scope
            //if one of them crash it doesn't mean the others cannot be performed
            try {
                scanner.cancelRead();
                scanner.disable();
            } catch (Exception ignored) {}
            try {
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
            } catch (Exception ignored) {}
            try {
                scanner.release();
            } catch (Exception ignored) {}
            scanner = null;
        }

        if (barcodeManager != null) {
            barcodeManager.removeConnectionListener(this);
            barcodeManager = null;
        }

        // Release the barcode manager resources
        if (emdkManager != null) {
            //we need to release all the resources as described here
            //https://developer.zebra.com/thread/34378
            emdkManager.release(EMDKManager.FEATURE_TYPE.BARCODE);
            emdkManager.release();
            emdkManager = null;
        }
    }

}
