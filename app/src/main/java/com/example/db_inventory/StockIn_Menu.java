package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StockIn_Menu extends AppCompatActivity implements View.OnClickListener {

    Button btn_scan, btn_rfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_menu);
        setTitle("Stock In Menu");

        btn_scan = findViewById(R.id.btn_scan_stockin_menu);
        btn_rfid = findViewById(R.id.btn_rfid_stockin_menu);

        btn_scan.setOnClickListener(this);
        btn_rfid.setOnClickListener(this);
        //Disable RFID on 22/12/2022
//        btn_rfid.setEnabled(false);
//        btn_rfid.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BBBBBB")));

    }

    @Override
    public void onClick(View view) {
        onStart();
        //GET DATA FROM "House_List_Stock_In"
        String key = getIntent().getStringExtra("Key");
        String name = getIntent().getStringExtra("name");
        String users = getIntent().getStringExtra("Users");
        String totalqtyh = getIntent().getStringExtra("TotalQtyH");

        switch (view.getId()) {
            case R.id.btn_scan_stockin_menu:
                //ENTER STOCK IN OF SELECTED HOUSE WITH SCAN BARCODE
                Intent intent = new Intent(getApplicationContext(), Stock_In_Scan.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                intent.putExtra("Users", users);
                Toast.makeText(getApplicationContext(), "Enter " + name + " with scan", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;

            case R.id.btn_rfid_stockin_menu:
                //ENTER STOCK IN OF SELECTED HOUSE WITH RFID
                Intent intent2rfid = new Intent(getApplicationContext(), Stock_In_Scan_RFID.class);
                intent2rfid.putExtra("Key", key);
                intent2rfid.putExtra("name", name);
                intent2rfid.putExtra("Users", users);
                intent2rfid.putExtra("TotalQtyH", totalqtyh);
                Toast.makeText(getApplicationContext(), "Enter " + name + " with RFID", Toast.LENGTH_SHORT).show();
                startActivity(intent2rfid);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent2back = new Intent(this, House_List_Stock_In.class);
        intent2back.putExtra("Users", users);
        startActivity(intent2back);
        finish();
    }
}