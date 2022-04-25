package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//maintain page
//maintain user, setting, add new house
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

        b1 = findViewById(R.id.btn_new_house_M);
        b2 = findViewById(R.id.btn_user_M);
        b3 = findViewById(R.id.btn_setting_M);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        String users = getIntent().getStringExtra("Users");
        switch (view.getId()) {
            case R.id.btn_new_house_M:
                if (users != null && users.equals("Admin")) {
                    Intent intent2new_house = new Intent(Maintain.this, House_New_House.class);
                    intent2new_house.putExtra("Users", users);
                    startActivity(intent2new_house);
                } else if (users.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch1 = snapshot.child("SW_NewHouse").getValue().toString().trim();
                            if (Switch1.equals("On")) {
                                Intent intent2new_house = new Intent(Maintain.this, House_New_House.class);
                                intent2new_house.putExtra("Users", users);
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
                //maintain user are only access by the admin
                if (users != null && users.equals("Admin")) {
                    Intent intent2MaintainUser = new Intent(Maintain.this, Maintain_User.class);
                    intent2MaintainUser.putExtra("Users", users);
                    startActivity(intent2MaintainUser);
                } else {
                    //when there is not admin role received then show error message where not allowed user to enter
                    Toast.makeText(Maintain.this, "You are not authorized to execute, Please Login as admin", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_setting_M:

                if (users != null && users.equals("Admin")) {
                    Intent intent2house_setting = new Intent(Maintain.this, House_Setting.class);
                    intent2house_setting.putExtra("Users", users);
                    startActivity(intent2house_setting);
                } else if (users.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch2 = snapshot.child("SW_Setting").getValue().toString().trim();
                            if (Switch2.equals("On")) {
                                Intent intent2house_setting = new Intent(Maintain.this, House_Setting.class);
                                intent2house_setting.putExtra("Users", users);
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