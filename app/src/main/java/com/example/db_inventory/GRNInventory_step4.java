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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GRNInventory_step4 extends AppCompatActivity {
    TextView tb, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, colorText, sizeText, qtyText;
    public static EditText e1;
    Button b1, b2;
    public static String barcode, goodReturnsNo, itemcode;
    String Qty;
    String size;
    String color;
    String brand;
    String currentDateandTime;
    String Key;

    private String barcodeStr;
    private ScanReader scanReader ;
    public static String code;

    DatabaseReference InventoryGoodReturnsNo;
    DatabaseReference Maintain;

    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode)) ;
            if (barcode != null) {
                barcodeStr = new String(barcode);
                add();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grninventory_step4);
        setTitle("Good Returns - Details");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //GET INTENT
        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("Barcode");
        goodReturnsNo = intent1.getStringExtra("goodReturnNo");
        itemcode = intent1.getStringExtra("itemCode");

        //CONNECT FIREBASE
        InventoryGoodReturnsNo= FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");
        Maintain= FirebaseDatabase.getInstance().getReference().child("Maintain");


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());


        t2 = (TextView) findViewById(R.id.textView_Inventory_Brand_GRN);
        t2.setVisibility(View.GONE);
        tb = findViewById(R.id.textView17);
        tb.setVisibility(View.GONE);
        t3 = (TextView) findViewById(R.id.textView_Inventory_name_GRN);
        qtyText = findViewById(R.id.qtytext_GRN);
        colorText = findViewById(R.id.colortext_GRN);
        sizeText = findViewById(R.id.sizeText_GRN);

        t4 = (TextView) findViewById(R.id.textView1_1);
        t4.setVisibility(View.GONE);

        t5 = (TextView) findViewById(R.id.textView2_2);
        t5.setVisibility(View.GONE);

        t6 = (TextView) findViewById(R.id.textView3_3);
        t6.setVisibility(View.GONE);

        t7 = (TextView) findViewById(R.id.textView4_4);
        t7.setVisibility(View.GONE);

        t8 = (TextView) findViewById(R.id.textView5_5);
        t8.setVisibility(View.GONE);

        t9 = (TextView) findViewById(R.id.textView6_6);
        t9.setVisibility(View.GONE);

        t10 = (TextView) findViewById(R.id.textView7_7);
        t10.setVisibility(View.GONE);

        t11 = (TextView) findViewById(R.id.textView8_8);
        t11.setVisibility(View.GONE);

        t12 = (TextView) findViewById(R.id.textView9_9);
        t12.setVisibility(View.GONE);

        t13 = (TextView) findViewById(R.id.textView10_10);
        t13.setVisibility(View.GONE);

        t14 = (TextView) findViewById(R.id.textView11_11);
        t14.setVisibility(View.GONE);

        t26 = (TextView) findViewById(R.id.textView12_12);
        t26.setVisibility(View.GONE);

        t27 = (TextView) findViewById(R.id.textView13_13);
        t27.setVisibility(View.GONE);

        t28 = (TextView) findViewById(R.id.textView14_14);
        t28.setVisibility(View.GONE);

        t15 = (TextView) findViewById(R.id.textView1);
        t15.setVisibility(View.GONE);

        t16 = (TextView) findViewById(R.id.textView2);
        t16.setVisibility(View.GONE);

        t17 = (TextView) findViewById(R.id.textView3);
        t17.setVisibility(View.GONE);

        t18 = (TextView) findViewById(R.id.textView4);
        t18.setVisibility(View.GONE);

        t19 = (TextView) findViewById(R.id.textView5);
        t19.setVisibility(View.GONE);

        t20 = (TextView) findViewById(R.id.textView6);
        t20.setVisibility(View.GONE);

        t21 = (TextView) findViewById(R.id.textView7);
        t21.setVisibility(View.GONE);

        t22 = (TextView) findViewById(R.id.textView8);
        t22.setVisibility(View.GONE);

        t23 = (TextView) findViewById(R.id.textView9);
        t23.setVisibility(View.GONE);

        t24 = (TextView) findViewById(R.id.textView10);
        t24.setVisibility(View.GONE);

        t25 = (TextView) findViewById(R.id.textView11);
        t25.setVisibility(View.GONE);

        t29 = (TextView) findViewById(R.id.textView12);
        t29.setVisibility(View.GONE);

        t30 = (TextView) findViewById(R.id.textView13);
        t30.setVisibility(View.GONE);

        t31 = (TextView) findViewById(R.id.textView14);
        t31.setVisibility(View.GONE);

        InventoryGoodReturnsNo.child(goodReturnsNo).orderByChild("Barcode").equalTo(barcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    code = ds.child("ItemCode").getValue().toString();
                    if (code.equals(itemcode)) {

                        Qty=ds.child("Qty").getValue().toString();
                        color = ds.child("Color").getValue().toString();
                        size = ds.child("Size").getValue().toString();
                        Key=ds.getKey();


                        t2.setText(brand);
                        t3.setText(itemcode);
                        colorText.setText(color);
                        sizeText.setText(size);
                        qtyText.setText(Qty);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        e1 = (EditText) findViewById(R.id.editText_Inventory_barcode_GRN);
        e1.setText(barcode);
        e1.setEnabled(false);


        b1 = (Button) findViewById(R.id.btn_Inventory_esc_GRN);
        b2 = (Button) findViewById(R.id.btn_Inventory_enter_GRN);




        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GRNInventory_step4.this, GoodReturnNo_Menu.class);
                intent.putExtra("goodReturnNo", goodReturnsNo);
                startActivity(intent);
                finish();

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GRNInventory_step4.this, GRNInventory_step5.class);
                intent.putExtra("Barcode", barcode);
                intent.putExtra("goodReturnNo", goodReturnsNo);
                intent.putExtra("Qty", Qty);
                intent.putExtra("itemCode", itemcode);
                intent.putExtra("Key", Key);
                startActivity(intent);
                finish();
            }
        });
    }

    public void add()    {
        // final String Barcode = barcodeStr.trim();
        InventoryGoodReturnsNo.child(goodReturnsNo).orderByChild("ItemCode").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        String key = ds.getKey();
                        int qty = Integer.parseInt(Qty);
                        String sum = String.valueOf(qty + 1);
                        InventoryGoodReturnsNo.child(goodReturnsNo).child(key).child("Qty").setValue(sum).toString().trim();
                    }


                }
                else
                {
                    Toast.makeText(GRNInventory_step4.this, "Invalid Barcode in Good Returns: "+goodReturnsNo+"", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(GRNInventory_step4.this, GoodReturnNo_Menu.class);
        intent.putExtra("goodReturnNo",goodReturnsNo);
        startActivity(intent);
        finish();

    }
}