package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class House_Menu extends AppCompatActivity {

    Button b1,b2,b3,b4,b5,b6;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_menu);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        t1 = (TextView)findViewById(R.id.name);
        t1.setText(name);

        b1=(Button)findViewById(R.id.btn_inventory);
        b2=(Button)findViewById(R.id.btn_inventory_list);
        b3=(Button)findViewById(R.id.btn_wireless_export);
        b4=(Button)findViewById(R.id.btn_data_clear);
        b5=(Button)findViewById(R.id.btn_House_Menu_Back);
        b6=(Button)findViewById(R.id.btn_export_Inventory);
        b6.setVisibility(View.INVISIBLE);

        String users=getIntent().getStringExtra("Users");


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Menu.this,Inventory.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key",key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Menu.this,Inventory_List.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key",key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(House_Menu.this, "Coming Soon!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(House_Menu.this,Wireless_Export.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key",key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Menu.this,Inventory_Data_Clear.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key",key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(House_Menu.this, "View At Inventory List!!!", Toast.LENGTH_SHORT).show();

                /*Intent intent = new Intent(House_Menu.this,Generate_Inventory.class);
                intent.putExtra("name", t1.getText());
                intent.putExtra("Key",key);
                startActivity(intent);
                finish();*/
            }
        });
    }
}