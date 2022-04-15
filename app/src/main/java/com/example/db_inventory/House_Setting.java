package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class House_Setting extends AppCompatActivity {

    android.widget.Switch s1,s2;
    String Switch;
    String Switch2;
    Button b1;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_setting);

        s1= (Switch)findViewById(R.id.switch_disable);
        s2= (Switch)findViewById(R.id.switch_edit_spec);

        b1=(Button)findViewById(R.id.btn_setting_confirm);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Switch");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String disable= dataSnapshot.child("Disable").getValue().toString().trim();

                Switch = disable;

                if (disable.equals("Enable")){
                    s1.setChecked(false);
                }else{
                    s1.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NoNeed= dataSnapshot.child("NoNeed").getValue().toString().trim();

                Switch2 = NoNeed;

                if (NoNeed.equals("Need")){
                    s2.setChecked(false);
                }else{
                    s2.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (s1.isChecked()){
                    Switch = "Switch_On";


                }else{
                    Switch = "Enable";

                }
            }
        });



        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (s2.isChecked()){
                    Switch2 = "Switch_On";


                }else{
                    Switch2 = "Need";

                }
            }
        });

        String users=getIntent().getStringExtra("Users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Disable").setValue(Switch).toString().trim();
                databaseReference.child("NoNeed").setValue(Switch2).toString().trim();
                Intent page= new Intent(House_Setting.this, MainActivity.class);
                page.putExtra("Users", users);
                page.putExtra("Switch",Switch);
                startActivity(page);
            }
        });
    }

    @Override
    public void onBackPressed(){
        String users=getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(House_Setting.this, MainActivity.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }
}