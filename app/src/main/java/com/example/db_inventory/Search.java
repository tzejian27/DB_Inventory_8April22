package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerResults;

import java.util.ArrayList;

public class Search extends zebraScanner {
    //Barcode
    public static String barcode;
    Button b1, b2;
    EditText e1;
    ScanReader scanReader;
    private String barcodeStr;

    private final String RES_ACTION = "android.intent.action.SCANRESULT";
    private BroadcastReceiver scanReceiver;
    ScannerInterface  scanner;

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
                barcodeStr = scanResult;
                e1.setText(barcodeStr);
            }
        }
    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");

            if (intent.getAction().equals(RES_ACTION)){

                if(scanResult.length()>0){
                    barcodeStr = scanResult;
                    e1.setText(barcodeStr);
                }
            }
        }
    }

    //SEARCHING ITEM WITH BARCODE
    //SETTING SCANNED ITEM TO EDIT TEXT BOX
    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode));
            if (barcode != null) {
                barcodeStr = new String(barcode);
                e1.setText(barcodeStr);
            }
        }
    };

    //SEARCHING BARCODE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        b1 = findViewById(R.id.btn_Search_Cancel);
        b2 = findViewById(R.id.btn_Search_Enter);

        e1 = findViewById(R.id.editText_Search_barcode);

        String users = getIntent().getStringExtra("Users");

        //BARCODE SCANNING
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        // Scanner input for iData
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(RES_ACTION);
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, filter2);
        scanner = new ScannerInterface(this);
        scanner.setOutputMode(1);

        //CANCEL SEARCH
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                final String name = intent.getStringExtra("name");
                final String key = intent.getStringExtra("Key");
                Intent page = new Intent(Search.this, Inventory_List.class);
                page.putExtra("name", name);
                page.putExtra("Key", key);
                page.putExtra("Users", users);
                startActivity(page);
                finish();
            }
        });

        //ENTER SEARCH
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode = e1.getText().toString().trim().replace("/", "|");
                Intent intent = getIntent();
                final String name = intent.getStringExtra("name");
                final String key = intent.getStringExtra("Key");
                final String key2 = intent.getStringExtra("Key2");
                Intent page = new Intent(Search.this, Inventory_List2.class);
                page.putExtra("name", name);
                page.putExtra("Barcode", barcode);
                page.putExtra("Key", key);
                page.putExtra("Key2", key2);
                page.putExtra("Users", users);
                startActivity(page);
                finish();
            }
        });
    }
}