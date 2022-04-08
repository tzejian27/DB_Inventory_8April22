package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Inventory_step2 extends AppCompatActivity {

    Button b1,b2;
    EditText e1;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step2);

        b1=(Button)findViewById(R.id.btn_inventory_back2);
        b2=(Button)findViewById(R.id.btn_inventory_next2);
        e1=(EditText)findViewById(R.id.editText_Inventory_name);
        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String key2 = intent.getStringExtra("Key2");
        String barcode = intent.getStringExtra("barcode");
        final String name = intent.getStringExtra("name");

        databaseReference= FirebaseDatabase.getInstance().getReference("House").child(key);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String key = intent1.getStringExtra("Key");
                String name = intent1.getStringExtra("name");
                Intent intent = new Intent(Inventory_step2.this,Inventory.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String barcode = intent1.getStringExtra("barcode");
                // String name = intent1.getStringExtra("name");
                final String itemName = e1.getText().toString().trim();
                String barcode_ref = barcode + "/";

                if (itemName.isEmpty()) {
                    Toast.makeText(Inventory_step2.this, "Please enter item name  ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Inventory_step2.this, Inventory_step3.class);
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("Key",key);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}