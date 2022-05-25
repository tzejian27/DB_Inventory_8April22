package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewGRN extends AppCompatActivity {

    Button btn_enter, btn_cancel;
    EditText edt_grn_code;
    DatabaseReference GoodReturnsNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grn);
        setTitle("CREATE GRN");

        edt_grn_code =  findViewById(R.id.edt_new_grn);

        btn_cancel = (Button) findViewById(R.id.btn_cancel_grn);
        btn_enter = (Button) findViewById(R.id.btn_enter_grn);

        //GRN FIREBASE
        GoodReturnsNo = FirebaseDatabase.getInstance().getReference().child("GoodReturnsNo");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GRN_Home.class);
                startActivity(intent);
                finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerCode=edt_grn_code.getText().toString().replace("/", "|");
                if (customerCode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in Customer Code ", Toast.LENGTH_SHORT).show();
                } else {
                    //CALL ADD FUNCTION
                    add(customerCode);
                }
            }
        });

    }

    private void add(final String customerCode) {
        GoodReturnsNo.child(customerCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //CHECK IS GRN NO ALREADY EXIST
                if (snapshot.exists()){

                    String status = snapshot.child("status").getValue().toString();

                    if(status.equals("pending"))
                    {
                        Intent intent = new Intent(getApplicationContext(), GoodReturnNo_Menu.class);
                        intent.putExtra("goodReturnNo", customerCode);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(NewGRN.this, "This GRN was done. Try another one!", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    HashMap<String,Object> insert = new HashMap<>();
                    insert.put("customerCode",customerCode);
                    insert.put("status", "pending");


                    GoodReturnsNo.child(customerCode).setValue(insert).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                Intent intent = new Intent(getApplicationContext(), GoodReturnNo_Menu.class);
                                intent.putExtra("goodReturnNo", customerCode);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}