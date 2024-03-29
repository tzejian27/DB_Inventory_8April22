package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class House_Menu extends AppCompatActivity {

    Button b1, b2, b3, b4, b5, b6;
    TextView t1;

    DatabaseReference arightRef;
    String Switch2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_menu);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        setTitle("eStock_House Menu_" + name);

        t1 = findViewById(R.id.name);
        t1.setText(name);

        b1 = findViewById(R.id.btn_inventory);
        b2 = findViewById(R.id.btn_inventory_list);
        b3 = findViewById(R.id.btn_export_inventory_HM);
        b4 = findViewById(R.id.btn_data_clear);
        b4.setText("Delete House");
        b5 = findViewById(R.id.btn_House_Menu_Back);
        b6 = findViewById(R.id.btn_export_Inventory);

        String users = getIntent().getStringExtra("Users");
        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");

        //ADD INVENTORY
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Menu.this, Inventory.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        //INVENTORY LIST
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Menu.this, Inventory_List.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        //GENERATE INVENTORY EXCEL
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Menu.this, Wireless_Export.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        //DATA CLEAR/DELETE
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Switch2 = snapshot.child("SW_DataClear").getValue().toString().trim();
                        if (Switch2.equals("On")) {
                            Intent intent = new Intent(House_Menu.this, Inventory_Data_Clear.class);
                            intent.putExtra("name", t1.getText());
                            intent.putExtra("Key", key);
                            intent.putExtra("Users", users);
                            startActivity(intent);
                            finish();
                        } else if (Switch2.equals("Off")) {
                            Toast.makeText(House_Menu.this, "Permission denied", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //BACK BUTTON
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        //Export the inventory that with batch number
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new exportInventoryByBatch(House_Menu.this, name);
            }
        });
    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(House_Menu.this, House_List.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();
    }
}