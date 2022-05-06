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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockIn_step3 extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, tv_qtyIn;
    EditText e1, e2_quantity_in;
    Button btn_esc, btn_enter, btn_back;
    String key;
    String key2;
    String barcode;
    String itemcode;
    String TotalQty;
    String name;
    int sum;
    String Quantity;
    String totalQty;
    String currentDateandTime, currentDateandTime2;

    DatabaseReference databaseReference, databaseReference2;
    DatabaseReference stockInOutRef;

    //Stock in qty in and get the detail display
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_step3);

        t1 = findViewById(R.id.textView_Inventory_TotalQty_SI);
        t2 = findViewById(R.id.textView_Inventory_Qty_SI);
        t3 = findViewById(R.id.textView_Inventory_name_SI);
        t4 = findViewById(R.id.textView_Inventory_price_SI);
        t5 = findViewById(R.id.textView_Inventory_cost_SI);
        tv_qtyIn = findViewById(R.id.textView_quantity_in_SI);

        e1 = findViewById(R.id.editText_Inventory_barcode_SI);
        e2_quantity_in = findViewById(R.id.editText_Inventory_step5_Qty_SI);

        btn_esc = findViewById(R.id.btn_Inventory_esc_SI);
        btn_enter = findViewById(R.id.btn_Inventory_enter_SI);
        btn_back = findViewById(R.id.btn_back_SI);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        itemcode = intent1.getStringExtra("ItemCode");

        e1.setText(barcode);
        e1.setEnabled(false);

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
                    String Quantity_In = dataSnapshot.child("QtyIn").getValue().toString().trim();

                    // t1.setText(TotalQty);
                    t2.setText(Quantity);
                    t3.setText(Name);
                    t4.setText(Price);
                    t5.setText(Cost);
                    tv_qtyIn.setText(Quantity_In);
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
                    tv_qtyIn.setText("");
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

                Intent intent = new Intent(StockIn_step3.this, Stock_In_Scan.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("name", name);
                intent.putExtra("Users", users);
                intent.putExtra("ItemCode", itemcode);
                startActivity(intent);
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            final String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockIn_step3.this, Home_Page.class);
                intent.putExtra("Users", users);
                startActivity(intent);
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            final String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View v) {
                final String qty = e2_quantity_in.getText().toString().trim();


                if (qty.isEmpty()) {
                    Toast.makeText(StockIn_step3.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                } else {
                    final int total = Integer.parseInt(qty);
                    final int qty1 = Integer.parseInt(Quantity);
                    int sum = qty1 + total;
                    String sum2 = String.valueOf(sum);
                    databaseReference.child(key2).child("Quantity").setValue(sum2);

                    //RECORD PERSON INSERT
                    final DBHandler dbHandler = new DBHandler(getApplicationContext());
                    Cursor cursor = dbHandler.fetch();
                    cursor.moveToLast();
                    String username1 = cursor.getString(1);

                    databaseReference.child(key2).child("User").setValue(username1);
                    //.addOnCompleteListener(new OnCompleteListener<Void>() {
                    //   @Override
                    //    public void onComplete(@NonNull Task<Void> task) {
                    //       if (task.isSuccessful()){
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();

                            int sum = 0;
                            int totalQty1 = Integer.parseInt(totalQty);
                            sum = totalQty1 + total;
                            String sum2 = String.valueOf(sum);

                            databaseReference.child("TotalQty").setValue(sum2).toString().trim();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                            currentDateandTime = sdf.format(new Date());

                            String Quantity = dataSnapshot.child(key2).child("Quantity").getValue().toString().trim();
                            t1.setText(sum2);
                            t2.setText(Quantity);


                            Map dataMap = new HashMap();
                            dataMap.put("QtyIn", qty);
                            dataMap.put("QtyIn_Date", currentDateandTime);

                            databaseReference2.updateChildren(dataMap);

                            tv_qtyIn.setText(qty);
                            e2_quantity_in.setEnabled(false);
                            btn_enter.setClickable(false);

                            TotalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();


                            String ItemName = dataSnapshot.child(key2).child("ItemName").getValue().toString().trim();

                            //record stock in and out record;
                            stockInOutRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                            currentDateandTime2 = sdf2.format(new Date());
                            String parentname = barcode + "_" + currentDateandTime2;

                            Map dataMap4 = new HashMap();
                            dataMap4.put("Barcode", barcode);
                            dataMap4.put("Name", ItemName);
                            dataMap4.put("QtyIn", qty);
                            dataMap4.put("QtyOut", 0);
                            dataMap4.put("QtyInOut_Date", currentDateandTime);
                            //QUANTITY BEFORE STOCK IN
                            dataMap4.put("Qty", qty1);
                            //QUANTITY AFTER STOCK IN
                            dataMap4.put("TotalQty", Quantity);
                            dataMap4.put("HouseName", name);
                            stockInOutRef.child(name).child(parentname).updateChildren(dataMap4);

                            stockInOutRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.child("housename").exists()) {
                                        Map map = new HashMap();
                                        map.put("housename", name);
                                        stockInOutRef.child(name).updateChildren(map);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //   }
                    //     }
                    //    });
                    Toast.makeText(StockIn_step3.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(StockIn_step3.this, Home_Page.class);
        intent.putExtra("Users", users);
        startActivity(intent);
    }
}