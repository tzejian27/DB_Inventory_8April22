package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class Inventory_step3 extends AppCompatActivity {

    Button b1, b2;
    EditText e1, e2;
    DatabaseReference databaseReference, databaseReference2, newGoodRef;
    int TotalQty = 0;
    String Quantity = "0";
    long maxid = 0;
    long k;
    String currentDateandTime;
    String key2;
    String totaltype;

    //Item cost and price for new good added when adding inventory
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step3);

        b1 = findViewById(R.id.btn_back3);
        b2 = findViewById(R.id.btn_next3);
        e1 = findViewById(R.id.editText_Inventory_price);
        e2 = findViewById(R.id.editText_Inventory_cost);


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String name = intent.getStringExtra("name");
        final String barcode = intent.getStringExtra("barcode");
        final String itemCode = intent.getStringExtra("ItemCode");
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
                Intent intent = new Intent(Inventory_step3.this, Inventory_step2.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                intent.putExtra("barcode", barcode);
                intent.putExtra("Users", users);
                intent.putExtra("ItemCode", itemCode);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String barcode = intent.getStringExtra("barcode");
                String itemName = intent.getStringExtra("itemName");
                String price = e1.getText().toString().trim();
                String cost = e2.getText().toString().trim();
                databaseReference2 = FirebaseDatabase.getInstance().getReference("House").child(key).push();
                databaseReference2.keepSynced(true);
                key2 = databaseReference2.getKey();
                k = (maxid - 3);

                totaltype = Long.toString(k);

                if (TextUtils.isEmpty(price)) {
                    e1.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(cost)) {
                    e2.setError("Required Field...");
                    return;
                }


                Map dataMap = new HashMap();
                dataMap.put("HouseKey", key);
                dataMap.put("Barcode", barcode);
                dataMap.put("ItemName", itemName);
                dataMap.put("Price", price);
                dataMap.put("Cost", cost);
                dataMap.put("Quantity", Quantity);
                dataMap.put("Key", key2);
                dataMap.put("ItemCode", itemCode);
                dataMap.put("Date_and_Time", currentDateandTime);

                //Store Data to "New_Goods" at the same time
                newGoodRef = FirebaseDatabase.getInstance().getReference("New_Goods").child(barcode);
                newGoodRef.child("Name").setValue(itemName);
                newGoodRef.child("Price").setValue(price);
                newGoodRef.child("Cost").setValue(cost);
                newGoodRef.child("Barcode").setValue(barcode);
                newGoodRef.child("ItemCode").setValue(itemCode);


                databaseReference2.updateChildren(dataMap);

                databaseReference.child("TotalType").setValue(totaltype);

                Intent page = new Intent(Inventory_step3.this, Inventory_step4.class);
                page.putExtra("barcode", barcode);
                page.putExtra("Key", key);
                page.putExtra("Key2", key2);
                page.putExtra("name", itemName);
                page.putExtra("Users", users);
                startActivity(page);
                finish();


            }
        });
    }
}