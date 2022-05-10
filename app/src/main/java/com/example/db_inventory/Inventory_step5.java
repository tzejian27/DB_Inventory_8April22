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

public class Inventory_step5 extends AppCompatActivity {

    Button b1, b2, b3;
    TextView t1;
    EditText e1;
    String key;
    String barcode;
    String Quantity;
    String key2;
    DatabaseReference databaseReference, databaseReference2;
    String totalQty;
    String name;
    DatabaseReference stockInOutRef;
    String currentDateandTime, currentDateandTime2;
    String itemName;

    //Modify Inventory qty
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step5);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        name = intent1.getStringExtra("name");
        itemName = intent1.getStringExtra("ItemName");

        setTitle("eStock_Inventory Modify_" + itemName);


        t1 = findViewById(R.id.textView_Inventory_step5_totalQty);

        e1 = findViewById(R.id.editText_Inventory_step5_Qty);

        b1 = findViewById(R.id.btn_Inventory_step5_cancel);
        //b2=(Button)findViewById(R.id.btn_Inventory_step5_add);
        b3 = findViewById(R.id.btn_Inventory_step5_change);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();

                t1.setText(Quantity);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String users = getIntent().getStringExtra("Users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("name", name);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        /*b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String qty = e1.getText().toString().trim();


                if(qty.isEmpty()){
                    Toast.makeText(Inventory_step5.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                }else{
                    final int total= Integer.parseInt(qty);
                    final int qty1= Integer.parseInt(Quantity);
                    int sum= qty1+total;
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
                            sum = totalQty1 + total;
                            String sum2 = String.valueOf(sum);

                            databaseReference.child("TotalQty").setValue(sum2).toString().trim();

                            Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                            intent.putExtra("barcode", barcode);
                            intent.putExtra("Key",key);
                            intent.putExtra("Key2",key2);
                            intent.putExtra("name",name);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //   }
                    //     }
                    //    });
                    Toast.makeText(Inventory_step5.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty = e1.getText().toString().trim();

                if (qty.isEmpty()) {
                    Toast.makeText(Inventory_step5.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                } else {

                    final int total = Integer.parseInt(qty);
                    final int qty1 = Integer.parseInt(Quantity);
                    final int stockadjust = total - qty1;
                    databaseReference.child(key2).child("Quantity").setValue(qty);

                    //RECORD PERSON INSERT
                    final DBHandler dbHandler = new DBHandler(getApplicationContext());
                    Cursor cursor = dbHandler.fetch();
                    cursor.moveToLast();
                    String username1 = cursor.getString(1);

                    databaseReference.child(key2).child("User").setValue(username1);

                    //save stock adjustment to stock in and out
                    //date for data store
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                    currentDateandTime = sdf.format(new Date());

                    //date for parent name
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                    currentDateandTime2 = sdf2.format(new Date());


                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentDateandTime3 = sdf3.format(new Date());

                    String parentname = barcode + "_" + currentDateandTime2;

                    stockInOutRef = FirebaseDatabase.getInstance().getReference("StockMovement");


                    Map dataMap4 = new HashMap();
                    dataMap4.put("ParentName", parentname);
                    dataMap4.put("Barcode", barcode);
                    dataMap4.put("Name", itemName);
                    dataMap4.put("QtyOut", 0);
                    dataMap4.put("QtyIn", 0);
                    dataMap4.put("StockAdj", stockadjust);
                    dataMap4.put("QtyInOut_Date", currentDateandTime);
                    dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                    //QUANTITY BEFORE STOCK TAKE
                    dataMap4.put("Qty", Quantity);
                    //QUANTITY AFTER STOCK TAKE
                    dataMap4.put("TotalQty", qty);
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

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();


                            int totalQty1 = Integer.parseInt(totalQty);

                            long sum = 0;

                            if (total < qty1) {
                                sum = totalQty1 - (qty1 - total);
                                String sum2 = Long.toString(sum);
                                databaseReference.child("TotalQty").setValue(sum2).toString().trim();
                                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                                intent.putExtra("Key", key);
                                intent.putExtra("Key2", key2);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                finish();

                            } else if (total == qty1) {
                                sum = totalQty1;
                                String sum2 = Long.toString(sum);
                                databaseReference.child("TotalQty").setValue(sum2).toString().trim();
                                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                                intent.putExtra("Key", key);
                                intent.putExtra("Key2", key2);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                finish();

                            } else if (total > qty1) {
                                sum = totalQty1 + (total - qty1);
                                String sum2 = Long.toString(sum);
                                databaseReference.child("TotalQty").setValue(sum2).toString().trim();
                                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                                intent.putExtra("Key", key);
                                intent.putExtra("Key2", key2);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //     }
                    //    }
                    //  });
                    Toast.makeText(Inventory_step5.this, "Change Successfully !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}