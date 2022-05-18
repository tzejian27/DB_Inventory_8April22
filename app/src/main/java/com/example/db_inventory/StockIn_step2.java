package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockIn_step2 extends AppCompatActivity {

    Button b1, b2;
    EditText e1, e2, e3, e4;
    DatabaseReference databaseReference, databaseReference2, newGoodRef;
    String Quantity = "0";
    long maxid = 0;
    long k;
    String currentDateandTime;
    String key2;
    String totaltype;

    ScanReader scanReader;
    private String itemCodeStr;
    public static String itemcode;

    //set scan input to edit text
    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] itemcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "itemcode = " + new String(itemcode));
            if (itemcode != null) {
                String itemCodeScan = e4.getText().toString().trim().replace("/", "|");
                itemCodeStr = new String(itemcode);
                e4.setText(itemCodeStr);

            }
        }
    };

    //Stock in item detail
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_step2);

        b1 = findViewById(R.id.btn_inventory_back2_SI);
        b2 = findViewById(R.id.btn_inventory_next2_SI);

        e1 = findViewById(R.id.editText_Inventory_name_SI);
        e2 = findViewById(R.id.editText_Inventory_price_SI);
        e3 = findViewById(R.id.editText_Inventory_cost_SI);
        e4 = findViewById(R.id.editText_Inventory_itemcode_SI);

        setTitle("eStock_Stock In ");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        //Barcode Scanning
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String name = intent.getStringExtra("name");

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    maxid = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String users = getIntent().getStringExtra("Users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockIn_step2.this, House_List_Stock_In.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String barcode = intent.getStringExtra("barcode");
                String itemName = e1.getText().toString().trim().replace("/", "|");
                String price = e2.getText().toString().trim();
                String cost = e3.getText().toString().trim();
                String itemcode = e4.getText().toString().trim().replace("/", "|");
                databaseReference2 = FirebaseDatabase.getInstance().getReference("House").child(key).push();
                databaseReference2.keepSynced(true);
                key2 = databaseReference2.getKey();
                k = (maxid - 3);

                totaltype = Long.toString(k);

                if (TextUtils.isEmpty(itemName)) {
                    e1.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(price)) {
                    e2.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(cost)) {
                    e3.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(itemcode)) {
                    e4.setText("");
                    return;
                }

                //RECORD PERSON INSERT
                final DBHandler dbHandler = new DBHandler(getApplicationContext());
                Cursor cursor = dbHandler.fetch();
                cursor.moveToLast();
                String username1 = cursor.getString(1);

                //SAVE TO HOUSE
                Map dataMap = new HashMap();
                dataMap.put("HouseKey", key);
                dataMap.put("Barcode", barcode);
                dataMap.put("ItemName", itemName);
                dataMap.put("Price", price);
                dataMap.put("Cost", cost);
                dataMap.put("ItemCode", itemcode);
                dataMap.put("Quantity", Quantity);
                dataMap.put("Key", key2);
                dataMap.put("ItemCode", itemcode);
                dataMap.put("Date_and_Time", currentDateandTime);
                dataMap.put("User", username1);

                //Store Data to "New_Goods" at the same time
                newGoodRef = FirebaseDatabase.getInstance().getReference("New_Goods").child(barcode);
                newGoodRef.child("Name").setValue(itemName);
                newGoodRef.child("Price").setValue(price);
                newGoodRef.child("Cost").setValue(cost);
                newGoodRef.child("Barcode").setValue(barcode);
                newGoodRef.child("ItemCode").setValue(itemcode);


                databaseReference2.updateChildren(dataMap);

                databaseReference.child("TotalType").setValue(totaltype);

                String users = getIntent().getStringExtra("Users");

                Intent page = new Intent(StockIn_step2.this, StockIn_step3.class);
                page.putExtra("barcode", barcode);
                page.putExtra("Key", key);
                page.putExtra("Key2", key2);
                page.putExtra("name", name);
                page.putExtra("Users", users);
                page.putExtra("ItemCode", itemcode);
                startActivity(page);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(StockIn_step2.this, House_List_Stock_In.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();
    }
}