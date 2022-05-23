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
    Button btn_admin, btn_user;

    //SELECT TYPE OF USER TO LOGIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        btn_admin = findViewById(R.id.btn_admin_login);
        btn_user = findViewById(R.id.btn_user_login);

        //INTENT TO THE ADMIN LOGIN PAGE
        //ONLY ADMIN ARE ALLOWED TO MAKE MODIFY ON MAINTAIN USER
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent2login);
            }
        });

        //INTENT TO THE USER LOGIN PAGE
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
        //DIALOG ASKING FOR EXIT WHEN USER PRESS ON BACK

        AlertDialog.Builder builder = new AlertDialog.Builder(Login_Menu.this)
                .setTitle("Exit")
                .setCancelable(false)
                .setMessage("Are you sure to exit?")
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
    }
}