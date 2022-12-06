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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StockOut_Checkout extends AppCompatActivity {

    private String user, house, barcode, selectedBatch, batchQty, txt_requestedQuantity;
    private int num_requestedQuantity;
    TextView txt_batchSelector;
    public static TextView txt_totalStockout, txt_totalRequired, txt_subtotal;
    RecyclerView batchRecyclerView;
    Dialog dialog;
    private SimpleAdapter simpleAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_batch);

        initializeComponent();
        setupBatchSelector();
        setupRecyclerViewContent();


    }

    private void setupRecyclerViewContent() {
        batchRecyclerView = findViewById(R.id.batch_recycler_View);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        batchRecyclerView.setLayoutManager(layoutManager);

        batchRecyclerViewAdapter viewAdapter = new batchRecyclerViewAdapter(this, computeFIFOBatch());
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

            if(temp_batch.equals(selectedBatch)){
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

        // Initialize and set value for the component
        txt_batchSelector = findViewById(R.id.txtView_BatchNumber2);
        txt_totalStockout = findViewById(R.id.txt_totalRequested);
        txt_totalRequired = findViewById(R.id.txt_required_qty);
        txt_subtotal = findViewById(R.id.txt_subtotal_qty);

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
                        String batchQty_fromDialog = result.get("Second Line").toString().split(": ")[1];

                        dialog.dismiss();
                    }
                });

            }

        });


    }
}