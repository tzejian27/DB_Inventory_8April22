package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Inventory_Delete_Confirm extends AppCompatActivity {

    TextView t1;
    Button b1, b2;
    DatabaseReference databaseReference;
    String totaltype;
    String Qty;
    String totalQty;
    int t_qty;
    int t_type;
    int Qty1;
    int sum;
    String sum2;
    int sum3;
    String sum4;

    //COMMUNICATE WITH INVENTORY_DATA_CLEAR FOR MAKING DATA CLEAR CONFIRMATION
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_delete_confirm);

        b1 = findViewById(R.id.btn_Inventory_dataClear_Cencel);
        b2 = findViewById(R.id.btn_Inventory_dataClear_Enter);

        t1 = findViewById(R.id.textView22);
        t1.setText("Delete this Inventory ? ");

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        final String key2 = intent.getStringExtra("Key2");

        setTitle("eStock_Delete_" + name);

        //LINK FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totaltype = dataSnapshot.child("TotalType").getValue().toString().trim();
                totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Qty = dataSnapshot.child("Quantity").getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //CANCEL BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String users = getIntent().getStringExtra("Users");
                t_type = Integer.parseInt(totaltype);
                sum = t_type - 1;
                sum2 = Integer.toString(sum);

                Qty1 = Integer.parseInt(Qty);
                t_qty = Integer.parseInt(totalQty);
                sum3 = t_qty - Qty1;
                sum4 = Integer.toString(sum3);

                databaseReference.child("TotalType").setValue(sum2);
                databaseReference.child("TotalQty").setValue(sum4);
                databaseReference.child(key2).removeValue();
                Toast.makeText(Inventory_Delete_Confirm.this, "Delete Successful ! !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Inventory_Delete_Confirm.this, Inventory_List.class);
                intent.putExtra("name", name);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();


            }
        });
    }
}