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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GRNInventory extends AppCompatActivity {

    public static EditText e1;
    public static String barcode;
    Button b1, b2;
    String currentDateandTime;
    ScanReader scanReader;
    private String barcodeStr;
    DatabaseReference InventoryGoodReturnsNo;
    DatabaseReference Maintain;

        //GET SCANNED BARCODE
    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode));
            if (barcode != null) {
                barcodeStr = new String(barcode);
                e1.setText(barcodeStr);
                b2.performClick();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grninventory);
        setTitle("Good Returns - Create Inventory");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //INIT SCANNER
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        //WIDGET DECLARATION
        e1=(EditText)findViewById(R.id.edt_grn_in_Barcode);
        b1=(Button)findViewById(R.id.btn_grn_in_back);
        b2=(Button)findViewById(R.id.btn_grn_in_next);

        //TIME DECLARATION
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        currentDateandTime = sdf.format(new Date());

        //GET INTENT
        Intent intent = getIntent();
        final String grnno = intent.getStringExtra("goodReturnNo");

        //CONNECT FIREBASE
        InventoryGoodReturnsNo= FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");
        Maintain= FirebaseDatabase.getInstance().getReference().child("Maintain");

        //BACK
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GoodReturnNo_Menu.class);
                intent.putExtra("goodReturnNo",grnno);
                startActivity(intent);
                finish();
            }
        });

        //ADD
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = e1.getText().toString().trim();
                if(barcode.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter/scan barcode  ", Toast.LENGTH_SHORT).show();
                }else{
                    add();
                }
            }
        });
    }

    private void add() {
        //GET INTENT
        Intent intent = getIntent();
        final String grnno = intent.getStringExtra("goodReturnNo");

        barcode = e1.getText().toString().trim();

        InventoryGoodReturnsNo.child(grnno).orderByChild("Barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ss) {


                Maintain.child("Barcode").child(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){


                            Intent page = new Intent(getApplicationContext(), GRNInventory_step2.class);
                            page.putExtra("Barcode",barcode);
                            page.putExtra("goodReturnNo",grnno);
                            startActivity(page);
                            finish();


                        }
                        else
                        {
                            Toast.makeText(GRNInventory.this, "Invalid Barcode. Please try again", Toast.LENGTH_SHORT).show();
                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}