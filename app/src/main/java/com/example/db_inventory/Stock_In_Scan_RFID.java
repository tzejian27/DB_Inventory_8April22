package com.example.db_inventory;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerResults;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Stock_In_Scan_RFID extends zebraScanner {

    //Barcode
    public static String barcode;
    EditText edt_barcode;
    Button btn_back, btn_next;
    DatabaseReference HouseReference, newGoodsReference;
    long maxid = 0;
    String currentDateandTime;
    String name;
    String key;
    String inventory_key;
    ScanReader scanReader;
    HashMap<String, Boolean> isBatchEnabled = new HashMap<>();
    ArrayList <String> batchNoList = new ArrayList<>();
    private String batchNumberPreset;
    private String batchNum;

    //SPINNER
    Spinner spinner;
    List<String> barcode_list;

    private String barcodeStr;

    private final String RES_ACTION = "android.intent.action.SCANRESULT";
    private BroadcastReceiver scanReceiver;
    ScannerInterface  scanner;



    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        String scanResult = "";
        if ((scanDataCollection != null) &&   (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData =  scanDataCollection.getScanData();
            // Iterate through scanned data and prepare the data.
            for (ScanDataCollection.ScanData data :  scanData) {
                // Get the scanned data
                scanResult =  data.getData();
            }
            // Update EditText with scanned data and type of label on UI thread.
            if (!scanResult.isEmpty()) {
                barcodeStr = scanResult;
                edt_barcode.setText(scanResult);
            }
        }
    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");

            if (intent.getAction().equals(RES_ACTION)){

                if(scanResult.length()>0){
                    barcodeStr = scanResult;
                    edt_barcode.setText(barcodeStr);
                }
            }
        }
    }

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

        // Scanner input for iData
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(RES_ACTION);
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, filter2);
        scanner = new ScannerInterface(this);
        scanner.setOutputMode(1);

        HouseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        HouseReference.keepSynced(true);
        HouseReference.addValueEventListener(new ValueEventListener() {
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

        newGoodsReference = FirebaseDatabase.getInstance().getReference("New_Goods");
        newGoodsReference.keepSynced(true);

        newGoodsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String spinnerbarcode = snapshot1.child("Barcode").getValue(String.class);
                    barcode_list.add(spinnerbarcode);
                    boolean itemBatchEnabled = snapshot1.child("isBatchEnabled").getValue(Boolean.class);
                    isBatchEnabled.put(spinnerbarcode, itemBatchEnabled);

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
                    if(isBatchEnabled.get(barcode)){
                        Dialog dialog = new Dialog(Stock_In_Scan_RFID.this);
                        dialog.setContentView(R.layout.dialog_rfid_batch);
                        dialog.getWindow().setLayout(650, 320);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        EditText batchInput = dialog.findViewById(R.id.editText_BatchNumber);
                        Button confirmBtn, cancelBtn;
                        ImageButton resetBtn = dialog.findViewById(R.id.resetButton);
                        TextView errorText = dialog.findViewById(R.id.Error_BatchNo);
                        confirmBtn = dialog.findViewById(R.id.confirmButton);
                        cancelBtn = dialog.findViewById(R.id.cancel_button);

                        // Preset batchNumber
                        
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
                                    batchInput.setText(batchNumberPreset);
                                    
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

                        resetBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                batchInput.setText(batchNumberPreset);
                            }
                        });
                        
                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                batchNum = batchInput.getText().toString();
                                dialog.dismiss();
                                add();
                            }
                        });

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        batchInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String inputText = charSequence.toString();

                                for(String text : batchNoList){
                                    if(inputText.matches(text)){
                                        batchInput.setBackgroundResource(R.drawable.red_border);
                                        errorText.setVisibility(View.VISIBLE);
                                        confirmBtn.setEnabled(false);
                                        confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#bbbbbb")));
                                        break;
                                    }
                                    else{
                                        batchInput.setBackgroundResource(R.drawable.blue_border);
                                        errorText.setVisibility(View.INVISIBLE);
                                        confirmBtn.setEnabled(true);
                                        confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#25A1DA")));
                                    }
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                    }else{
                        add();
                    }


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



        HouseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                if (dataSnapshot3.exists()) {
                    final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                    HouseReference.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                if(isBatchEnabled.get(barcode)){
                                    if(!batchNum.isEmpty()){
                                        intent.putExtra("batchNum", batchNum);
                                    }
                                }
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