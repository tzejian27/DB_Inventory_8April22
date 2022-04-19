package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockOut_step3 extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, tv_qtyOut;
    EditText e1, e2_quantity_out;
    Button b1, b2, b3;
    String key;
    String key2;
    String barcode;
    String TotalQty;
    String name;
    int sum;
    String Quantity;
    String totalQty;
    String currentDateandTime;

    DatabaseReference databaseReference, databaseReference2, stockOutRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_step3);

        t1 = (TextView) findViewById(R.id.textView_Inventory_TotalQty_SO);
        t2 = (TextView) findViewById(R.id.textView_Inventory_Qty_SO);
        t3 = (TextView) findViewById(R.id.textView_Inventory_name_SO);
        t4 = (TextView) findViewById(R.id.textView_Inventory_price_SO);
        t5 = (TextView) findViewById(R.id.textView_Inventory_cost_SO);
        tv_qtyOut = (TextView) findViewById(R.id.textView_quantity_in_SO);

        e1 = (EditText) findViewById(R.id.editText_Inventory_barcode_SO);
        e2_quantity_out = (EditText) findViewById(R.id.editText_Inventory_step5_Qty_SO);

        b1 = (Button) findViewById(R.id.btn_Inventory_esc_SO);
        b2 = (Button) findViewById(R.id.btn_Inventory_enter_SO);
        b3 = (Button) findViewById(R.id.btn_back_SO);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");

        e1.setText(barcode);
        e1.setEnabled(false);

        stockOutRef= FirebaseDatabase.getInstance().getReference("StockOut");

        databaseReference2 = FirebaseDatabase.getInstance().getReference("House").child(key).child(key2);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();
                if (dataSnapshot != null && dataSnapshot.child("QtyOut").exists()) {
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

        b1.setOnClickListener(new View.OnClickListener() {
            String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StockOut_step3.this, Stock_Out_Scan.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("name", name);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockOut_step3.this, Home_Page.class);
                intent.putExtra("Users", users);
                startActivity(intent);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View v) {
                final String qty = e2_quantity_out.getText().toString().trim();


                if (qty.isEmpty()) {
                    Toast.makeText(StockOut_step3.this, "Please enter Qty out", Toast.LENGTH_SHORT).show();
                } else {
                    final int total = Integer.parseInt(qty);
                    final int qty1 = Integer.parseInt(Quantity);
                    int sum = qty1 - total;
                    String sum2 = String.valueOf(sum);
                    databaseReference.child(key2).child("Quantity").setValue(sum2);//.addOnCompleteListener(new OnCompleteListener<Void>() {
                    //   @Override
                    //    public void onComplete(@NonNull Task<Void> task) {
                    //       if (task.isSuccessful()){
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();

                            int sum = 0;
                            int totalQty1 = Integer.parseInt(totalQty);
                            sum = totalQty1 - total;
                            String sum2 = String.valueOf(sum);

                            databaseReference.child("TotalQty").setValue(sum2).toString().trim();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                            currentDateandTime = sdf.format(new Date());

                            Map dataMap = new HashMap();
                            dataMap.put("QtyOut", qty);
                            dataMap.put("QtyOut_Date", currentDateandTime);

                            databaseReference2.updateChildren(dataMap);

                            tv_qtyOut.setText(qty);
                            e2_quantity_out.setEnabled(false);
                            b2.setClickable(false);


                            String Quantity = dataSnapshot.child(key2).child("Quantity").getValue().toString().trim();
                            t1.setText(sum2);
                            t2.setText(Quantity);

                            String ItemName = dataSnapshot.child(key2).child("ItemName").getValue().toString().trim();


                            //Create stock in record
                            barcode = intent1.getStringExtra("barcode");
                            name = intent1.getStringExtra("name");
                            String barcode_ref=barcode + "/";
                            Map dataMap2 = new HashMap();
                            dataMap2.put("Barcode",barcode);
                            dataMap2.put("Name",ItemName);
                            dataMap2.put("QtyOut", qty);
                            dataMap2.put("QtyOut_Date", currentDateandTime);

                            Map dataMap3=new HashMap();
                            dataMap3.put(barcode_ref+"/", dataMap2);

                            stockOutRef.updateChildren(dataMap3);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //   }
                    //     }
                    //    });
                    Toast.makeText(StockOut_step3.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(StockOut_step3.this, Home_Page.class);
        intent.putExtra("Users", users);
        startActivity(intent);
    }
}