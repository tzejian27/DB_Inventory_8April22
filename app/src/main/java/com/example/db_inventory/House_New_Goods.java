package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import android.os.RemoteException;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class House_New_Goods extends AppCompatActivity{

    Button b1,b2;
    EditText e1,e2;

    //Barcode
    public static String barcode;
    private String barcodeStr;
    ScanReader scanReader;

    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode));
            if (barcode!= null){
                barcodeStr = new String(barcode);
                e1.setText(barcodeStr);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_new_goods);


        e1=(EditText)findViewById(R.id.editText_new_goods_barcode);
        e2=(EditText)findViewById(R.id.editText_new_goods_name);

        b1=(Button)findViewById(R.id.btn_new_goods_back);
        b2=(Button)findViewById(R.id.btn_new_goods_next);

        //Barcode Scanning
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(House_New_Goods.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = e1.getText().toString().trim();
                String name= e2.getText().toString().trim();

                if(TextUtils.isEmpty(barcode)){
                    e1.setError("Please enter barcode");
                    return;
                }

                if(TextUtils.isEmpty(name)){
                    e2.setError("Please enter name");
                    return;
                }


                if (barcode.isEmpty()) {
                    Toast.makeText(House_New_Goods.this, "Please enter barcode and Good Name", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(House_New_Goods.this, New_Goods_step2.class);
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("name", name);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(House_New_Goods.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}