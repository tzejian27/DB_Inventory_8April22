package com.example.db_inventory;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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

public class StockOut_step3 extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, tv_qtyOut, txt_batchNum, txt_batchQty;
    EditText e1, e2_quantity_out;
    Button b1, b2, b3;
    String key,key2,barcode,TotalQty,itemcode,name,Quantity,totalQty,currentDateandTime, currentDateandTime2;
    String batchQty, batchNum;
    DatabaseReference allowNRef;
    String Switch1;

    DatabaseReference databaseReference, databaseReference2;
    DatabaseReference stockInOutRef,batchRef;

    //Stock out qty out and get the detail display
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_stock_out_step3);

        t1 = findViewById(R.id.textView_Inventory_TotalQty_SO);
        t2 = findViewById(R.id.textView_Inventory_Qty_SO);
        t3 = findViewById(R.id.textView_Inventory_name_SO);
        t4 = findViewById(R.id.textView_Inventory_price_SO);
        t5 = findViewById(R.id.textView_Inventory_cost_SO);
        tv_qtyOut = findViewById(R.id.textView_quantity_in_SO);
        txt_batchNum = findViewById(R.id.txt_batch_field);
        txt_batchQty = findViewById(R.id.txt_batch_qty_field);

        e1 = findViewById(R.id.editText_Inventory_barcode_SO);
        e2_quantity_out = findViewById(R.id.editText_Inventory_step5_Qty_SO);

        b1 = findViewById(R.id.btn_Inventory_esc_SO);
        b2 = findViewById(R.id.btn_Inventory_enter_SO);
        b3 = findViewById(R.id.btn_back_SO);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        itemcode = intent1.getStringExtra("ItemCode");
        batchNum = intent1.getStringExtra("batchNum");
        txt_batchNum.setText(batchNum);
        batchQty= intent1.getStringExtra("batchQty");
        txt_batchQty.setText(batchQty);

        setTitle("eStock_Stock Out_" + barcode);

        e1.setText(barcode);
        e1.setEnabled(false);

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
            final String users = getIntent().getStringExtra("Users");

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
            final String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockOut_step3.this, Home_Page.class);
                intent.putExtra("Users", users);
                startActivity(intent);
            }
        });

        // Done button
        b2.setOnClickListener(new View.OnClickListener() {
            final String users = getIntent().getStringExtra("Users");


            @Override
            public void onClick(View v) {
                final String qty = e2_quantity_out.getText().toString().trim().replace("/", "|");
                int qty_in_number = Integer.parseInt(qty);
                allowNRef = FirebaseDatabase.getInstance().getReference("Switch");
                allowNRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Switch1 = snapshot.child("Allow_Negative").getValue().toString().trim();
                        if (Switch1.equals("On")) {
                            if (qty.isEmpty()) {
                                Toast.makeText(StockOut_step3.this, "Please enter Qty out", Toast.LENGTH_SHORT).show();
                            } else {

                                final int total = Integer.parseInt(qty); // Value from user's input
                                final int qty1 = Integer.parseInt(Quantity); // Value from firebase
                                int sum = qty1 - total; // Value of actual quantity after deducted the input value
                                String sum2 = String.valueOf(sum);
                                databaseReference.child(key2).child("Quantity").setValue(sum2); // Update the value of the item quantity

                                //RECORD PERSON INSERT
                                final DBHandler dbHandler = new DBHandler(getApplicationContext());
                                Cursor cursor = dbHandler.fetch();
                                cursor.moveToLast();
                                String username1 = cursor.getString(1);
                                databaseReference.child(key2).child("User").setValue(username1);

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

                                        //record stock in and out record;
                                        stockInOutRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                                        SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                                        currentDateandTime2 = sdf2.format(new Date());

                                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                                        String currentDateandTime3 = sdf3.format(new Date());

                                        String parentname = barcode + "_" + currentDateandTime2;

                                        Map dataMap4 = new HashMap();
                                        dataMap4.put("ParentName", parentname);
                                        dataMap4.put("Barcode", barcode);
                                        dataMap4.put("Name", ItemName);
                                        dataMap4.put("QtyOut", qty);
                                        dataMap4.put("QtyIn", 0);
                                        dataMap4.put("QtyInOut_Date", currentDateandTime);
                                        dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                                        //QUANTITY BEFORE STOCK OUT
                                        dataMap4.put("Qty", qty1);
                                        //QUANTITY AFTER STOCK OUT
                                        dataMap4.put("TotalQty", Quantity);
                                        dataMap4.put("HouseName", name);
                                        dataMap4.put("User", users);
                                        stockInOutRef.child(name).child(parentname).updateChildren(dataMap4);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Toast.makeText(StockOut_step3.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                                b1.setEnabled(true);
                                b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                            }
                        }else {
                            // Only positive quantity is allowed.
                            // In other words, user is only allowed to stock out based on the
                            // quantity available in the house

                            final int total1 = Integer.parseInt(qty); // User input quantity
                            final int qty11 = Integer.parseInt(Quantity); // Quantity value get from firebase
                            if (qty.isEmpty()) {
                                Toast.makeText(StockOut_step3.this, "Please enter Qty out", Toast.LENGTH_SHORT).show();

                            }else if(!txt_batchQty.getText().toString().equals("")){
                                updateFirebaseWithBNum(total1, qty11);
//                                b1.setEnabled(true);
//                                b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                            }else if(txt_batchQty.getText().toString().equals("")){
                                updateFirebaseWOBNum(total1, qty11);
                                b1.setEnabled(true);
                                b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                            }
                        }
                    }

                    private void updateFirebaseWOBNum(int total1, int qty11) {
                        if(total1 <= qty11){
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

                                   //record stock in and out record;
                                   stockInOutRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                                   SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                                   String currentDateandTime3 = sdf3.format(new Date());

                                   SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                                   currentDateandTime2 = sdf2.format(new Date());
                                   String parentname = barcode + "_" + currentDateandTime2;

                                   Map dataMap4 = new HashMap();
                                   dataMap4.put("ParentName", parentname);
                                   dataMap4.put("Barcode", barcode);
                                   dataMap4.put("Name", ItemName);
                                   dataMap4.put("QtyOut", qty);
                                   dataMap4.put("QtyIn", 0);
                                   dataMap4.put("QtyInOut_Date", currentDateandTime);
                                   dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                                   //QUANTITY BEFORE STOCK OUT
                                   dataMap4.put("Qty", qty1);
                                   //QUANTITY AFTER STOCK OUT
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
                           Toast.makeText(StockOut_step3.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                           b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                       }
                        else{
                           Toast.makeText(StockOut_step3.this, "Execute actual Quantity \n"+ total1 + " < " + qty11, Toast.LENGTH_SHORT).show();
                       }
                    }

                    private void updateFirebaseWithBNum(int total1, int qty11) {
                        // If the stocked out value less than the current available quantity
                        if((total1 <= qty11) && (total1 <= Integer.parseInt(txt_batchQty.getText().toString()))){
                            // Update the quantity value in the database
                            final int total = Integer.parseInt(qty);
                            final int qty1 = Integer.parseInt(Quantity);
                            int sum = qty1 - total;
                            int batchQuantity = Integer.parseInt(txt_batchQty.getText().toString());
                            int batchQtyResult = batchQuantity- total1;
                            String sum2 = String.valueOf(sum);
                            databaseReference.child(key2).child("Quantity").setValue(sum2);
                            batchRef = FirebaseDatabase.getInstance().getReference("Batch").child(key).child(batchNum).child("Quantity");
                            batchRef.setValue(((batchQtyResult>=0)?batchQtyResult:0)+"");

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

                                    //record stock in and out record;
                                    stockInOutRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                                    String currentDateandTime3 = sdf3.format(new Date());

                                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                                    currentDateandTime2 = sdf2.format(new Date());
                                    String parentname = barcode + "_" + currentDateandTime2;

                                    Map dataMap4 = new HashMap();
                                    dataMap4.put("ParentName", parentname);
                                    dataMap4.put("Barcode", barcode);
                                    dataMap4.put("Name", ItemName);
                                    dataMap4.put("QtyOut", qty);
                                    dataMap4.put("QtyIn", 0);
                                    dataMap4.put("QtyInOut_Date", currentDateandTime);
                                    dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                                    //QUANTITY BEFORE STOCK OUT
                                    dataMap4.put("Qty", qty1);
                                    //QUANTITY AFTER STOCK OUT
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
                            Toast.makeText(StockOut_step3.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                            b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                        }
                        else if((total1 <= qty11) && (total1 > Integer.parseInt(txt_batchQty.getText().toString()))){
                            Intent intent = new Intent(StockOut_step3.this, StockOut_Checkout.class);
                            intent.putExtra("Users", users);
                            intent.putExtra("Key", key);
                            intent.putExtra("Key2", key2);
                            intent.putExtra("Barcode", barcode);
                            intent.putExtra("Batch", batchNum);
                            intent.putExtra("Qty", batchQty);
                            intent.putExtra("StockoutQty", qty);
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(StockOut_step3.this, "Execute actual Quantity \n"+ total1 + " < " + qty11, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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