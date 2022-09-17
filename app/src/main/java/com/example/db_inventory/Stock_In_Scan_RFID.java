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
import java.util.List;

public class Stock_In_Scan_RFID extends AppCompatActivity {

    //Barcode
    public static String barcode;
    EditText edt_barcode;
    Button btn_back, btn_next;
    DatabaseReference databaseReference, databaseReference2;
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

        setTitle("eStock_Stock In RFID");

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

        //SPINNER
        spinner = findViewById(R.id.spinner_stock_in);
        barcode_list = new ArrayList<>();

        databaseReference2 = FirebaseDatabase.getInstance().getReference("New_Goods");
        databaseReference2.keepSynced(true);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String spinnerbarcode = snapshot1.child("Barcode").getValue(String.class);
                    barcode_list.add(spinnerbarcode);

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Stock_In_Scan_RFID.this, android.R.layout.simple_list_item_1, barcode_list);
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
                Intent intent = new Intent(Stock_In_Scan_RFID.this, House_List_Stock_In.class);
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
                    Toast.makeText(Stock_In_Scan_RFID.this, "Please enter/scan barcode  ", Toast.LENGTH_SHORT).show();
                } else {
                    add();
                }
            }
        });
    }

    private void add() {
        final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
        String users = getIntent().getStringExtra("Users");
        String totalqtyh = getIntent().getStringExtra("TotalQtyH");

        if (TextUtils.isEmpty(barcode)) {
            edt_barcode.setError("Required Field...");
            return;
        }

        databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                if (dataSnapshot3.exists()) {
                    final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                    databaseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                inventory_key = childSnapshot.getKey();
                                Intent intent = new Intent(Stock_In_Scan_RFID.this, RFID_MainActivity_Stock_In.class);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name); //HOUSE'S NAME
                                intent.putExtra("Key", key); //HOUSE'S RANDOM KEY
                                intent.putExtra("Key2", inventory_key); //BARCODE'S RANDOM KEY
                                intent.putExtra("Users", users);
                                intent.putExtra("TotalQtyH", totalqtyh);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    // Toast.makeText(Inventory.this, "Data Already Exists", Toast.LENGTH_SHORT).show();
                } else if (!dataSnapshot3.exists()) {
                    Toast.makeText(getApplicationContext(), "Barcode " + barcode + " Not Exists in House: " + name, Toast.LENGTH_SHORT).show();
                }
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
        Intent intent = new Intent(Stock_In_Scan_RFID.this, House_List_Stock_In.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }
}