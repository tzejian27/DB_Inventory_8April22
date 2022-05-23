package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Inventory_step4 extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5;
    EditText e1;
    Button b1, b2;
    String key;
    String key2;
    String barcode;
    String ItemCode;
    String TotalQty;
    String name;
    int sum;
    String ItemName;

    DatabaseReference databaseReference;

    //SHOW ITEM DETAIL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step4);

        t1 = findViewById(R.id.textView_Inventory_TotalQty);
        t2 = findViewById(R.id.textView_Inventory_Qty);
        t3 = findViewById(R.id.textView_Inventory_name);
        t4 = findViewById(R.id.textView_Inventory_price);
        t5 = findViewById(R.id.textView_Inventory_cost);

        e1 = findViewById(R.id.editText_Inventory_barcode);

        b1 = findViewById(R.id.btn_Inventory_esc);
        b2 = findViewById(R.id.btn_Inventory_enter);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        ItemCode = intent1.getStringExtra("ItemCode");

        setTitle("eStock_Inventory_Step4");

        e1.setText(barcode);
        e1.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();
                    ItemName = dataSnapshot.child("ItemName").getValue().toString().trim();
                    String Price = dataSnapshot.child("Price").getValue().toString().trim();
                    String Cost = dataSnapshot.child("Cost").getValue().toString().trim();
                    if (dataSnapshot.child("ItemCode").exists()) {
                        String ItemCode = dataSnapshot.child("ItemCode").getValue().toString().trim();
                    } else {
                        String ItemCode = "-";
                    }

                    // t1.setText(TotalQty);
                    t2.setText(Quantity);
                    t3.setText(ItemName);
                    t4.setText(Price);
                    t5.setText(Cost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TotalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();

                t1.setText(TotalQty);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String users = getIntent().getStringExtra("Users");

        //EXIT BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_step4.this, House_Menu.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                intent.putExtra("Key2", key2);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();

            }
        });

        //ENTER STOKE TAKE
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_step4.this, Inventory_step5.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("name", name);
                intent.putExtra("ItemName", ItemName);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });
    }
}