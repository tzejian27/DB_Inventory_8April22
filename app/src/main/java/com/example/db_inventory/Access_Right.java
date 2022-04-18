package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

public class Access_Right extends AppCompatActivity {
    Switch sw_new_house;
    Switch sw_edit_spec;
    Switch sw_data_clear;
    Switch sw_stock_in;
    Switch sw_stock_out;
    Switch sw_modify_delete;
    Switch sw_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_right);
        setTitle("Access Right");

        sw_new_house=findViewById(R.id.switch_new_house);
        sw_edit_spec=findViewById(R.id.switch_edit_spec);
        sw_data_clear=findViewById(R.id.switch_data_clear);
        sw_stock_in=findViewById(R.id.switch_stock_in);
        sw_stock_out=findViewById(R.id.switch_SO);
        sw_modify_delete=findViewById(R.id.switch_modify_delete);
        sw_setting=findViewById(R.id.switch_setting);



    }

    @Override
    public void onBackPressed() {
        String users=getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent2back = new Intent(getApplicationContext(), Maintain_User.class);
        intent2back.putExtra("Users", users);
        startActivity(intent2back);
    }
}