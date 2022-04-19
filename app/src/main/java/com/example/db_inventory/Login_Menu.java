package com.example.db_inventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Login_Menu extends AppCompatActivity {
    Boolean doubleBackToExitPressedOnce = true;
    Button btn_admin, btn_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        btn_admin = findViewById(R.id.btn_admin_login);
        btn_user = findViewById(R.id.btn_user_login);

        //intent to the admin login page
        //only admin are allowed to make modify on maintain user
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent2login);
            }
        });

        //intent to the user login page
        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2login = new Intent(getApplicationContext(), Login_user.class);
                startActivity(intent2login);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(Login_Menu.this)
                .setTitle("Exit")
                .setCancelable(false)
                .setMessage("Are your sure want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(Login_Menu.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();

        /*Intent intent=new Intent(Login_Menu.this, Login_Menu.class);
        startActivity(intent);
        finish();*/

        /*if(doubleBackToExitPressedOnce){
            // Do what ever you want
            doubleBackToExitPressedOnce = false;
        } else{
            // Do exit app or back press here
            super.onBackPressed();
        }*/
    }
}