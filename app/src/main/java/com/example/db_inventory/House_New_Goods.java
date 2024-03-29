package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerResults;

import java.util.ArrayList;

public class House_New_Goods extends zebraScanner {

    //BARCODE
    public static String barcode;
    Button b1, b2;
    EditText e1, e2, e3;
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
                String barcodeScan = e1.getText().toString().trim().replace("/", "|");
                String itemCodeScan = e3.getText().toString().trim().replace("/", "|");
                barcodeStr = scanResult;
                if (TextUtils.isEmpty(barcodeScan) && TextUtils.isEmpty(itemCodeScan)) {
                    e1.setText(barcodeStr);
                } else if (TextUtils.isEmpty(barcodeScan)) {
                    e1.setText(barcodeStr);
                } else {
                    e3.setText(barcodeStr);
                }
            }
        }
    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");

            if (intent.getAction().equals(RES_ACTION)){

                if(scanResult.length()>0){
                    String barcodeScan = e1.getText().toString().trim().replace("/", "|");
                    String itemCodeScan = e3.getText().toString().trim().replace("/", "|");
                    barcodeStr = scanResult;
                    if (TextUtils.isEmpty(barcodeScan) && TextUtils.isEmpty(itemCodeScan)) {
                        e1.setText(barcodeStr);
                    } else if (TextUtils.isEmpty(barcodeScan)) {
                        e1.setText(barcodeStr);
                    } else {
                        e3.setText(barcodeStr);
                    }
                }
            }
        }
    }

    //RETURN WHAT SCANNER GET
    //GIVE PRIORITY THE BARCODE SCAN TO THE BARCODE COLUMN
    //THEN IF THE BARCODE ALREADY EXIST, THE SCANNED DATA WILL BE GIVEN TO THE ITEM CODE COLUMN
    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode));
            if (barcode != null) {
                String barcodeScan = e1.getText().toString().trim().replace("/", "|");
                String itemCodeScan = e3.getText().toString().trim().replace("/", "|");
                barcodeStr = new String(barcode);
                if (TextUtils.isEmpty(barcodeScan) && TextUtils.isEmpty(itemCodeScan)) {
                    e1.setText(barcodeStr);
                } else if (TextUtils.isEmpty(barcodeScan)) {
                    e1.setText(barcodeStr);
                } else {
                    e3.setText(barcodeStr);
                }

            }
        }
    };

    //ADDING NEW GOOD
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_new_goods);


        e1 = findViewById(R.id.editText_new_goods_barcode);
        e2 = findViewById(R.id.editText_new_goods_name);
        e3 = findViewById(R.id.editText_new_goods_itemcode);

        b1 = findViewById(R.id.btn_new_goods_back);
        b2 = findViewById(R.id.btn_new_goods_next);

        setTitle("eStock_Add New Good");

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

        String users = getIntent().getStringExtra("Users");


        //BACK BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_New_Goods.this, MainActivity.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        //INTENT TO ADD NEW GOODS STEP 2
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = e1.getText().toString().trim().replace("/", "|");
                String name = e2.getText().toString().trim().replace("/", "|");
                String itemcode = e3.getText().toString().trim().replace("/", "|");

                if (TextUtils.isEmpty(barcode)) {
                    e1.setError("Please enter barcode");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    e2.setError("Please enter name");
                    return;
                }

                if (TextUtils.isEmpty(itemcode)) {
                    e3.setText("");
                    return;
                }


                if (barcode.isEmpty()) {
                    Toast.makeText(House_New_Goods.this, "Please enter barcode", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(House_New_Goods.this, New_Goods_step2.class);
                    //PASSING DATA TO STEP 2
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("name", name);
                    intent.putExtra("Users", users);
                    intent.putExtra("ItemCode", itemcode);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(House_New_Goods.this, MainActivity.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }
}