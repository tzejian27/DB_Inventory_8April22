package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Search extends AppCompatActivity {
    Button b1,b2;
    EditText e1 ;
    String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        b1=(Button)findViewById(R.id.btn_Search_Cancel);
        b2=(Button)findViewById(R.id.btn_Search_Enter);

        e1=(EditText)findViewById(R.id.editText_Search_barcode);





        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                final String name = intent.getStringExtra("name");
                final String key = intent.getStringExtra("Key");
                Intent page = new Intent(Search.this,Inventory_List.class);
                page.putExtra("name",name);
                page.putExtra("Key",key);
                startActivity(page);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode = e1.getText().toString().trim();
                Intent intent = getIntent();
                final String name = intent.getStringExtra("name");
                final String key = intent.getStringExtra("Key");
                final String key2 = intent.getStringExtra("Key2");
                Intent page = new Intent(Search.this,Inventory_List2.class);
                page.putExtra("name",name);
                page.putExtra("Barcode",barcode);
                page.putExtra("Key",key);
                page.putExtra("Key2",key2);
                startActivity(page);
                finish();
            }
        });
    }
}