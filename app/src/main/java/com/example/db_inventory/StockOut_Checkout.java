package com.example.db_inventory;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;

public class StockOut_Checkout extends AppCompatActivity {

    private String user, house, barcode, selectedBatch,selectedBatchQtyfromDialog, batchQty, txt_requestedQuantity, txt_key2;
    private int num_requestedQuantity;
    TextView txt_batchSelector;
    FloatingActionButton btn_addButton;
    public static TextView txt_totalStockout, txt_totalRequired, txt_subtotal;
    RecyclerView batchRecyclerView;
    Dialog dialog;
    private SimpleAdapter simpleAdapter;
    private LinearLayoutManager layoutManager;
    private batchRecyclerViewAdapter adapter;
    private Button btn_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_batch);

        initializeComponent();
        setupBatchSelector();
        setupRecyclerViewContent();
        setupConfirmAction();

    }

    private void setupConfirmAction() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int requiredValue = Integer.parseInt(txt_totalRequired.getText().toString());
                if(requiredValue!=0){
                    if (requiredValue>0){
                        // Case 1: the quantity cannot be fulfilled due to INSUFFICIENT quantity entered
                        Toast.makeText(StockOut_Checkout.this, "Insufficient quantity",Toast.LENGTH_LONG).show();

                    }
                    else if(requiredValue<0){
                        // Case 2: the quantity entered has already EXCEEDED the requested quantity
                        Toast.makeText(StockOut_Checkout.this, "Quantity Exceeded as requested",Toast.LENGTH_LONG).show();

                    }
                }
                else{
                    // Case 3: the quantity entered is EQUIVALENT to the quantity requested
                    // Flow:
                    // 1. Get the All the batches added in the list
                    // 2. (Batch) Connect to firebase and update the value based on the respective value entered
                    // 3. (House) Connect to firebase and update the respective item's quantity in the respectgive house
                    // 4. Prompt toast message "Stocked out successfully"
                    List<batchRecyclerViewAdapter.batchItem> tempList = new ArrayList<>(batchRecyclerViewAdapter.mData) ;

                    DatabaseReference updateDataRefBatch = FirebaseDatabase.getInstance().getReference("Batch");
                    DatabaseReference updateDataRefHouse = FirebaseDatabase.getInstance().getReference("House");

                            // Update batch firebase
                            for (batchRecyclerViewAdapter.batchItem item :
                                batchRecyclerViewAdapter.mData) {
                                String temp_batchNum = item.getBatchNum();
                                int temp_availableQty = item.getNumericQuantity();
                                int temp_presetQty = item.getPreset_ipt_qty();


                                updateDataRefBatch.child(house).child(temp_batchNum).child("Quantity").setValue(temp_availableQty - temp_presetQty+"");
                            }

                            // Update house firebase data
                            updateDataRefHouse.child(house).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();
                                    int totalQtyOfItem = Integer.parseInt(dataSnapshot.child(txt_key2).child("Quantity").getValue().toString());

                                    int totalQty1 = Integer.parseInt(totalQty);
                                    int sum = totalQty1 - Integer.parseInt(txt_requestedQuantity);
                                    String sum2 = String.valueOf(sum);

                                    updateDataRefHouse.child(house).child("TotalQty").setValue(sum2).toString().trim();

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                                    String currentDateandTime = sdf.format(new Date());

                                    Map dataMap = new HashMap();
                                    dataMap.put("QtyOut", Integer.parseInt(txt_requestedQuantity));
                                    dataMap.put("QtyOut_Date", currentDateandTime);

                                    updateDataRefHouse.child(house).child(txt_key2).child("Quantity").setValue(totalQtyOfItem-Integer.parseInt(txt_requestedQuantity)+"");
                                    updateDataRefHouse.child(house).child(txt_key2).updateChildren(dataMap);

                                    //record stock in and out record;
                                    DatabaseReference stockInOutRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                                    String currentDateandTime3 = sdf3.format(new Date());

                                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                                    String currentDateandTime2 = sdf2.format(new Date());
                                    String parentname = barcode + "_" + currentDateandTime2;
                                    String ItemName = dataSnapshot.child(txt_key2).child("ItemName").getValue().toString();

                                    Map dataMap4 = new HashMap();
                                    dataMap4.put("ParentName", parentname);
                                    dataMap4.put("Barcode", barcode);
                                    dataMap4.put("Name", ItemName);
                                    dataMap4.put("QtyOut", txt_requestedQuantity);
                                    dataMap4.put("QtyIn", 0);
                                    dataMap4.put("QtyInOut_Date", currentDateandTime);
                                    dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                                    //QUANTITY BEFORE STOCK OUT
                                    dataMap4.put("Qty", totalQty);
                                    //QUANTITY AFTER STOCK OUT
                                    dataMap4.put("TotalQty", sum2);
                                    dataMap4.put("HouseName", house);
                                    stockInOutRef.child(house).child(parentname).updateChildren(dataMap4);

                                    stockInOutRef.child(house).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.child("housename").exists()) {
                                                Map map = new HashMap();
                                                map.put("housename", house);
                                                stockInOutRef.child(house).updateChildren(map);
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

                            Toast.makeText(StockOut_Checkout.this, "Item stocked out successfully", Toast.LENGTH_LONG).show();
                    // Back to Stock out scan page
                    Intent intent = new Intent(StockOut_Checkout.this, Stock_Out_Scan.class);
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("Key", house);
                    intent.putExtra("Key2", txt_key2);
                    intent.putExtra("name", house);
                    intent.putExtra("Users", user);
                    startActivity(intent);
                    finish();
                }



            }

        });
    }

    private void setupRecyclerViewContent() {
        batchRecyclerView = findViewById(R.id.batch_recycler_View);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        batchRecyclerView.setLayoutManager(layoutManager);

        batchRecyclerViewAdapter viewAdapter = new batchRecyclerViewAdapter(this, computeFIFOBatch());
        adapter = viewAdapter;
        batchRecyclerView.setAdapter(viewAdapter);

    }

    @NonNull
    private ArrayList<batchRecyclerViewAdapter.batchItem> computeFIFOBatch() {
        int totalQuantity = num_requestedQuantity;
        ArrayList<batchRecyclerViewAdapter.batchItem> result = new ArrayList<>();

        // Add the first selected batch details to the list
        result.add(new batchRecyclerViewAdapter.batchItem(selectedBatch, batchQty, Integer.parseInt(batchQty)));

        totalQuantity -= Integer.parseInt(batchQty);
        for (int i = 0; i < simpleAdapter.getCount(); i++) {
            HashMap<String, String> item = (HashMap<String, String>) simpleAdapter.getItem(i);
            String temp_batch = item.get("First Line");
            String temp_qty = Objects.requireNonNull(item.get("Second Line")).replaceAll("[^0-9]","");
            int num_tempQty = Integer.parseInt(temp_qty);
            int presetQty=0;

            if(temp_batch.equals(selectedBatch) || num_tempQty <= 0){
                continue;
            }

            if(num_tempQty >= totalQuantity){
                presetQty = totalQuantity;
                totalQuantity=0;
            }else if(num_tempQty<totalQuantity){
                presetQty = num_tempQty;
                totalQuantity-=presetQty;

            }
            result.add(new batchRecyclerViewAdapter.batchItem(temp_batch, temp_qty, presetQty));

            if(totalQuantity==0){
                break;
            }
        }


        return result;
    }

    private void initializeComponent() {
        // Initialize component reference from other class
        this.simpleAdapter = Stock_Out_Scan.simpleAdapter;

        // Get required details from intent
        // House, Barcode, Username
        Intent intent = getIntent();
        user = intent.getStringExtra("Users");
        house = intent.getStringExtra("Key");
        barcode = intent.getStringExtra("Barcode");
        selectedBatch = intent.getStringExtra("Batch");
        batchQty = intent.getStringExtra("Qty");
        txt_requestedQuantity = intent.getStringExtra("StockoutQty");
        num_requestedQuantity = Integer.parseInt(txt_requestedQuantity);
        txt_key2 = intent.getStringExtra("Key2");

        // Initialize and set value for the component
        txt_batchSelector = findViewById(R.id.txtView_BatchNumber2);
        txt_totalStockout = findViewById(R.id.txt_totalRequested);
        txt_totalRequired = findViewById(R.id.txt_required_qty);
        txt_subtotal = findViewById(R.id.txt_subtotal_qty);
        btn_addButton = findViewById(R.id.btn_add_batch);
        btn_confirm = findViewById(R.id.button_confirm);

        txt_totalStockout.setText(txt_requestedQuantity);
        txt_totalRequired.setText("0");
        txt_subtotal.setText(txt_requestedQuantity);

    }

    private void setupBatchSelector() {

        txt_batchSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(StockOut_Checkout.this);
                dialog.setContentView(R.layout.dialog_batch_spinner);
                dialog.getWindow().setLayout(650, 800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                // Initialize the item to the list
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                // Set adapter
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

                        String batchNum_fromDialog = result.get("First Line").toString();
                        String batchQty_fromDialog = result.get("Second Line").toString().replaceAll("[^0-9]+", "");
                        selectedBatchQtyfromDialog = batchQty_fromDialog;
                        txt_batchSelector.setText(batchNum_fromDialog);

                        dialog.dismiss();
                    }
                });

            }

        });
        btn_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempBatchNum = txt_batchSelector.getText().toString();
                List <batchRecyclerViewAdapter.batchItem> templist = batchRecyclerViewAdapter.mData;

                if(tempBatchNum.isEmpty()){
                    Toast.makeText(StockOut_Checkout.this,"Please select batch first!", Toast.LENGTH_LONG).show();
                }
                else if(!(templist.stream().anyMatch(b -> (b.getBatchNum()).equals(tempBatchNum)))){
                    templist.add(new batchRecyclerViewAdapter.batchItem(tempBatchNum, selectedBatchQtyfromDialog, 0));
                    Toast.makeText(StockOut_Checkout.this,"Successfully added!", Toast.LENGTH_LONG).show();
                    adapter.notifyItemInserted(templist.size()-1);
                }else{
                    Toast.makeText(StockOut_Checkout.this,"This batch has been added!", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

}