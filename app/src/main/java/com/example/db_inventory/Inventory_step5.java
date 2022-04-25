package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Inventory_step5 extends AppCompatActivity {

    Button b1, b2, b3;
    TextView t1;
    EditText e1;
    String key;
    String barcode;
    String Quantity;
    String key2;
    DatabaseReference databaseReference, databaseReference2;
    String totalQty;
    String name;

    //Modify Inventory qty
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_step5);

        Intent intent1 = getIntent();
        barcode = intent1.getStringExtra("barcode");
        key = intent1.getStringExtra("Key");
        key2 = intent1.getStringExtra("Key2");
        name = intent1.getStringExtra("name");


        t1 = findViewById(R.id.textView_Inventory_step5_totalQty);

        e1 = findViewById(R.id.editText_Inventory_step5_Qty);

        b1 = findViewById(R.id.btn_Inventory_step5_cancel);
        //b2=(Button)findViewById(R.id.btn_Inventory_step5_add);
        b3 = findViewById(R.id.btn_Inventory_step5_change);

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Quantity = dataSnapshot.child("Quantity").getValue().toString().trim();

                t1.setText(Quantity);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String users = getIntent().getStringExtra("Users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("name", name);
                intent.putExtra("Key", key);
                intent.putExtra("Key2", key2);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        /*b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String qty = e1.getText().toString().trim();


                if(qty.isEmpty()){
                    Toast.makeText(Inventory_step5.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                }else{
                    final int total= Integer.parseInt(qty);
                    final int qty1= Integer.parseInt(Quantity);
                    int sum= qty1+total;
                    String sum2 = String.valueOf(sum);
                    databaseReference.child(key2).child("Quantity").setValue(sum2);//.addOnCompleteListener(new OnCompleteListener<Void>() {
                    //   @Override
                    //    public void onComplete(@NonNull Task<Void> task) {
                    //       if (task.isSuccessful()){
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();

                            int sum = 0;
                            int totalQty1 = Integer.parseInt(totalQty);
                            sum = totalQty1 + total;
                            String sum2 = String.valueOf(sum);

                            databaseReference.child("TotalQty").setValue(sum2).toString().trim();

                            Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                            intent.putExtra("barcode", barcode);
                            intent.putExtra("Key",key);
                            intent.putExtra("Key2",key2);
                            intent.putExtra("name",name);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //   }
                    //     }
                    //    });
                    Toast.makeText(Inventory_step5.this, "Add Successfully !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty = e1.getText().toString().trim();

                if (qty.isEmpty()) {
                    Toast.makeText(Inventory_step5.this, "Please enter  Qty", Toast.LENGTH_SHORT).show();
                } else {

                    final int total = Integer.parseInt(qty);
                    final int qty1 = Integer.parseInt(Quantity);
                    databaseReference.child(key2).child("Quantity").setValue(qty);//.addOnCompleteListener(new OnCompleteListener<Void>() {
                    //   @Override
                    //   public void onComplete(@NonNull Task<Void> task) {
                    //    if (task.isSuccessful()){
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalQty = dataSnapshot.child("TotalQty").getValue().toString().trim();

                            int totalQty1 = Integer.parseInt(totalQty);

                            long sum = 0;

                            if (total < qty1) {
                                sum = totalQty1 - (qty1 - total);
                                String sum2 = Long.toString(sum);
                                databaseReference.child("TotalQty").setValue(sum2).toString().trim();
                                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                                intent.putExtra("Key", key);
                                intent.putExtra("Key2", key2);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                finish();

                            } else if (total == qty1) {
                                sum = totalQty1;
                                String sum2 = Long.toString(sum);
                                databaseReference.child("TotalQty").setValue(sum2).toString().trim();
                                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                                intent.putExtra("Key", key);
                                intent.putExtra("Key2", key2);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                finish();

                            } else if (total > qty1) {
                                sum = totalQty1 + (total - qty1);
                                String sum2 = Long.toString(sum);
                                databaseReference.child("TotalQty").setValue(sum2).toString().trim();
                                Intent intent = new Intent(Inventory_step5.this, Inventory_step4.class);
                                intent.putExtra("Key", key);
                                intent.putExtra("Key2", key2);
                                intent.putExtra("barcode", barcode);
                                intent.putExtra("name", name);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //     }
                    //    }
                    //  });
                    Toast.makeText(Inventory_step5.this, "Change Successfully !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}