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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Inventory_step4 extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5;
    EditText e1;
    Button b1, b2;
    String key,key2,barcode,ItemCode,TotalQty,name,ItemName;
    private HashMap<String, String> BatchQty;
    DatabaseReference databaseReference;
    Dialog dialog;
    SimpleAdapter simpleAdapter;

    //SHOW ITEM DETAIL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step4);

        t1 = findViewById(R.id.textView_Inventory_TotalQty);
        t2 = findViewById(R.id.textView_Inventory_Qty);
        t3 = findViewById(R.id.textView_Inventory_name);
        t4 = findViewById(R.id.textView_Inventory_price);
        t5 = findViewById(R.id.textView_Inventory_cost);

        e1 = findViewById(R.id.editText_Inventory_barcode);

        b1 = findViewById(R.id.btn_Inventory_esc);
        b2 = findViewById(R.id.btn_Inventory_enter);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        name = intent1.getStringExtra("name");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        ItemCode = intent1.getStringExtra("ItemCode");
        getBatchNumberList();
        setTitle("eStock_Inventory_Step4");

        e1.setText(barcode);
        e1.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();
                    ItemName = dataSnapshot.child("ItemName").getValue().toString().trim();
                    String Price = dataSnapshot.child("Price").getValue().toString().trim();
                    String Cost = dataSnapshot.child("Cost").getValue().toString().trim();
                    if (dataSnapshot.child("ItemCode").exists()) {
                        String ItemCode = dataSnapshot.child("ItemCode").getValue().toString().trim();
                    } else {
                        String ItemCode = "-";
                    }

                    // t1.setText(TotalQty);
                    t2.setText(Quantity);
                    t3.setText(ItemName);
                    t4.setText(Price);
                    t5.setText(Cost);
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

        String users = getIntent().getStringExtra("Users");

        //EXIT BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_step4.this, House_Menu.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                intent.putExtra("Key2", key2);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();

            }
        });

        //ENTER STOKE TAKE
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Prompt dialog to show all the batch relatively to the item selected
                dialog = new Dialog(Inventory_step4.this);
                dialog.setContentView(R.layout.dialog_batch_spinner);
                dialog.getWindow().setLayout(650, 800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                // Retrieve data from firebase (Batch) & build hashmap
                getBatchNumberList();

                // Set up the adapter and put in the dialog (simple adapter)
                // Initialize the item to the list
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);
                List<HashMap<String, String>> listItem = new ArrayList<>();
                SimpleAdapter simpleAdapter = new SimpleAdapter(Inventory_step4.this, listItem, R.layout.list_batchno,
                        new String[]{"First Line", "Second Line"},
                        new int[]{R.id.text1, R.id.text2});

                // Fill in the data in the recycler view
                Iterator it = BatchQty.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap<String, String> resultMap = new HashMap<>();
                    Map.Entry pair = (Map.Entry) it.next();
                    resultMap.put("First Line", pair.getKey().toString());
                    resultMap.put("Second Line", pair.getValue().toString());
                    listItem.add(resultMap);
                }
                Inventory_step4.this.simpleAdapter = simpleAdapter;
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
                // Define onClick listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // After selected prompt another dialog to ask user whether confirm to proceed to next process
                        // - onClick Proceed option, initialize the intent for the next activity with essential data needed
                        // (Inventory_step5)


                        // TO DO
                        // After dialog item selected only new activity start

                        Intent intent = new Intent(Inventory_step4.this, Inventory_step5.class);
                        intent.putExtra("barcode", barcode);
                        intent.putExtra("Key", key);
                        intent.putExtra("Key2", key2);
                        intent.putExtra("name", name);
                        intent.putExtra("ItemName", ItemName);
                        intent.putExtra("Users", users);
                        startActivity(intent);
                        finish();
                    }
                });










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
                    if(temp_barcode.equalsIgnoreCase(barcode)){
                        temp_qty = (String) dss.child("Quantity").getValue();
                        BatchQty.put(batchNum, "Current quantity: " + temp_qty);
                    }
                }

                Inventory_step4.this.BatchQty = BatchQty;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}