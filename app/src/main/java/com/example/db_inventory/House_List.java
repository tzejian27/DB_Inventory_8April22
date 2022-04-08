package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_House);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        databaseReference= FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);

        btn_back=findViewById(R.id.imageView_house_back);
        btn_search=(ImageView)findViewById(R.id.imageView_house_search);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2home = new Intent(House_List.this, MainActivity.class);
                startActivity(intent2home);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page= new Intent(House_List.this,Search_House.class);
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

                postList=new ArrayList<>();
                for (DataSnapshot postsnap:dataSnapshot.getChildren()){
                    House_list_class list =postsnap.getValue(House_list_class.class);
                    postList.add(list);
                }
                HouseList_Adapter= new HouseList_Adapter(House_List.this,postList);
                recyclerView.setAdapter(HouseList_Adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(House_List.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}