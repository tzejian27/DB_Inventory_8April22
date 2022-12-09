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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class Stock_In_Scan extends AppCompatActivity {

    //Barcode
    public static String barcode;
    EditText edt_barcode;
    Button btn_back, btn_next;
    DatabaseReference databaseReference, databaseReference2, databaseReference3;
    long numOfItem = 0;
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

    //SPINNER
    Spinner spinner;
    List<String> barcode_list;

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
                    numOfItem = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //SPINNER
        spinner = findViewById(R.id.spinner_stock_in);
        barcode_list = new ArrayList<>();

        databaseReference2 = FirebaseDatabase.getInstance().getReference("New_Goods");
        databaseReference2.keepSynced(true);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    String spinnerbarcode = snapshot1.child("Barcode").getValue(String.class);
                    barcode_list.add(spinnerbarcode);

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Stock_In_Scan.this, android.R.layout.simple_list_item_1, barcode_list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spinner.setAdapter(arrayAdapter);
                String barcodes = spinner.getSelectedItem().toString().trim();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //SET SELECTED ITEM IN LISTENER TO EDIT TEXT BOXES
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                edt_barcode.setText(item.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        // Reference to firebase New Goods which contains all the inventory
        databaseReference2.child(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataFromNEWGOODS) {
                // Find the record "barcode"
                databaseReference2.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot queryResultNewGoods) {
                        databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot queryResultHouse) {
                                // Exist in NEW GOODS but does not exist in the respective HOUSE
                                if (queryResultNewGoods.exists() && !queryResultHouse.exists()) {
                                    Name = dataFromNEWGOODS.child("Name").getValue().toString().trim();
                                    Price = dataFromNEWGOODS.child("Price").getValue().toString().trim();
                                    Cost = dataFromNEWGOODS.child("Cost").getValue().toString().trim();

                                    if (dataFromNEWGOODS.child("ItemCode").exists()) {
                                        ItemCode = dataFromNEWGOODS.child("ItemCode").getValue().toString().trim();
                                    } else {
                                        ItemCode = "-";
                                    }

                                    Intent intent = getIntent();
                                    String name = intent.getStringExtra("name");
                                    k = numOfItem - 3; // Equivalent to (NumofItem - 4[Non-Item Child] + 1[New Item])
                                    totaltype = Long.toString(k); // Final number of item in the house

                                    databaseReference3 = FirebaseDatabase.getInstance().getReference("House").child(key).push();
                                    databaseReference3.keepSynced(true);
                                    final String key2 = databaseReference3.getKey();

                                    Map dataMap = new HashMap();
                                    dataMap.put("Key", key2);       // Unique key
                                    dataMap.put("HouseKey", key);   // House Key
                                    dataMap.put("Barcode", barcode);
                                    dataMap.put("ItemName", Name);
                                    dataMap.put("ItemCode", ItemCode);
                                    dataMap.put("Price", Price);
                                    dataMap.put("Cost", Cost);
                                    dataMap.put("Quantity", Quantity);
                                    dataMap.put("Date_and_Time", currentDateandTime);

                                    databaseReference3.updateChildren(dataMap);
                                    // Update House quantity value
                                    databaseReference.child("TotalType").setValue(totaltype);
                                    Intent page = new Intent(Stock_In_Scan.this, StockIn_step3.class);
                                    page.putExtra("barcode", barcode);
                                    page.putExtra("name", name);
                                    page.putExtra("Key", key);
                                    page.putExtra("Key2", key2);
                                    page.putExtra("Users", users);
                                    startActivity(page);
                                    finish();

                                }

                                // Does not exist in both NEW GOODS and respective HOUSE
                                else if (!queryResultHouse.exists()) {
                                    databaseReference2 = FirebaseDatabase.getInstance().getReference("Switch");
                                    databaseReference2.keepSynced(true);
                                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String s1 = dataSnapshot.child("NoNeed").getValue().toString().trim();
                                            // Check whether the system allow to edit spec of item
                                            if (s1.equals("Need")) {
                                                String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                                                Intent intent = new Intent(Stock_In_Scan.this, StockIn_step2.class);
                                                intent.putExtra("barcode", barcode);
                                                intent.putExtra("name", name);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                            } else {
                                                // Insert item to the HOUSE without key in the details of the item
                                                // The details here is either default value or NULL value
                                                databaseReference3 = FirebaseDatabase.getInstance().getReference("House").child(key).push();
                                                databaseReference3.keepSynced(true);
                                                final String key2 = databaseReference3.getKey();

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

                                                //calculate total type
                                                k = numOfItem - 3;
                                                totaltype = Long.toString(k);

                                                databaseReference3.updateChildren(dataMap);
                                                databaseReference.child("TotalType").setValue(totaltype);

                                                // Go to the page where to key in the quantity to be stocked in
                                                Intent intent = new Intent(Stock_In_Scan.this, StockIn_step3.class);
                                                intent.putExtra("barcode", barcode);
                                                intent.putExtra("name", name);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                                Toast.makeText(Stock_In_Scan.this, "Data Save", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    // Condition where the item is existed in the HOUSE
                                } else if (queryResultHouse.exists()) {

                                    final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                                    databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            // The loop is TO AVOID duplication of ITEM in the HOUSE using the data differently
                                            // Generally, the loop only run once, more than one time is an exception.
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