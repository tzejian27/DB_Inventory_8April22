package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Stock_Out_Success_View extends AppCompatActivity {
    TextView t1, t2, t3, t4, t5, tv_qtyOut;
    EditText e1;
    Button btn_esc;
    String key;
    String key2;
    String barcode;
    String itemcode;
    String TotalQty;
    String name;
    String Quantity;
    String totalQty;

    DatabaseReference databaseReference, databaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_success_view);



        t1 = findViewById(R.id.textView_Inventory_TotalQty_SO_RFID);
        t2 = findViewById(R.id.textView_Inventory_Qty_SO_RFID);
        t3 = findViewById(R.id.textView_Inventory_name_SO_RFID);
        t4 = findViewById(R.id.textView_Inventory_price_SO_RFID);
        t5 = findViewById(R.id.textView_Inventory_cost_SO_RFID);
        tv_qtyOut = findViewById(R.id.textView_quantity_out_SO_RFID);

        e1 = findViewById(R.id.editText_Inventory_barcode_SO_RFID);

        btn_esc = findViewById(R.id.btn_back_SO_RFID);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        itemcode = intent1.getStringExtra("ItemCode");

        e1.setText(barcode);
        e1.setEnabled(false);

        setTitle("eStock_Stock Out_RFID_" + barcode);

        databaseReference2 = FirebaseDatabase.getInstance().getReference("House").child(key).child(key2);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();
                if (dataSnapshot != null && dataSnapshot.child("QtyIn").exists()) {
                    String Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();
                    String Name = dataSnapshot.child("ItemName").getValue().toString().trim();
                    String Price = dataSnapshot.child("Price").getValue().toString().trim();
                    String Cost = dataSnapshot.child("Cost").getValue().toString().trim();
                    String Quantity_Out = dataSnapshot.child("QtyOut").getValue().toString().trim();

                    // t1.setText(TotalQty);
                    t2.setText(Quantity);
                    t3.setText(Name);
                    t4.setText(Price);
                    t5.setText(Cost);
                    tv_qtyOut.setText(Quantity_Out);
                } else {
                    String Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();
                    String Name = dataSnapshot.child("ItemName").getValue().toString().trim();
                    String Price = dataSnapshot.child("Price").getValue().toString().trim();
                    String Cost = dataSnapshot.child("Cost").getValue().toString().trim();

                    // t1.setText(TotalQty);
                    t2.setText(Quantity);
                    t3.setText(Name);
                    t4.setText(Price);
                    t5.setText(Cost);
                    tv_qtyOut.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TotalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();
                t1.setText(TotalQty);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_esc.setOnClickListener(new View.OnClickListener() {
            final String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), House_List_Stock_Out.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), House_List_Stock_Out.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }

}