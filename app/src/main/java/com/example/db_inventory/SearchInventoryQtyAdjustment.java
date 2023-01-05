package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class SearchInventoryQtyAdjustment extends AppCompatActivity {

    TextView tb, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, colorText, sizeText, qtyText, shelfText;
    public static EditText e1;
    Button b1, b2;
    public static String barcode, goodReturnsNo, itemcode, sizecode, brandcode, colorcode;
    String itemname;
    String Qty;
    String size;
    String color;
    String brand;
    String currentDateandTime;
    String Key;
    String bc;
    String code;
    public static String CheckKey;

    public static String Id;
    public static String SPECTEXT1 = "";
    public static String SPECTEXT2 = "";
    public static String SPECTEXT3 = "";
    public static String SPECTEXT4 = "";
    public static String SPECTEXT5 = "";
    public static String SPECTEXT6 = "";
    public static String SPECTEXT7 = "";
    public static String SPECTEXT8 = "";
    public static String SPECTEXT9 = "";
    public static String SPECTEXT10 = "";
    public static String SPECTEXT11 = "";
    public static String SPECTEXT12 = "";
    public static String SPECTEXT13 = "";
    public static String SPECTEXT14 = "";
    String column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13, column14;
    ScanDevice sm;
    private final static String SCAN_ACTION = "scan.rcv.message";
    private String barcodeStr;
    private ScanReader scanReader ;

    DatabaseReference InventoryGoodReturnsNo;
    DatabaseReference Maintain;

    private final String RES_ACTION = "android.intent.action.SCANRESULT";
    private BroadcastReceiver scanReceiver;
    ScannerInterface  scanner;

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");

            if (intent.getAction().equals(RES_ACTION)){

                if(scanResult.length()>0){
                    barcodeStr = scanResult;
                }
            }
        }
    }

    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode)) ;
            if (barcode != null) {
                barcodeStr = new String(barcode);
                //add();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grninventory_step4);
        //SET TITLE
        setTitle("Good Returns - Qty Adjustment");

        //CONNECT SCANNER
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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //GET INTENT DATA
        Intent intent1 = getIntent();
        goodReturnsNo = intent1.getStringExtra("goodReturnNo");
        itemcode = intent1.getStringExtra("itemcode");
        barcode = intent1.getStringExtra("Barcode");

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

        e1 = (EditText) findViewById(R.id.editText_Inventory_barcode_GRN);
        e1.setEnabled(false);

        InventoryGoodReturnsNo.child(goodReturnsNo).orderByChild("ItemCode").equalTo(itemcode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ss) {
                        if(ss.exists())
                        {
                            for(DataSnapshot ds: ss.getChildren())
                            {
                                CheckKey = ds.getKey();
                            }
                            InventoryGoodReturnsNo.child(goodReturnsNo).child(CheckKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        bc = snapshot.child("Barcode").getValue().toString();

                                        Qty=snapshot.child("Qty").getValue().toString();
                                        color = snapshot.child("Color").getValue().toString();
                                        size = snapshot.child("Size").getValue().toString();
                                        //Key=snapshot.getKey();

                                        e1.setText(bc);
                                        t2.setText(brand);
                                        t3.setText(itemcode);
                                        colorText.setText(color);
                                        sizeText.setText(size);
                                        qtyText.setText(Qty);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }else{
                            autoadd();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        b1 = (Button) findViewById(R.id.btn_Inventory_esc_GRN);
        b2 = (Button) findViewById(R.id.btn_Inventory_enter_GRN);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchInventoryQtyAdjustment.this, GoodReturnNo_Menu.class);
                intent.putExtra("goodReturnNo", goodReturnsNo);
                startActivity(intent);
                finish();

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchInventoryQtyAdjustment.this, GRNInventory_step5.class);
                intent.putExtra("Barcode", barcode);
                intent.putExtra("goodReturnNo", goodReturnsNo);
                intent.putExtra("Qty", Qty);
                intent.putExtra("itemCode", itemcode);
                intent.putExtra("Key", CheckKey);
                startActivity(intent);

            }
        });

    }

    public void add()    {
        InventoryGoodReturnsNo.child(goodReturnsNo).child(CheckKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {


                    int qty = Integer.parseInt(Qty);
                    String sum = String.valueOf(qty + 1);
                    InventoryGoodReturnsNo.child(goodReturnsNo).child(CheckKey).child("Qty").setValue(sum).toString().trim();

                }
                else
                {
                    Toast.makeText(SearchInventoryQtyAdjustment.this, "Invalid Barcode in Good Returns: "+goodReturnsNo+"", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void autoadd()    {
        InventoryGoodReturnsNo.child(goodReturnsNo).child(CheckKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {


                    int qty = Integer.parseInt(Qty);
                    String sum = String.valueOf(qty + 1);
                    InventoryGoodReturnsNo.child(goodReturnsNo).child(CheckKey).child("Qty").setValue(sum).toString().trim();
                    Toast.makeText(SearchInventoryQtyAdjustment.this, "1 Quantity added", Toast.LENGTH_SHORT).show();


                }
                else
                {
                    Toast.makeText(SearchInventoryQtyAdjustment.this, "Invalid Barcode in Good Returns: "+goodReturnsNo+"", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(SearchInventoryQtyAdjustment.this, GoodReturnNo_Menu.class);
        intent.putExtra("goodReturnNo",goodReturnsNo);
        startActivity(intent);
        finish();

    }
}