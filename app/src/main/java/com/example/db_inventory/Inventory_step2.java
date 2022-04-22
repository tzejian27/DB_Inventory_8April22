package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Inventory_step2 extends AppCompatActivity {

    Button b1, b2;
    EditText e1, e2;
    DatabaseReference databaseReference;
    ScanReader scanReader;
    private String itemCodeStr;
    public static String itemcode;

    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] itemcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "itemcode = " + new String(itemcode));
            if (itemcode != null) {
                String itemCodeScan = e2.getText().toString().trim();
                itemCodeStr = new String(itemcode);
                e2.setText(itemCodeStr);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step2);

        b1 = findViewById(R.id.btn_inventory_back2);
        b2 = findViewById(R.id.btn_inventory_next2);
        e1 = findViewById(R.id.editText_Inventory_name);
        e2 = findViewById(R.id.editText_input_itemcode);

        //Barcode Scanning
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        Intent intent = getIntent();
        final String key = intent.getStringExtra("Key");
        final String key2 = intent.getStringExtra("Key2");
        String barcode = intent.getStringExtra("barcode");
        final String name = intent.getStringExtra("name");
        String users = getIntent().getStringExtra("Users");
        String itemCode = intent.getStringExtra("ItemCode");

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String key = intent1.getStringExtra("Key");
                String name = intent1.getStringExtra("name");
                Intent intent = new Intent(Inventory_step2.this, Inventory.class);
                intent.putExtra("Key", key);
                intent.putExtra("name", name);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String barcode = intent1.getStringExtra("barcode");
                // String name = intent1.getStringExtra("name");
                final String itemName = e1.getText().toString().trim();
                final String itemCode = e2.getText().toString().trim();
                String barcode_ref = barcode + "/";

                if (itemName.isEmpty()) {
                    Toast.makeText(Inventory_step2.this, "Please enter item name  ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Inventory_step2.this, Inventory_step3.class);
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("Key", key);
                    intent.putExtra("name", name);
                    intent.putExtra("Users", users);
                    intent.putExtra("ItemCode", itemCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}