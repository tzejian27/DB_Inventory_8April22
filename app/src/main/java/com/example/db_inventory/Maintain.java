package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//MAINTAIN PAGE
//MAINTAIN USER, SETTING, ADD NEW HOUSE
public class Maintain extends AppCompatActivity implements View.OnClickListener{

    Button b1, b2, b3;
    DatabaseReference arightRef;
    String Switch1;
    String Switch2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");
        setTitle("eStock_Maintain");

        //DECLARE AND LINKING THE BUTTON
        b1 = findViewById(R.id.btn_new_house_M);
        b2 = findViewById(R.id.btn_user_M);
        b3 = findViewById(R.id.btn_setting_M);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        //GET THE USER ROLE
        final DBHandler dbHandler = new DBHandler(this);
        Cursor cursor = dbHandler.fetch();
        cursor.moveToLast();
        String role = cursor.getString(2);

        switch (view.getId()) {
            //ADMIN ALWAYS ALLOWED TO ENTER
            //CHECKING ADD NEW HOUSE'S ACCESS RIGHT
            case R.id.btn_new_house_M:
                if (role != null && role.equals("Admin")) {
                    Intent intent2new_house = new Intent(Maintain.this, House_New_House.class);
                    intent2new_house.putExtra("Users", role);
                    startActivity(intent2new_house);
                } else if (role.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch1 = snapshot.child("SW_NewHouse").getValue().toString().trim();
                            if (Switch1.equals("On")) {
                                Intent intent2new_house = new Intent(Maintain.this, House_New_House.class);
                                intent2new_house.putExtra("Users", role);
                                startActivity(intent2new_house);
                            } else if (Switch1.equals("Off")) {
                                Toast.makeText(Maintain.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(Maintain.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }


                break;

            case R.id.btn_user_M:
                //MAINTAIN USER ARE ONLY ACCESS BY THE ADMIN
                if (role != null && role.equals("Admin")) {
                    Intent intent2MaintainUser = new Intent(Maintain.this, Maintain_User.class);
                    intent2MaintainUser.putExtra("Users", role);
                    startActivity(intent2MaintainUser);
                } else {
                    //WHEN THERE IS NOT ADMIN ROLE RECEIVED THEN SHOW ERROR MESSAGE WHERE NOT ALLOWED USER TO ENTER
                    Toast.makeText(Maintain.this, "You are not authorized to execute, Please Login as admin", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_setting_M:
                //ENTER SETTING PAGE
                if (role != null && role.equals("Admin")) {
                    Intent intent2house_setting = new Intent(Maintain.this, House_Setting.class);
                    intent2house_setting.putExtra("Users", role);
                    startActivity(intent2house_setting);
                } else if (role.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch2 = snapshot.child("SW_Setting").getValue().toString().trim();
                            if (Switch2.equals("On")) {
                                Intent intent2house_setting = new Intent(Maintain.this, House_Setting.class);
                                intent2house_setting.putExtra("Users", role);
                                startActivity(intent2house_setting);
                            } else if (Switch2.equals("Off")) {
                                Toast.makeText(Maintain.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(Maintain.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }

                break;

        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        String users = getIntent().getStringExtra("Users");
        Intent intent2homepage = new Intent(getApplicationContext(), Home_Page.class);
        intent2homepage.putExtra("Users", users);
        startActivity(intent2homepage);
    }


}