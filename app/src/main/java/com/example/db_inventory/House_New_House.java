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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("House").push();
        myRef.keepSynced(true);

        edt_house_name = findViewById(R.id.editText_house_name);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_enter = findViewById(R.id.btn_enter);

        String users = getIntent().getStringExtra("Users");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2home = new Intent(House_New_House.this, MainActivity.class);
                intent2home.putExtra("Users", users);
                startActivity(intent2home);
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_house_name.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(House_New_House.this, "Please enter Name ", Toast.LENGTH_SHORT).show();
                } else {
                    add();
                }
            }
        });

    }

    private void add() {
        String name = edt_house_name.getText().toString().trim();
        String users = getIntent().getStringExtra("Users");
        myRef.child("Name").orderByChild("Name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // for (DataSnapshot data : dataSnapshot.getChildren()) {
                // String name=d,ataSnapshot.child("House_Name").getValue().toString();


                final String key = myRef.getKey();
                String name = edt_house_name.getText().toString().trim();
                // String house_ref=name + "/";
                if (dataSnapshot.exists()) {
                    Toast.makeText(House_New_House.this, "Data Exists !", Toast.LENGTH_SHORT).show();
                } else {
                    Map dataMap = new HashMap();
                    dataMap.put("Name", name);
                    dataMap.put("TotalQty", TotalQty);
                    dataMap.put("TotalType", TotalType);
                    dataMap.put("Key", key);


                    // Map dataMap2 = new HashMap();
                    //dataMap2.put(house_ref + "/" ,dataMap);

                    myRef.updateChildren(dataMap);//.addOnCompleteListener(new OnCompleteListener() {
                    //  @Override
                    //   public void onComplete(@NonNull Task task) {
                    //      if(task.isSuccessful()){
                    //   String name = e1.getText().toString().trim();
                    Intent intent = new Intent(House_New_House.this, House_Menu.class);
                    intent.putExtra("name", name);
                    intent.putExtra("Key", key);
                    intent.putExtra("Users", users);
                    startActivity(intent);
                    finish();
                    //         }
                    //     }
                    //   });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}