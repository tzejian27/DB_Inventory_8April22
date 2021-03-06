package com.example.db_inventory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Item_Spec_Modify extends AppCompatActivity {

    TextView t1;
    EditText e1, e2, e3;
    Button b1, b2;
    DatabaseReference databaseReference, databaseReference2;
    String barcode;
    String itemName;
    String price;
    String cost;

    //MODIFY ITEM SPEC
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_spec_modify);
        setTitle("eStock_Inventory_Modify Spec");

        t1 = findViewById(R.id.textView42);

        e1 = findViewById(R.id.editText_ItemSpec_Modify_name);
        e2 = findViewById(R.id.editText_ItemSpec_Modify_price);
        e3 = findViewById(R.id.editText_ItemSpec_Modify_cost);

        b1 = findViewById(R.id.btn_Inventory_ItemSpec_Modidy_Esc);
        b2 = findViewById(R.id.btn_Inventory_ItemSpec_Modify_Enter);

        //GET INTENT DATA
        final Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        final String key2 = intent.getStringExtra("Key2");
        //final String barcode1=intent.getStringExtra("Barcode");

        //LINK FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key).child(key2);
        databaseReference.keepSynced(true);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barcode = dataSnapshot.child("Barcode").getValue().toString().trim();
                itemName = dataSnapshot.child("ItemName").getValue().toString().trim();
                price = dataSnapshot.child("Price").getValue().toString().trim();
                cost = dataSnapshot.child("Cost").getValue().toString().trim();

                e1.setText(itemName);
                e2.setText(price);
                e3.setText(cost);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //EXIT BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String users = getIntent().getStringExtra("Users");

        //MODIFY CONFIRMATION
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = e1.getText().toString().trim().replace("/", "|");
                String price = e2.getText().toString().trim();
                String cost = e3.getText().toString().trim();

                final Intent intent = getIntent();
                final String barcode1 = intent.getStringExtra("Barcode1");

                if (name.isEmpty() && price.isEmpty() && cost.isEmpty()) {
                    Toast.makeText(Item_Spec_Modify.this, "Please enter name , price and cost ", Toast.LENGTH_SHORT).show();
                } else {
                    //Not sure need or not
                    databaseReference2 = FirebaseDatabase.getInstance().getReference("New_Goods").child(barcode);
                    databaseReference2.keepSynced(true);
                    databaseReference2.child("Name").setValue(name);
                    databaseReference2.child("Price").setValue(price);
                    databaseReference2.child("Cost").setValue(cost);
                    databaseReference2.child("Barcode").setValue(barcode1);
                    databaseReference.child("ItemName").setValue(name);
                    databaseReference.child("Price").setValue(price);
                    databaseReference.child("Cost").setValue(cost);

                    //GET LOGIN PERSON AS RECORD PERSON
                    final DBHandler dbHandler = new DBHandler(getApplicationContext());
                    Cursor cursor = dbHandler.fetch();
                    cursor.moveToLast();
                    String username1 = cursor.getString(1);

                    //UPDATE THE SPEC PRICE ALSO WHEN IT WAS MODIFY
                    databaseReference.child("ItemName").setValue(name);
                    databaseReference.child("Price").setValue(price);
                    databaseReference.child("Cost").setValue(cost);
                    databaseReference.child("User").setValue(username1);


                    Intent page = new Intent(Item_Spec_Modify.this, Item_Spec.class);
                    page.putExtra("Key", key);
                    page.putExtra("Key2", key2);
                    page.putExtra("Users", users);
                    startActivity(page);
                    finish();
                    Toast.makeText(Item_Spec_Modify.this, "Modify Successfully !", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}