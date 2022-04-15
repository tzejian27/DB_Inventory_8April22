package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Access_Right extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_right);
        setTitle("Access Right");

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