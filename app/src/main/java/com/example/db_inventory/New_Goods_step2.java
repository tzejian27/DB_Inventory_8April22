package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class New_Goods_step2 extends AppCompatActivity {

    EditText e1, e2;
    Button button_next, button_cancel;
    DatabaseReference databaseReference;

    //cost and price for new good
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goods_step2);

        e1 = findViewById(R.id.editText_New_Goods_price);
        e2 = findViewById(R.id.editText_New_Goods_cost);
        button_cancel = findViewById(R.id.btn_new_goods_back2);
        button_next = findViewById(R.id.btn_new_goods_next2);

        databaseReference = FirebaseDatabase.getInstance().getReference("New_Goods");
        databaseReference.keepSynced(true);

        String users = getIntent().getStringExtra("Users");


        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(New_Goods_step2.this, House_New_Goods.class);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String barcode = intent.getStringExtra("barcode");
                String name = intent.getStringExtra("name");
                String itemcode = intent.getStringExtra("ItemCode");
                String price = e1.getText().toString().trim();
                String cost = e2.getText().toString().trim();

                String barcode_ref = barcode + "/";

                if (TextUtils.isEmpty(price)) {
                    e1.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(cost)) {
                    e2.setError("Required Field...");
                    return;
                }

                // if (price.isEmpty() || cost.isEmpty()) {
                //     Toast.makeText(New_Goods_step2.this, "Please enter price and cost  ", Toast.LENGTH_SHORT).show();
                //  } else {
                Map dataMap = new HashMap();
                dataMap.put("Barcode", barcode);
                dataMap.put("Name", name);
                dataMap.put("ItemCode", itemcode);
                dataMap.put("Price", price);
                dataMap.put("Cost", cost);

                Map dataMap2 = new HashMap();
                dataMap2.put(barcode_ref + "/", dataMap);
                databaseReference.updateChildren(dataMap2);//.addOnCompleteListener(new OnCompleteListener() {
                //    @Override
                //     public void onComplete(@NonNull Task task) {
                //       if (task.isSuccessful()) {
                Toast.makeText(New_Goods_step2.this, "Data Save", Toast.LENGTH_SHORT).show();
                Intent page = new Intent(New_Goods_step2.this, MainActivity.class);
                page.putExtra("Users", users);
                startActivity(page);
                finish();
                //       }else{
                //           Toast.makeText(New_Goods_step2.this, "Error !!!", Toast.LENGTH_SHORT).show();
                //         }

                //    }
                //   });

                //  }
            }
        });
    }
}