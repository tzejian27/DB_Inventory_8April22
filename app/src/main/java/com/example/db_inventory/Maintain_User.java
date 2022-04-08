package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Maintain_User extends AppCompatActivity {

    Button btn_create, btn_access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_user);

        btn_create=findViewById(R.id.btn_create_user);
        btn_access=findViewById(R.id.btn_access_right);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2_create_User=new Intent(getApplicationContext(), Create_User.class);
                startActivity(intent2_create_User);
            }
        });

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2_create_User=new Intent(getApplicationContext(), Access_Right.class);
                startActivity(intent2_create_User);
            }
        });


    }
}