package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class Inventory_Data_Clear extends AppCompatActivity {

    Button b1, b2;
    DatabaseReference databaseReference;
    String totaltype;
    int t_type;
    TextView t1, t2;

    //HOUSE CLEAR PAGES
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_data_clear);

        b1 = findViewById(R.id.btn_Inventory_dataClear_Cencel);
        b2 = findViewById(R.id.btn_Inventory_dataClear_Enter);

        t1 = findViewById(R.id.textView22);
        t2 = findViewById(R.id.textView24);

        //GET INTENT DATA
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        //CONNECT FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);

        //SET TITLE
        setTitle("eStock_Data Clear_" + name);
        t1.setText("Delete House (" + name + ") ?");
        t2.setText("Delete House");

        String users = getIntent().getStringExtra("Users");

        //CANCEL DATA CLEAR
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_Data_Clear.this, House_Menu.class);
                intent.putExtra("name", name);
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        //CONFIRM DATA CLEAR
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(key).removeValue();
                Toast.makeText(Inventory_Data_Clear.this, "Delete Successful ! !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Inventory_Data_Clear.this, MainActivity.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onBackPressed() {
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        String users = getIntent().getStringExtra("Users");

        super.onBackPressed();
        Intent page = new Intent(Inventory_Data_Clear.this, House_Menu.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        page.putExtra("Users", users);
        startActivity(page);
        finish();

    }
}