package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_new_house, btn_house_list, btn_house_delete, btn_generate_txt, btn_setting, btn_new_goods;
    DatabaseReference databaseReference, databaseReference2;
    String s1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Front page for DB Inventory

        //Get root data from firebase as reference
        databaseReference= FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);

        //Create the buttons and set their onClickListener to "this"
        btn_new_house=findViewById(R.id.btn_new_house);
        btn_house_list=findViewById(R.id.btn_house_list);
        btn_house_delete=findViewById(R.id.btn_house_delete);
        btn_generate_txt=findViewById(R.id.btn_gen_txt);
        btn_setting=findViewById(R.id.btn_house_setting);
        btn_new_goods=findViewById(R.id.btn_new_goods);

        btn_new_house.setOnClickListener(this);
        btn_house_list.setOnClickListener(this);
        btn_house_delete.setOnClickListener(this);
        btn_generate_txt.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_new_goods.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_new_house:
                Intent intent2new_house = new Intent(MainActivity.this, House_New_House.class);
                startActivity(intent2new_house);
                break;
            case R.id.btn_house_list:
                Intent intent2house_list = new Intent(MainActivity.this, House_List.class);
                startActivity(intent2house_list);
                break;
            case R.id.btn_house_delete:
                Toast.makeText(this, "Function Already Moved to House List!!!", Toast.LENGTH_LONG).show();
                break;
                //Intent intent2house_delete = new Intent(MainActivity.this, House_Delete.class);
                //startActivity(intent2house_delete);
            case R.id.btn_gen_txt:

                Intent intent2house_generate = new Intent(MainActivity.this, Generate_Txt.class);
                startActivity(intent2house_generate);
                //Toast.makeText(this, "Coming Soon!!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_house_setting:
                Intent intent2house_setting = new Intent(MainActivity.this, House_Setting.class);
                startActivity(intent2house_setting);
                break;
            case R.id.btn_new_goods:

                databaseReference2=FirebaseDatabase.getInstance().getReference("Switch");
                databaseReference2.keepSynced(true);
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        s1 = dataSnapshot.child("Disable").getValue().toString().trim();

                        if (s1.equals("Switch_On")){
                            Toast.makeText(MainActivity.this,"New Good is Disable", Toast.LENGTH_SHORT).show();
                        }else if (s1.equals("Enable")){
                            Intent intent2new_goods = new Intent(MainActivity.this, House_New_Goods.class);
                            startActivity(intent2new_goods);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                break;
        }
    }

    public void onBackPressed(){
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        super.onBackPressed();
        Intent page = new Intent(MainActivity.this, Home_Page.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        startActivity(page);
        finish();
    }
}