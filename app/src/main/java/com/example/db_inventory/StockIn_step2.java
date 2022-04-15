package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    Button b1,b2;
    EditText e1,e2,e3;
    DatabaseReference databaseReference,databaseReference2, newGoodRef;
    int TotalQty=0;
    String Quantity="0";
    long maxid=0;
    long k;
    String currentDateandTime;
    String key2;
    String totaltype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_step2);

        b1=(Button)findViewById(R.id.btn_inventory_back2_SI);
        b2=(Button)findViewById(R.id.btn_inventory_next2_SI);

        e1=(EditText)findViewById(R.id.editText_Inventory_name_SI);
        e2=(EditText)findViewById(R.id.editText_Inventory_price_SI);
        e3=(EditText)findViewById(R.id.editText_Inventory_cost_SI);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String name = intent.getStringExtra("name");

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid=(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockIn_step2.this, House_List_Stock_In.class);
                startActivity(intent);
                finish();
            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String barcode = intent.getStringExtra("barcode");
                String itemName = e1.getText().toString().trim();
                String price = e2.getText().toString().trim();
                String cost = e3.getText().toString().trim();
                databaseReference2=FirebaseDatabase.getInstance().getReference("House").child(key).push();
                databaseReference2.keepSynced(true);
                key2 = databaseReference2.getKey();
                k = (maxid-3);

                totaltype =Long.toString(k);

                if(TextUtils.isEmpty(price)){
                    e1.setError("Required Field...");
                    return;
                }

                if(TextUtils.isEmpty(cost)){
                    e2.setError("Required Field...");
                    return;
                }


                Map dataMap = new HashMap();
                dataMap.put("HouseKey",key);
                dataMap.put("Barcode",barcode);
                dataMap.put("ItemName",itemName);
                dataMap.put("Price",price);
                dataMap.put("Cost",cost);
                dataMap.put("Quantity",Quantity);
                dataMap.put("Key",key2);
                dataMap.put("Date_and_Time",currentDateandTime);

                //Store Data to "New_Goods" at the same time
                newGoodRef=FirebaseDatabase.getInstance().getReference("New_Goods").child(barcode);
                newGoodRef.child("Name").setValue(name);
                newGoodRef.child("Price").setValue(price);
                newGoodRef.child("Cost").setValue(cost);
                newGoodRef.child("Barcode").setValue(barcode);


                databaseReference2.updateChildren(dataMap);

                databaseReference.child("TotalType").setValue(totaltype);

                Intent page = new Intent(StockIn_step2.this, StockIn_step3.class);
                page.putExtra("barcode", barcode);
                page.putExtra("Key", key);
                page.putExtra("Key2", key2);
                page.putExtra("name",name);
                startActivity(page);
                finish();



            }
        });

    }
}