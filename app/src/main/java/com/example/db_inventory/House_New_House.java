package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class House_New_House extends AppCompatActivity {

    EditText edt_house_name;
    Button btn_cancel, btn_enter;
    DatabaseReference myRef;
    String TotalQty = "0";
    String TotalType = "0";

    //Adding new house
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_new_house);
        setTitle("eStock_Create New House");

        myRef = FirebaseDatabase.getInstance().getReference("House");
        myRef.keepSynced(true);

        edt_house_name = findViewById(R.id.editText_house_name);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_enter = findViewById(R.id.btn_enter);

        String users = getIntent().getStringExtra("Users");

        //CANCEL/EXIT "ADD NEW HOUSE"
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2home = new Intent(House_New_House.this, MainActivity.class);
                intent2home.putExtra("Users", users);
                startActivity(intent2home);
                finish();
            }
        });

        //ADD NEW HOUSE
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_house_name.getText().toString().trim().replace("/", "|");
                if (name.isEmpty()) {
                    Toast.makeText(House_New_House.this, "Please enter Name ", Toast.LENGTH_SHORT).show();
                } else {
                    add();
                }
            }
        });

    }

    private void add() {
        String name = edt_house_name.getText().toString().trim().replace("/", "|");
        String users = getIntent().getStringExtra("Users");
        myRef.orderByChild("Name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String key = myRef.getKey();
                String name = edt_house_name.getText().toString().trim().replace("/", "|");

                //CHECK IS HOUSE ALREADY EXIST
                if (dataSnapshot.exists()) {
                    Toast.makeText(House_New_House.this, "House Already Exists !", Toast.LENGTH_SHORT).show();
                } else {
                    Map dataMap = new HashMap();
                    dataMap.put("Name", name);
                    dataMap.put("TotalQty", TotalQty);
                    dataMap.put("TotalType", TotalType);
                    dataMap.put("Key", key);

                    String key_ref = key + "/";
                    Map dataMap2 = new HashMap();
                    dataMap2.put(key_ref + "/", dataMap);

                    //ADDING THE NEW HOUSE WHEN HOUSE NOT EXISTED
                    myRef.updateChildren(dataMap2);
                    Intent intent = new Intent(House_New_House.this, House_Menu.class);
                    intent.putExtra("name", name);
                    intent.putExtra("Key", key);
                    intent.putExtra("Users", users);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}