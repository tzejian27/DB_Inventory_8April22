package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class Stock_Out_Scan extends AppCompatActivity {

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
    String key2;
    String totaltype;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_scan);

        edt_barcode = findViewById(R.id.editText_barcode_SO);
        btn_back = findViewById(R.id.btn_inventory_back_SO);
        btn_next = findViewById(R.id.btn_inventory_next_SO);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        key = intent.getStringExtra("Key");

        //Barcode Scanning
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
                Intent intent = new Intent(Stock_Out_Scan.this, House_List_Stock_Out.class);
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
                String barcode = edt_barcode.getText().toString().trim();
                if (barcode.isEmpty()) {
                    Toast.makeText(Stock_Out_Scan.this, "Please enter/scan barcode  ", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(Stock_Out_Scan.this, "Still under construction  ", Toast.LENGTH_SHORT).show();
                    add();
                }
            }
        });
    }

    private void add() {
        final String barcode = edt_barcode.getText().toString().trim();
        String users = getIntent().getStringExtra("Users");
        // final String barcode1 = e1.getText().toString().trim();
        //final String barcode_ref = barcode1 + "/";

        //databaseReference3=FirebaseDatabase.getInstance().getReference("House").child(key).push();
        //final String key2 = databaseReference3.getKey();

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
                                    Intent page = new Intent(Stock_Out_Scan.this, StockOut_step3.class);
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
                                    Toast.makeText(Stock_Out_Scan.this, "Barcode doesn't exist", Toast.LENGTH_SHORT).show();

                                } else if (dataSnapshot3.exists()) {
                                    // k = maxid -3;
                                    // key2 =Long.toString(k);
                                    // databaseReference3=FirebaseDatabase.getInstance().getReference("House").child(key).push();
                                    //  key2 = databaseReference3.getKey();
                                    final String barcode = edt_barcode.getText().toString().trim();
                                    databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                inventory_key = childSnapshot.getKey();
                                                Intent intent = new Intent(Stock_Out_Scan.this, StockOut_step3.class);
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
        Intent intent = new Intent(Stock_Out_Scan.this, House_List_Stock_Out.class);
        intent.putExtra("name", name);
        intent.putExtra("Key", key);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }
}