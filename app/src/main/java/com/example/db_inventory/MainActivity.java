package com.example.db_inventory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_new_house, btn_house_list, btn_house_delete, btn_generate_txt, btn_new_goods;
    DatabaseReference databaseReference, databaseReference2;
    String s1;

    DatabaseReference arightRef;
    String Switch1;
    String Switch3;

    //Stoke take menu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("eStock_Stock Take Menu");

        //Get root data from firebase as reference
        databaseReference = FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);

        //Create the buttons and set their onClickListener to "this"
        btn_new_house = findViewById(R.id.btn_new_house);
        btn_house_list = findViewById(R.id.btn_house_list);
        btn_house_delete = findViewById(R.id.btn_house_delete);
        btn_generate_txt = findViewById(R.id.btn_gen_txt);
        btn_new_goods = findViewById(R.id.btn_new_goods);

        btn_new_house.setOnClickListener(this);
        btn_house_list.setOnClickListener(this);
        btn_house_delete.setOnClickListener(this);
        btn_generate_txt.setOnClickListener(this);
        btn_new_goods.setOnClickListener(this);
        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");


    }

    @Override
    public void onClick(View view) {
        final DBHandler dbHandler = new DBHandler(this);
        Cursor cursor = dbHandler.fetch();
        cursor.moveToLast();
        String role = cursor.getString(2);

        switch (view.getId()) {
            case R.id.btn_new_house:
                if (role != null && role.equals("Admin")) {
                    Intent intent2new_house = new Intent(MainActivity.this, House_New_House.class);
                    intent2new_house.putExtra("Users", role);
                    intent2new_house.putExtra("from", "StockTake");
                    startActivity(intent2new_house);
                } else if (role.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch1 = snapshot.child("SW_NewHouse").getValue().toString().trim();
                            if (Switch1.equals("On")) {
                                Intent intent2new_house = new Intent(MainActivity.this, House_New_House.class);
                                intent2new_house.putExtra("Users", role);
                                startActivity(intent2new_house);
                            } else if (Switch1.equals("Off")) {
                                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }


                break;
            case R.id.btn_house_list:
                if (role != null && role.equals("Admin")) {
                    Intent intent2house_list = new Intent(MainActivity.this, House_List.class);
                    intent2house_list.putExtra("Users", role);
                    startActivity(intent2house_list);
                } else if (role.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch3 = snapshot.child("SW_HouseList").getValue().toString().trim();
                            if (Switch3.equals("On")) {
                                Intent intent2house_list = new Intent(MainActivity.this, House_List.class);
                                intent2house_list.putExtra("Users", role);
                                startActivity(intent2house_list);
                            } else if (Switch3.equals("Off")) {
                                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.btn_house_delete:
                Toast.makeText(this, "New Function will be Update in the future", Toast.LENGTH_LONG).show();
                break;
            //Intent intent2house_delete = new Intent(MainActivity.this, House_Delete.class);
            //startActivity(intent2house_delete);
            case R.id.btn_gen_txt:

                Intent intent2house_generate = new Intent(MainActivity.this, Generate_Txt.class);
                intent2house_generate.putExtra("Users", role);
                startActivity(intent2house_generate);
                //Toast.makeText(this, "Coming Soon!!!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_new_goods:

                databaseReference2 = FirebaseDatabase.getInstance().getReference("Switch");
                databaseReference2.keepSynced(true);
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        s1 = dataSnapshot.child("Disable").getValue().toString().trim();

                        if (s1.equals("Switch_On")) {
                            Toast.makeText(MainActivity.this, "New Good is Disable", Toast.LENGTH_SHORT).show();
                        } else if (s1.equals("Enable")) {
                            Intent intent2new_goods = new Intent(MainActivity.this, House_New_Goods.class);
                            intent2new_goods.putExtra("Users", role);
                            startActivity(intent2new_goods);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                break;
        }
    }

    public void onBackPressed() {
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        String users = getIntent().getStringExtra("Users");

        super.onBackPressed();
        Intent page = new Intent(MainActivity.this, Home_Page.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        page.putExtra("Users", users);
        startActivity(page);
        finish();
    }
}