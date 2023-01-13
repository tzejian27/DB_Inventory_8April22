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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerResults;

import java.util.ArrayList;

public class Inventory_step2 extends zebraScanner {

    Button b1, b2;
    EditText e1, e2;
    DatabaseReference databaseReference;
    ScanReader scanReader;
    private String itemCodeStr;
    public static String itemcode;

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
                itemCodeStr = scanResult;
                e2.setText(itemCodeStr);
            }
        }
    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");

            if (intent.getAction().equals(RES_ACTION)){

                if(scanResult.length()>0){
                    itemCodeStr = scanResult;
                    e2.setText(itemCodeStr);
                }
            }
        }
    }

    //GET SCANNED BARCODE
    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] itemcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "itemcode = " + new String(itemcode));
            if (itemcode != null) {
                String itemCodeScan = e2.getText().toString().trim().replace("/", "|");
                itemCodeStr = new String(itemcode);
                e2.setText(itemCodeStr);

            }
        }
    };

    //DEFINE ITEM CODE AND ITEM DESCRIPTION TO NEW INVENTORY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step2);

        b1 = findViewById(R.id.btn_inventory_back2);
        b2 = findViewById(R.id.btn_inventory_next2);
        e1 = findViewById(R.id.editText_Inventory_name);
        e2 = findViewById(R.id.editText_input_itemcode);

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

        //GET INTENT
        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String key2 = intent.getStringExtra("Key2");
        String barcode = intent.getStringExtra("barcode");
        final String name = intent.getStringExtra("name");
        String users = getIntent().getStringExtra("Users");
        String itemCode = intent.getStringExtra("ItemCode");

        setTitle("eStock_Inventory_" + name + "_Step2");

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);

        //BACK BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String key = intent1.getStringExtra("Key");
                String name = intent1.getStringExtra("name");
                Intent intent = new Intent(Inventory_step2.this, Inventory.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        //NEXT BUTTON
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String barcode = intent1.getStringExtra("barcode");
                String name = intent1.getStringExtra("name");
                final String itemName = e1.getText().toString().trim().replace("/", "|");
                final String itemCode = e2.getText().toString().trim().replace("/", "|");
                String barcode_ref = barcode + "/";

                if (itemName.isEmpty()) {
                    Toast.makeText(Inventory_step2.this, "Please enter item name  ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Inventory_step2.this, Inventory_step3.class);
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("Key", key);
                    intent.putExtra("name", name);
                    intent.putExtra("Users", users);
                    intent.putExtra("ItemCode", itemCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}