package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GRNInventory_step5 extends AppCompatActivity {

    Button b1,b2,b3;
    TextView t1;
    EditText e1;
    String barcode;
    String goodReturnsNo;
    String Qty;
    String itemCode;
    TextView textViewItemCode;
    String Key;
    DatabaseReference InventoryGoodReturnsNo;
    DatabaseReference Maintain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grninventory_step5);
        setTitle("Good Returns - Modify Quantity");

        //GET INTENT
        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("Barcode");
        goodReturnsNo = intent1.getStringExtra("goodReturnNo");
        itemCode = intent1.getStringExtra("itemCode");
        Qty = intent1.getStringExtra("Qty");
        Key = intent1.getStringExtra("Key");

        textViewItemCode = findViewById(R.id.textView15);
        t1= findViewById(R.id.textView_Inventory_step5_totalQty_grn);
        textViewItemCode.setText("Item Code: "+itemCode);
        t1.setText(Qty);

        e1= findViewById(R.id.editText_Inventory_step5_Qty_grn);

        b1= findViewById(R.id.btn_Inventory_step5_cancel_grn);
        b2= findViewById(R.id.btn_Inventory_step5_add_grn);
        b3= findViewById(R.id.btn_Inventory_step5_change_grn);

        //CONNECT FIREBASE
        InventoryGoodReturnsNo= FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");
        Maintain= FirebaseDatabase.getInstance().getReference().child("Maintain");



        //CANCEL BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(GRNInventory_step5.this, GRNInventory_step4.class);
                page.putExtra("goodReturnNo",goodReturnsNo);
                page.putExtra("Barcode",barcode);
                page.putExtra("itemCode",itemCode);
                startActivity(page);
                finish();
            }
        });

        //ADD BUTTON
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String qty = e1.getText().toString().trim();

                if(qty.isEmpty()){
                    Toast.makeText(GRNInventory_step5.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                }else{
                    final int total= Integer.parseInt(qty);

                    final int qty1= Integer.parseInt(Qty);
                    int sum= qty1+total;
                    String sum2 = String.valueOf(sum);

                    HashMap<String,Object> update = new HashMap<>();
                    update.put("Qty",sum2);

                    InventoryGoodReturnsNo.child(goodReturnsNo).child(Key).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(GRNInventory_step5.this, "Add Successfully", Toast.LENGTH_SHORT).show();
                            Intent page = new Intent(GRNInventory_step5.this, GRNInventory_step4.class);
                            page.putExtra("goodReturnNo",goodReturnsNo);
                            page.putExtra("Barcode",barcode);
                            page.putExtra("itemCode",itemCode);
                            startActivity(page);
                            finish();
                        }
                    });


                }
            }
        });

        //CHANGE BUTTON
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty = e1.getText().toString().trim();

                if(qty.isEmpty()){
                    Toast.makeText(GRNInventory_step5.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String,Object> update = new HashMap<>();
                    update.put("Qty",qty);

                    InventoryGoodReturnsNo.child(goodReturnsNo).child(Key).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(GRNInventory_step5.this, "Change Successfully", Toast.LENGTH_SHORT).show();
                            Intent page = new Intent(GRNInventory_step5.this, GRNInventory_step4.class);
                            page.putExtra("goodReturnNo",goodReturnsNo);
                            page.putExtra("Barcode",barcode);
                            page.putExtra("itemCode",itemCode);
                            startActivity(page);
                            finish();
                        }
                    });
                }
            }
        });


    }
}