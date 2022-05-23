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

import androidx.appcompat.app.AppCompatActivity;

public class Search extends AppCompatActivity {
    //Barcode
    public static String barcode;
    Button b1, b2;
    EditText e1;
    ScanReader scanReader;
    private String barcodeStr;

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