package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class House_Modify extends AppCompatActivity {

    Button b1, b2;
    EditText e1;
    DatabaseReference databaseReference;
    String key, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_modify);

        b1 = findViewById(R.id.btn_House_Modify_Cancel);
        b2 = findViewById(R.id.btn_House_Modify_Enter);

        Intent intent1 = getIntent();
        key = intent1.getStringExtra("Key");
        name = intent1.getStringExtra("name");
        e1 = findViewById(R.id.editText_House_Modify_Name);
        e1.setText(name);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);

        String users = getIntent().getStringExtra("Users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_Modify.this, House_List.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editName = e1.getText().toString().trim();
                databaseReference.child("Name").setValue(editName);//.addOnCompleteListener(new OnCompleteListener<Void>() {
                //   @Override
                //  public void onComplete(@NonNull Task<Void> task) {
                //     if (task.isSuccessful()) {
                Toast.makeText(House_Modify.this, "Modified Successfully ! ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(House_Modify.this, House_List.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
                //     }else{
                //          Toast.makeText(House_Modify.this, "Error .....", Toast.LENGTH_SHORT).show();
                //     }
                //   }
                //  });


            }
        });
    }
}