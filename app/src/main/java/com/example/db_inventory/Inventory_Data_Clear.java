package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Inventory_Data_Clear extends AppCompatActivity {

    Button b1,b2;
    DatabaseReference databaseReference;
    String totaltype;
    int t_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_data_clear);

        b1=(Button)findViewById(R.id.btn_Inventory_dataClear_Cencel);
        b2=(Button)findViewById(R.id.btn_Inventory_dataClear_Enter);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        databaseReference= FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_Data_Clear.this,House_Menu.class);
                intent.putExtra("name",name);
                intent.putExtra("Key",key);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(key).removeValue();//.addOnCompleteListener(new OnCompleteListener<Void>() {
                // @Override
                // public void onComplete(@NonNull Task<Void> task) {
                //    if (task.isSuccessful()){

                Toast.makeText(Inventory_Data_Clear.this, "Delete Successful ! !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Inventory_Data_Clear.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onBackPressed(){
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        super.onBackPressed();
        Intent page = new Intent(Inventory_Data_Clear.this, House_Menu.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        startActivity(page);
        finish();

    }
}