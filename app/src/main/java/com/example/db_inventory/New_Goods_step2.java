package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class New_Goods_step2 extends AppCompatActivity {

    EditText e1,e2;
    Button b1,b2;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goods_step2);

        e1= (EditText)findViewById(R.id.editText_New_Goods_price) ;
        e2=(EditText) findViewById(R.id.editText_New_Goods_cost);
        b1=(Button)findViewById(R.id.btn_new_goods_back2);
        b2=(Button)findViewById(R.id.btn_new_goods_next2);

        databaseReference= FirebaseDatabase.getInstance().getReference("New_Goods");
        databaseReference.keepSynced(true);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(New_Goods_step2.this,House_New_Goods.class);
                startActivity(intent);
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String barcode = intent.getStringExtra("barcode");
                String name = intent.getStringExtra("name");
                String price = e1.getText().toString().trim();
                String cost = e2.getText().toString().trim();

                String barcode_ref = barcode + "/";
                final String key =databaseReference.getKey();

                if(TextUtils.isEmpty(price)){
                    e1.setError("Required Field...");
                    return;
                }

                if(TextUtils.isEmpty(cost)){
                    e2.setError("Required Field...");
                    return;
                }

                // if (price.isEmpty() || cost.isEmpty()) {
                //     Toast.makeText(New_Goods_step2.this, "Please enter price and cost  ", Toast.LENGTH_SHORT).show();
                //  } else {
                Map dataMap = new HashMap();
                dataMap.put("Barcode", barcode);
                dataMap.put("Name", name);
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