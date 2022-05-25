package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GRN_Home extends AppCompatActivity implements View.OnClickListener {

    Button btn_create_grn, btn_grn_list, btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grn_home);
        setTitle("GRN HOME");

        //DECLARE AND LINK WITH BUTTON
        btn_create_grn = findViewById(R.id.btn_make_grn);
        btn_grn_list = findViewById(R.id.btn_grn_list);
        btn_back = findViewById(R.id.btn_grn_back);

        btn_create_grn.setOnClickListener(this);
        btn_grn_list.setOnClickListener(this);
        btn_back.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //INTENT TO CREATE GRN PAGE
            case R.id.btn_make_grn:
                Intent intent2CreateGRN = new Intent(getApplicationContext(), NewGRN.class);
                startActivity(intent2CreateGRN);
                break;

            //INTENT TO GRN LIST PAGE
            case R.id.btn_grn_list:
                Intent intent2GRN_list = new Intent(getApplicationContext(), GRNList.class);
                startActivity(intent2GRN_list);
                break;

            //INTENT TO GRN BACK PAGE
            case R.id.btn_grn_back:
                Intent intent2GRN_back = new Intent(getApplicationContext(), Home_Page.class);
                startActivity(intent2GRN_back);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent2GRN_back = new Intent(getApplicationContext(), Home_Page.class);
        startActivity(intent2GRN_back);
    }
}