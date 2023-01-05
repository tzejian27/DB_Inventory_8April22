package com.example.db_inventory;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockIn_step3 extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, tv_qtyIn, batchNoField, err_BatchNo;
    EditText e1, e2_quantity_in;
    Button btn_esc, btn_enter, btn_back;
    ImageView btn_reset;
    String key,key2,barcode,itemcode,TotalQty,name,Quantity,totalQty,currentDateandTime, currentDateandTime2;
    boolean isItemBatchEnabled;
    private String batchNumberPreset;
    ArrayList <String> batchNoList = new ArrayList<>();

    DatabaseReference databaseReference, databaseReference2;
    DatabaseReference stockMovRef;

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
        batchNoField = findViewById(R.id.editText_BatchNumber);
        err_BatchNo = findViewById(R.id.Error_BatchNo);

        e1 = findViewById(R.id.editText_Inventory_barcode_SI);
        e2_quantity_in = findViewById(R.id.editText_Inventory_step5_Qty_SI);

        btn_esc = findViewById(R.id.btn_Inventory_esc_SI);
        btn_enter = findViewById(R.id.btn_Inventory_enter_SI);
        btn_back = findViewById(R.id.btn_back_SI);
        btn_reset = findViewById(R.id.resetButton);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        itemcode = intent1.getStringExtra("ItemCode");

        setTitle("eStock_Stock In_" + barcode);

        e1.setText(barcode);
        e1.setEnabled(false);

        // Check whether the item enable for batch
        DatabaseReference newGoodsRef = FirebaseDatabase.getInstance().getReference("New_Goods");
        newGoodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isBatchEnabled = (boolean)(snapshot.child(barcode).child("isBatchEnabled").getValue());
                isItemBatchEnabled = isBatchEnabled;
                if(isBatchEnabled){
                    getBatchNo();
                    batchNoField.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String inputText = charSequence.toString();

                            for(String text : batchNoList){
                                if(inputText.matches(text)){
                                    batchNoField.setBackgroundResource(R.drawable.red_border);
                                    err_BatchNo.setVisibility(View.VISIBLE);
                                    btn_enter.setEnabled(false);
                                    btn_enter.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#bbbbbb")));
                                    break;
                                }
                                else{
                                    batchNoField.setBackgroundResource(R.drawable.blue_border);
                                    err_BatchNo.setVisibility(View.INVISIBLE);
                                    btn_enter.setEnabled(true);
                                    btn_enter.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }else{
                    batchNoField.setEnabled(false);
                    btn_reset.setEnabled(false);
                    batchNoField.setHint("N/A");
                    batchNoField.setBackgroundResource(R.drawable.corners_background);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchNoField.setText(batchNumberPreset);
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
                final String users = getIntent().getStringExtra("Users");
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

        btn_enter.setOnClickListener(new View.OnClickListener() {
            final String users = getIntent().getStringExtra("Users");

            @Override
            public void onClick(View v) {
                final String qty = e2_quantity_in.getText().toString().trim().replace("/", "|");


                if (qty.isEmpty()) {
                    Toast.makeText(StockIn_step3.this, "Please enter Qty", Toast.LENGTH_SHORT).show();
                }else if(isItemBatchEnabled && batchNoField.getText().toString().isEmpty()){
                    Toast.makeText(StockIn_step3.this, "Please enter batch number", Toast.LENGTH_SHORT).show();
                }
                else {
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
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();

                            int sum = 0;
                            int totalQty1 = Integer.parseInt(totalQty);
                            sum = totalQty1 + total;
                            String sum2 = String.valueOf(sum);

                            //UPDATE THE LASTED QUANTITY OF HOUSE
                            databaseReference.child("TotalQty").setValue(sum2).toString().trim();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                            currentDateandTime = sdf.format(new Date());

                            String Quantity = dataSnapshot.child(key2).child("Quantity").getValue().toString().trim();
                            t1.setText(sum2);
                            t2.setText(Quantity);

                            //CHANGE QUANTITY WHEN UPDATED
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
                            stockMovRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                            currentDateandTime2 = sdf2.format(new Date());

                            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                            String currentDateandTime3 = sdf3.format(new Date());

                            String parentname = barcode + "_" + currentDateandTime2;

                            //Insert stock movement
                            Map dataMap4 = new HashMap();
                            dataMap4.put("ParentName", parentname);
                            dataMap4.put("Barcode", barcode);
                            dataMap4.put("Name", ItemName);
                            dataMap4.put("QtyIn", qty);
                            dataMap4.put("QtyOut", 0);
                            dataMap4.put("QtyInOut_Date", currentDateandTime);
                            dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                            //QUANTITY BEFORE STOCK IN
                            dataMap4.put("Qty", qty1);
                            //QUANTITY AFTER STOCK IN
                            dataMap4.put("TotalQty", Quantity);
                            dataMap4.put("HouseName", name);
                            stockMovRef.child(name).child(parentname).updateChildren(dataMap4);

                            stockMovRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.child("housename").exists()) {
                                        Map map = new HashMap();
                                        map.put("housename", name);
                                        stockMovRef.child(name).updateChildren(map);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            if(isItemBatchEnabled){
                                UpdateBatchFirebase(qty, currentDateandTime);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(StockIn_step3.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                    btn_esc.setEnabled(true);
                    btn_esc.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                }
            }
        });

    }

    private void UpdateBatchFirebase(String qty, String DateTime) {
        DatabaseReference batchNoRef = FirebaseDatabase.getInstance().getReference("Batch");
        Map batchData = new HashMap();
        batchData.put("Barcode", barcode);
        batchData.put("ItemCode", itemcode);
        batchData.put("Qty In", qty);
        batchData.put("Quantity", qty);
        batchData.put("DateTime", DateTime);
        Map batchKey = new HashMap();
        batchKey.put(key+"/"+batchNoField.getText().toString() + "/" , batchData);
        batchNoRef.updateChildren(batchKey);

        if(batchNoField.getText().toString().equals(batchNumberPreset)){
            batchNoRef.child("Latest Batch").setValue(batchNumberPreset);
        }

        Map usedValue = new HashMap();

        usedValue.put(batchNoField.getText().toString(),batchNoField.getText().toString() );
        batchNoRef.child("Used Value").updateChildren(usedValue);
    }

    private void getBatchNo() {
        DatabaseReference tempReference = FirebaseDatabase.getInstance().getReference("Batch");
        final String[] latestBatchNumber = new String[1];
        tempReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Retrieve the latest used batch number
                latestBatchNumber[0] = snapshot.child("Latest Batch").getValue().toString().trim();
                String[] batchNo= latestBatchNumber[0].split("-");
                SimpleDateFormat sdf = new SimpleDateFormat("yyMM");

                String prefix = sdf.format(new Date()); // Current prefix of the batch number
                String suffix;
                if(prefix.equals(batchNo[0])){
                    suffix = String.format("%04d",Integer.parseInt(batchNo[1])+1);
                }else{
                    suffix = "0001";
                }
                batchNumberPreset = prefix+"-"+suffix;
                batchNoField.setText(batchNumberPreset);

                // Retrieve the used value of Batch Number into ArrayList
                DataSnapshot snapshot1 = snapshot.child("Used Value");
                for(DataSnapshot dss : snapshot1.getChildren()){
                    batchNoList.add((String) dss.getValue());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        final String users = getIntent().getStringExtra("Users");
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
}