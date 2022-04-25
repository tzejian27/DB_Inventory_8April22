package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Item_Spec extends AppCompatActivity {

    TextView t1, t2;
    Button b1, b2;
    String s1;
    DatabaseReference databaseReference, databaseReference2;

    //Show item spec
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_spec);

        t1 = findViewById(R.id.textView_Item_Spec_price);
        t2 = findViewById(R.id.textView_Item_Spec_cost);

        b1 = findViewById(R.id.btn_Item_Spec_Esc);
        b2 = findViewById(R.id.btn_Item_Spec_Enter);

        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String key2 = intent.getStringExtra("Key2");
        final String barcode = intent.getStringExtra("Barcode");
        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String price = dataSnapshot.child("Price").getValue().toString().trim();
                final String cost = dataSnapshot.child("Cost").getValue().toString().trim();

                t1.setText(price);
                t2.setText(cost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String users = getIntent().getStringExtra("Users");


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Item_Spec.this, Inventory_List.class);
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                //  startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Item_Spec.this, Item_Spec_Modify.class);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("Barcode1", barcode);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });
    }
}