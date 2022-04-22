package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class House_List extends AppCompatActivity {

    ImageView btn_back, btn_search;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    HouseList_Adapter HouseList_Adapter;
    List<House_list_class> postList;
    TextView t1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list);

        t1 = findViewById(R.id.record_HL);

        recyclerView = findViewById(R.id.recyclerView_House);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);

        btn_back = findViewById(R.id.imageView_house_back);
        btn_search = findViewById(R.id.imageView_house_search);
        String users = getIntent().getStringExtra("Users");


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2home = new Intent(House_List.this, MainActivity.class);
                intent2home.putExtra("Users", users);
                startActivity(intent2home);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(House_List.this, Search_House.class);
                page.putExtra("Users", users);
                startActivity(page);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        //Get List  Posts from the database

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList = new ArrayList<>();
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    House_list_class list = postsnap.getValue(House_list_class.class);
                    postList.add(list);
                }
                HouseList_Adapter = new HouseList_Adapter(House_List.this, postList);
                recyclerView.setAdapter(HouseList_Adapter);
                t1.setText(String.valueOf(HouseList_Adapter.getItemCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(House_List.this, MainActivity.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }
}