package com.example.db_inventory;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Stock_Out_Scan extends zebraScanner {

    //Barcode
    public static String barcode;
    EditText edt_barcode;
    TextView txt_batchNumField, txt_QtyField;
    Button btn_back, btn_next;
    boolean isItemBatchEnabled;
    DatabaseReference databaseReference, databaseReference2;
    Dialog dialog;
    long maxid = 0;
    String currentDateandTime,name,key,inventory_key;
    ScanReader scanReader;
    private HashMap<String, String> BatchQty;
    public static SimpleAdapter simpleAdapter;
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
                if(scanResult.length()>0){
                    barcodeStr = scanResult;

                    if(dialog!=null){
                        if(dialog.isShowing()) {
                            EditText editText = dialog.findViewById(R.id.edit_text);
                            editText.setText(barcodeStr);
                        }else{
                            edt_barcode.setText(barcodeStr);
                        }
                    }else{
                        edt_barcode.setText(barcodeStr);
                    }
                }
            }
        }
    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");

            if (intent.getAction().equals(RES_ACTION)){

                if(scanResult.length()>0){
                    barcodeStr = scanResult;

                    if(dialog!=null){
                        if(dialog.isShowing()) {
                            EditText editText = dialog.findViewById(R.id.edit_text);
                            editText.setText(barcodeStr);
                        }else{
                            edt_barcode.setText(barcodeStr);
                        }
                    }else{
                        edt_barcode.setText(barcodeStr);
                    }
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
                System.out.println(barcodeStr);
                if(dialog!=null){
                    if(dialog.isShowing()) {
                        EditText editText = dialog.findViewById(R.id.edit_text);
                        editText.setText(barcodeStr);
                    }else{
                        edt_barcode.setText(barcodeStr);
                    }
                }else{
                    edt_barcode.setText(barcodeStr);
                }

            }
        }
    };


    //Scan barcode for stock out
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_scan);

        edt_barcode = findViewById(R.id.editText_barcode_SO);
        btn_back = findViewById(R.id.btn_inventory_back_SO);
        btn_next = findViewById(R.id.btn_inventory_next_SO);
        txt_batchNumField = findViewById(R.id.txtView_BatchNumber);
        txt_QtyField = findViewById(R.id.QtyField);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        key = intent.getStringExtra("Key");

        setTitle("eStock_Stock Out Scan_" + name);

        //Barcode Scanning
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
        spinner = findViewById(R.id.spinner_stock_out);
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Stock_Out_Scan.this, android.R.layout.simple_list_item_1, barcode_list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spinner.setAdapter(arrayAdapter);
                String barcodes = spinner.getSelectedItem().toString().trim();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edt_barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                barcode = charSequence.toString();

                // Check whether the item enable for batch
                DatabaseReference newGoodsRef = FirebaseDatabase.getInstance().getReference("New_Goods");
                newGoodsRef.orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            boolean isBatchEnabled = (boolean)(snapshot.child(barcode).child("isBatchEnabled").getValue());
                            isItemBatchEnabled = isBatchEnabled;
                            txt_batchNumField.setEnabled(true);
                            txt_batchNumField.setBackgroundResource(R.drawable.blue_border);
                            if(isBatchEnabled){
                                txt_batchNumField.setHint("Select Batch Number");
                                getBatchNumberList();
                                txt_batchNumField.setText("");
                                txt_QtyField.setText("");
                                txt_batchNumField.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        dialog = new Dialog(Stock_Out_Scan.this);
                                        dialog.setContentView(R.layout.dialog_batch_spinner);
                                        dialog.getWindow().setLayout(650, 800);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();

                                        // Initialize the item to the list
                                        EditText editText = dialog.findViewById(R.id.edit_text);
                                        ListView listView = dialog.findViewById(R.id.list_view);

                                        // Set adapter
                                        List<HashMap<String, String>> listItem = new ArrayList<>();
                                        SimpleAdapter simpleAdapter = new SimpleAdapter(Stock_Out_Scan.this, listItem, R.layout.list_batchno,
                                                new String[]{"First Line", "Second Line"},
                                                new int[]{R.id.text1, R.id.text2});


                                        Iterator it = BatchQty.entrySet().iterator();
                                        while (it.hasNext()) {
                                            HashMap<String, String> resultMap = new HashMap<>();
                                            Map.Entry pair = (Map.Entry) it.next();
                                            resultMap.put("First Line", pair.getKey().toString());
                                            resultMap.put("Second Line", pair.getValue().toString());
                                            listItem.add(resultMap);
                                        }
                                        Stock_Out_Scan.this.simpleAdapter = simpleAdapter;
                                        listView.setAdapter(simpleAdapter);

                                        editText.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                simpleAdapter.getFilter().filter(charSequence);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {

                                            }
                                        });

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                HashMap<String, String> result = (HashMap<String, String>) simpleAdapter.getItem(i);
                                                txt_batchNumField.setText(result.get("First Line").toString());
                                                txt_QtyField.setText(result.get("Second Line").toString().split(": ")[1]);
                                                dialog.dismiss();
                                            }
                                        });

                                    }

                                });
                            }else{
                                txt_QtyField.setText("");
                                txt_batchNumField.setText("");
                                txt_batchNumField.setEnabled(false);
                                txt_batchNumField.setHint("N/A");
                                txt_batchNumField.setBackgroundResource(R.drawable.corners_background);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
                if (barcode.isEmpty()) {
                    Toast.makeText(Stock_Out_Scan.this, "Please enter/scan barcode  ", Toast.LENGTH_SHORT).show();
                } else if(isItemBatchEnabled){
                    if(txt_batchNumField.getText().toString().isEmpty()){
                        Toast.makeText(Stock_Out_Scan.this, "Please select batch number  ", Toast.LENGTH_SHORT).show();
                    }else{
                        add();
                    }
                } else{
                    //Toast.makeText(Stock_Out_Scan.this, "Still under construction  ", Toast.LENGTH_SHORT).show();
                    add();
                }
            }
        });



    }

    private void getBatchNumberList() {
        HashMap<String , String > BatchQty = new HashMap<>();
        DatabaseReference batchRef = FirebaseDatabase.getInstance().getReference("Batch").child(key);
        batchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dss : snapshot.getChildren()){
                    String batchNum = dss.getKey();
                    String temp_barcode = (String) dss.child("Barcode").getValue();
                    String temp_qty ;
                    if(temp_barcode.equalsIgnoreCase(edt_barcode.getText().toString())){
                        temp_qty = (String) dss.child("Quantity").getValue();
                        if (temp_qty.equals("0")){
                            continue;
                        }
                        BatchQty.put(batchNum, "Quantity left: " + temp_qty);

                    }
                }

                Stock_Out_Scan.this.BatchQty = BatchQty;
                sortMap();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sortMap() {
        ArrayList<String> list = new ArrayList<>();
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : BatchQty.entrySet()) {
            list.add(entry.getKey());
        }
        Collections.sort(list);
        for (String key : list) {
            for (Map.Entry<String, String> entry : BatchQty.entrySet()) {
                if (entry.getKey().equals(key)) {
                    sortedMap.put(key, entry.getValue());
                }
            }
        }

        BatchQty = sortedMap;

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
                                    Toast.makeText(Stock_Out_Scan.this, "Barcode doesn't exist in House, Please make a stock take", Toast.LENGTH_SHORT).show();

                                } else if (!dataSnapshot3.exists()) {
                                    Toast.makeText(Stock_Out_Scan.this, "Barcode doesn't exist in House", Toast.LENGTH_SHORT).show();

                                } else if (dataSnapshot3.exists()) {

                                    final String barcode = edt_barcode.getText().toString().trim().replace("/", "|");
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
                                                intent.putExtra("batchNum", txt_batchNumField.getText().toString());
                                                intent.putExtra("batchQty", txt_QtyField.getText());

                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
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