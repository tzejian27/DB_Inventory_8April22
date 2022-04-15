package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class Search_House extends AppCompatActivity {

    EditText e1;
    Button b1,b2;
    String name ;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_house);

        e1=(EditText)findViewById(R.id.editText_House_Search_name);

        b1=(Button)findViewById(R.id.btn_house_search_cancel);
        b2=(Button)findViewById(R.id.btn_house_search_enter);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String users=getIntent().getStringExtra("Users");


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = e1.getText().toString().trim();

                if (name == null){
                    Toast.makeText(Search_House.this, "Error ....", Toast.LENGTH_SHORT).show();
                }else{
                    Intent page =new Intent(Search_House.this,House_List2.class);
                    page.putExtra("name",name);
                    page.putExtra("Users", users);
                    startActivity(page);
                    finish();
                }
            }
        });
    }
}