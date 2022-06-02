package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db_inventory.Class.Brand_class;
import com.example.db_inventory.Class.Color_class;
import com.example.db_inventory.Class.ItemCode_class;
import com.example.db_inventory.Class.Shelf_class;
import com.example.db_inventory.Class.Size_class;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GRNInventory_step2 extends AppCompatActivity {
    Button b1,b2;
    Spinner s1,s2,s3,sItemCode;
    TextView barcodeView ,colorTextview, sizeTextView, noteTextView;
    EditText itemcode;
    ArrayList<String> listItem =new ArrayList<>();
    ArrayList<String> listItem2 =new ArrayList<>();
    ArrayList<String> listItem3 = new ArrayList<>();
    ArrayList<String> listItem4 = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    ArrayAdapter<String> adapter3;
    ArrayAdapter<String> itemcodeadapter;
    private  String barcode;
    private String goodReturnsNo;
    ArrayList<String> listItemCode = new ArrayList<>();
    public static String finalitemcode;
    public static String finalcolorcode;
    public static String finalsizecode;
    ScanReader scanReader;

    DatabaseReference InventoryGoodReturnsNo;
    DatabaseReference Maintain;

    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode)) ;
            if (barcode != null) {
                String barcodeStr = new String(barcode);
                itemcode.setText(barcodeStr);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grninventory_step2);
        //SET TITLE
        setTitle("Good Returns - Create Item Code");

        //CONNECT SCANNER
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        //DECLARATION
        noteTextView = findViewById(R.id.textView6);
        b1= findViewById(R.id.btn_back2);
        b2= findViewById(R.id.btn_next2);
        itemcode = findViewById(R.id.itemcodetext);
        s1=findViewById(R.id.spinner_Inventory_brand);
        s2=findViewById(R.id.spinner_Inventory_color);
        s3=findViewById(R.id.spinner_Inventory_size);
        barcodeView = findViewById(R.id.barcodeview);
        colorTextview = findViewById(R.id.textView7);
        sizeTextView = findViewById(R.id.textView8);
        sItemCode = findViewById(R.id.spinner_itemcode);

        //GET INTENT
        Intent intent = getIntent();
        barcode = intent.getStringExtra("Barcode");
        goodReturnsNo = intent.getStringExtra("goodReturnNo");

        barcodeView.setText(barcode);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        listItem.add("---");
        listItem2.add("---");
        listItem3.add("---");
        listItemCode.add("---");

        InventoryGoodReturnsNo= FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");
        Maintain= FirebaseDatabase.getInstance().getReference().child("Maintain");

        getBrand();
        getShelf();

        //SPINNER ADAPTER
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listItem);
        s1.setAdapter(adapter);
        s1.setVisibility(View.GONE);

        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listItem2);
        s2.setAdapter(adapter2);

        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,listItem3);
        s3.setAdapter(adapter3);

        itemcodeadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listItemCode);
        sItemCode.setAdapter(itemcodeadapter);

        getItemCode();

        sItemCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("---"))
                {
                    listItem2.clear();
                    listItem2.add("---");
                    listItem3.clear();
                    listItem3.add("---");
                    noteTextView.setVisibility(View.VISIBLE);
                    noteTextView.setText("* Color and Size will show after selecting Item Code");
                    adapter2.notifyDataSetChanged();
                    adapter3.notifyDataSetChanged();
                    colorTextview.setVisibility(View.GONE);
                    s2.setVisibility(View.GONE);
                    sizeTextView.setVisibility(View.GONE);
                    s3.setVisibility(View.GONE);
                    b2.setVisibility(View.GONE);
                }
                else
                {
                    finalitemcode = parent.getItemAtPosition(position).toString();
                    String[] splitfinalitemcode = finalitemcode.split("/");
                    int sizecode = splitfinalitemcode.length;
                    if(sizecode >= 2)
                    {
                        listItem2.clear();
                        listItem2.add("---");
                        listItem3.clear();
                        listItem3.add("---");
                        adapter2.notifyDataSetChanged();
                        adapter3.notifyDataSetChanged();
                        colorTextview.setVisibility(View.GONE);
                        s2.setVisibility(View.GONE);
                        sizeTextView.setVisibility(View.GONE);
                        s3.setVisibility(View.GONE);
                        noteTextView.setVisibility(View.INVISIBLE);
                        b2.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getColor();
                        getSize();
                        noteTextView.setVisibility(View.VISIBLE);
                        noteTextView.setText("* Please select your color and size");
                        colorTextview.setVisibility(View.VISIBLE);
                        s2.setVisibility(View.VISIBLE);
                        sizeTextView.setVisibility(View.VISIBLE);
                        s3.setVisibility(View.VISIBLE);
                        b2.setVisibility(View.VISIBLE);
                    }



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //GET FINAL COLOR CODE
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("---"))
                {
                    finalcolorcode = "";
                }
                else
                {
                    finalcolorcode = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //GET FINAL SIZE CODE
        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("---"))
                {
                    finalsizecode = "";
                }
                else
                {
                    finalsizecode = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //BACK BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GRNInventory_step2.this, GRNInventory.class);
                intent.putExtra("goodReturnNo", goodReturnsNo);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = finalitemcode;
                //final String code = itemcode.getText().toString().trim();
                final String brandCode = s1.getSelectedItem().toString();
                String colorCode = s2.getSelectedItem().toString();
                String sizeCode = s3.getSelectedItem().toString();

                String cc="";
                String sc="";

                String color;
                final String size;

                if(colorCode=="---"){
                    color ="";
                    cc = "";
                }else{
                    color="/"+colorCode;
                    cc = colorCode;
                }
                if(sizeCode=="---"){
                    size ="";
                    sc = "";
                }else{
                    size="/"+sizeCode;
                    sc = sizeCode;
                }

                final String itemCode= code+color+size;

                if (itemCode.isEmpty()) {
                    Toast.makeText(GRNInventory_step2.this, "Please enter item the code", Toast.LENGTH_SHORT).show();
                } else {
                    insert(itemCode,cc,sc);
                }
            }
        });

    }

    private void insert(final String itemCode, final String color1, final String size1) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());

        InventoryGoodReturnsNo.child(goodReturnsNo).orderByChild("ItemCode").equalTo(itemCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Intent intent = new Intent(GRNInventory_step2.this, SearchInventoryQtyAdjustment.class);
                    intent.putExtra("goodReturnNo", goodReturnsNo);
                    intent.putExtra("itemcode", itemCode);
                    intent.putExtra("Barcode",barcode);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    HashMap<String,Object> insertInventoryGoodReturnsNo = new HashMap<>();
                    insertInventoryGoodReturnsNo.put("Barcode",barcode);
                    insertInventoryGoodReturnsNo.put("ItemCode",itemCode);
                    insertInventoryGoodReturnsNo.put("Qty","0");
                    //  insertInventoryGoodReturnsNo.put("inventoryKey",key);
                    insertInventoryGoodReturnsNo.put("DateAndTime",currentDateandTime);
                    insertInventoryGoodReturnsNo.put("goodReturnsNo",goodReturnsNo);
//                    insertInventoryGoodReturnsNo.put("Color", finalcolorcode);
//                    insertInventoryGoodReturnsNo.put("Size", finalsizecode);
                    insertInventoryGoodReturnsNo.put("Color", color1);
                    insertInventoryGoodReturnsNo.put("Size", size1);


                    InventoryGoodReturnsNo.child(goodReturnsNo).push().setValue(insertInventoryGoodReturnsNo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                Toast.makeText(GRNInventory_step2.this, "Insert Successfully !", Toast.LENGTH_SHORT).show();
                                Intent page = new Intent(GRNInventory_step2.this, GRNInventory_step4.class);
                                page.putExtra("goodReturnNo",goodReturnsNo);
                                page.putExtra("Barcode",barcode);
                                page.putExtra("itemCode",itemCode);
                                startActivity(page);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void getBrand() {
        Maintain.child("Brand").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Brand_class brand_class =ds.getValue(Brand_class.class);
                        listItem.add(brand_class.getBrandCode());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getItemCode()    {
        Maintain.child("Barcode").child(barcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren())
                {
                    ItemCode_class itemCode_class = ds.getValue(ItemCode_class.class);
                    listItemCode.add(itemCode_class.getItemCode());
                }


                itemcodeadapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getColor() {
        Maintain.child("Color").child(barcode).child(finalitemcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Color_class color_class =ds.getValue(Color_class.class);
                        listItem2.add(color_class.getColor());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSize() {
        Maintain.child("Size").child(barcode).child(finalitemcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Size_class size_class =ds.getValue(Size_class.class);
                        listItem3.add(size_class.getSizeName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getShelf() {
        Maintain.child("Shelf").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Shelf_class shelfClass =ds.getValue(Shelf_class.class);
                        listItem4.add(shelfClass.getShelfName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(GRNInventory_step2.this, GRNInventory.class);
        intent.putExtra("goodReturnNo", goodReturnsNo);
        startActivity(intent);
        finish();

    }
}