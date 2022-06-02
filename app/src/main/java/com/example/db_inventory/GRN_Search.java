package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GRN_Search extends AppCompatActivity {

    EditText e1;
    Button b1,b2;
    String code ;
    DatabaseReference GoodReturnsNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grn_search);
        setTitle("Search Good Returns No");

        e1= findViewById(R.id.editText_good_return_no_Search_code);

        b1= findViewById(R.id.btn_search_cancel);
        b2= findViewById(R.id.btn_search_enter);


        GoodReturnsNo= FirebaseDatabase.getInstance().getReference().child("GoodReturnsNo");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = e1.getText().toString().trim();

                GoodReturnsNo.orderByChild("customerCode").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            GoodReturnsNo.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ss) {
                                    String status = ss.child("status").getValue().toString();

                                    if(status.equals("pending"))
                                    {
                                        Intent page = new Intent(GRN_Search.this, GRNList2.class);
                                        page.putExtra("goodReturnNo",code);
                                        startActivity(page);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(GRN_Search.this, "This GRN was done. Try another one", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }else {
                            Toast.makeText(GRN_Search.this, "This good returns no haven't found !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(GRN_Search.this, GRNList.class);
        startActivity(intent);
        finish();

    }
}