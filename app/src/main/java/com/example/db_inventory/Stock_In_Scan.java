package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class Stock_In_Scan extends AppCompatActivity {

    //Barcode
    public static String barcode;
    EditText edt_barcode;
    Button btn_back, btn_next;
    DatabaseReference databaseReference, databaseReference2, databaseReference3;
    long maxid = 0;
    String Quantity = "0";
    String currentDateandTime;
    String Name;
    String Price;
    String Cost;
    String name;
    String key;
    String totaltype;
    String ItemCode;
    long k;
    String inventory_key;
    ScanReader scanReader;
    private String barcodeStr;
    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode));
            if (barcode != null) {
                barcodeStr = new String(barcode);
                edt_barcode.setText(barcodeStr);
            }
        }
    };

    //SCAN BARCODE FOR STOCK IN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_scan);

        edt_barcode = findViewById(R.id.editText_barcode_SI);
        btn_back = findViewById(R.id.btn_inventory_back_SI);
        btn_next = findViewById(R.id.btn_inventory_next_SI);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        key = intent.getStringExtra("Key");

        setTitle("eStock_Stock In Scan");

        //BARCODE SCANNING
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    maxid = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String users = getIntent().getStringExtra("Users");

        btn_back.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Stock_In_Scan.this, House_List_Stock_In.class);
                intent.putExtra("name", name);
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                if (barcode.isEmpty()) {
                    Toast.makeText(Stock_In_Scan.this, "Please enter/scan barcode  ", Toast.LENGTH_SHORT).show();
                } else {
                    add();
                }
            }
        });
    }

    private void add() {
        final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
        String users = getIntent().getStringExtra("Users");

        if (TextUtils.isEmpty(barcode)) {
            edt_barcode.setError("Required Field...");
            return;
        }

        databaseReference2 = FirebaseDatabase.getInstance().getReference("New_Goods");
        databaseReference2.keepSynced(true);
        databaseReference2.child(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                databaseReference2.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot2) {
                        databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                if (dataSnapshot2.exists() && !dataSnapshot3.exists()) {
                                    Name = dataSnapshot.child("Name").getValue().toString().trim();
                                    Price = dataSnapshot.child("Price").getValue().toString().trim();
                                    Cost = dataSnapshot.child("Cost").getValue().toString().trim();

                                    if (dataSnapshot.child("ItemCode").exists()) {
                                        ItemCode = dataSnapshot.child("ItemCode").getValue().toString().trim();
                                    } else {
                                        ItemCode = "-";
                                    }

                                    Intent intent = getIntent();
                                    String name = intent.getStringExtra("name");
                                    k = maxid - 3;
                                    totaltype = Long.toString(k);
                                    // final String key2 =Long.toString(k);

                                    databaseReference3 = FirebaseDatabase.getInstance().getReference("House").child(key).push();
                                    databaseReference3.keepSynced(true);
                                    final String key2 = databaseReference3.getKey();

                                    Map dataMap = new HashMap();
                                    dataMap.put("Key", key2);
                                    dataMap.put("HouseKey", key);
                                    dataMap.put("Barcode", barcode);
                                    dataMap.put("ItemName", Name);
                                    dataMap.put("ItemCode", ItemCode);
                                    dataMap.put("Price", Price);
                                    dataMap.put("Cost", Cost);
                                    dataMap.put("Quantity", Quantity);
                                    dataMap.put("Date_and_Time", currentDateandTime);


                                    //   Map dataMap2 = new HashMap();
                                    //   dataMap2.put(maxid-3 + "/" ,dataMap);


                                    databaseReference3.updateChildren(dataMap);//.addOnCompleteListener(new OnCompleteListener() {
                                    //   @Override
                                    //       public void onComplete(@NonNull Task task) {
                                    //       if (task.isSuccessful()){
                                    databaseReference.child("TotalType").setValue(totaltype);//.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    //   @Override
                                    //     public void onComplete(@NonNull Task<Void> task) {
                                    //     if (task.isSuccessful()){
                                    Intent page = new Intent(Stock_In_Scan.this, StockIn_step3.class);
                                    page.putExtra("barcode", barcode);
                                    page.putExtra("name", name);
                                    page.putExtra("Key", key);
                                    page.putExtra("Key2", key2);
                                    page.putExtra("Users", users);
                                    startActivity(page);
                                    finish();
                                    //     }else{
                                    //       Toast.makeText(Inventory.this, "Error", Toast.LENGTH_SHORT).show();
                                    //     }

                                    //    }
                                    //  });
                                    //     }

                                    //    }
                                    //  });
                                } else if (!dataSnapshot3.exists()) {
                                    databaseReference2 = FirebaseDatabase.getInstance().getReference("Switch");
                                    databaseReference2.keepSynced(true);
                                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String s1 = dataSnapshot.child("NoNeed").getValue().toString().trim();

                                            if (s1.equals("Need")) {
                                                String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                                                Intent intent = new Intent(Stock_In_Scan.this, StockIn_step2.class);
                                                intent.putExtra("barcode", barcode);
                                                intent.putExtra("name", name);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                            } else {

                                                databaseReference3 = FirebaseDatabase.getInstance().getReference("House").child(key).push();
                                                databaseReference3.keepSynced(true);
                                                final String key2 = databaseReference3.getKey();

                                                //RECORD PERSON INSERT
                                                /*final DBHandler dbHandler = new DBHandler(getApplicationContext());
                                                Cursor cursor = dbHandler.fetch();
                                                cursor.moveToLast();
                                                String username1 = cursor.getString(1);*/

                                                String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                                                Map dataMap = new HashMap();
                                                dataMap.put("Key", key2);
                                                dataMap.put("HouseKey", key);
                                                dataMap.put("Barcode", barcode);
                                                dataMap.put("ItemCode", ItemCode);
                                                dataMap.put("ItemName", "Haven't Modify");
                                                dataMap.put("Price", "0");
                                                dataMap.put("Cost", "0");
                                                dataMap.put("Quantity", Quantity);
                                                dataMap.put("Date_and_Time", currentDateandTime);
                                                /*dataMap.put("User", username1);*/

                                                //calculate total type
                                                k = maxid - 3;
                                                totaltype = Long.toString(k);

                                                databaseReference3.updateChildren(dataMap);
                                                databaseReference.child("TotalType").setValue(totaltype);

                                                Intent intent = new Intent(Stock_In_Scan.this, StockIn_step3.class);
                                                intent.putExtra("barcode", barcode);
                                                intent.putExtra("name", name);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                                Toast.makeText(Stock_In_Scan.this, "Data Save", Toast.LENGTH_SHORT).show();
                                            }

                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                } else if (dataSnapshot3.exists()) {
                                    // k = maxid -3;
                                    // key2 =Long.toString(k);
                                    // databaseReference3=FirebaseDatabase.getInstance().getReference("House").child(key).push();
                                    //  key2 = databaseReference3.getKey();
                                    final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                                    databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                inventory_key = childSnapshot.getKey();
                                                Intent intent = new Intent(Stock_In_Scan.this, StockIn_step3.class);
                                                intent.putExtra("barcode", barcode);
                                                intent.putExtra("name", name);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", inventory_key);
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    // Toast.makeText(Inventory.this, "Data Already Exists", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(Stock_In_Scan.this, House_List_Stock_In.class);
        intent.putExtra("name", name);
        intent.putExtra("Key", key);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }
}