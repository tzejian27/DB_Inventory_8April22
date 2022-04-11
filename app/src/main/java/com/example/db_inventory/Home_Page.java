package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Home_Page extends AppCompatActivity implements View.OnClickListener{

    Button btn_home_home, btn_stock_adjustment;
    Button btn_sales_order, btn_maintain_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btn_home_home=findViewById(R.id.btn_House_intent);
        btn_stock_adjustment=findViewById(R.id.btn_stock_adjustment);
        btn_sales_order=findViewById(R.id.btn_sales_order);
        btn_maintain_users=findViewById(R.id.btn_maintain_user);

        btn_home_home.setOnClickListener(this);
        btn_stock_adjustment.setOnClickListener(this);
        btn_sales_order.setOnClickListener(this);
        btn_maintain_users.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_House_intent:
                Intent intent2house = new Intent(Home_Page.this, MainActivity.class);
                startActivity(intent2house);
                break;
            case R.id.btn_stock_adjustment:
                Toast.makeText(getApplicationContext(),"Stock adjustment still under construction",Toast.LENGTH_SHORT).show();
                //Intent intent2stock_adjust = new Intent(Home_Page.this, Stock_Adjustment_Home.class);
                //startActivity(intent2stock_adjust);
                break;
            case R.id.btn_sales_order:
                Toast.makeText(getApplicationContext(),"Sales Order still under construction",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_maintain_user:
                Intent intent2MaintainUser = new Intent(Home_Page.this, Maintain_User.class);
                startActivity(intent2MaintainUser);
                //Toast.makeText(getApplicationContext(),"Maintain User still under construction",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2login = new Intent(Home_Page.this, Login.class);
        startActivity(intent2login);
    }
}