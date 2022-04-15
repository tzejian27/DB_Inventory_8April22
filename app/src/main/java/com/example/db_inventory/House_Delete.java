package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class House_Delete extends AppCompatActivity {

    ImageView btn_back, btn_search;
    DatabaseReference databaseReference;

    private RecyclerView recyclerView_delete;
    private DatabaseReference mHouseDatabase;
    private DeleteHouseAdapter deleteHouseAdapter;
    private List<House_list_class> myDataList;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_delete);

        mAuth=FirebaseAuth.getInstance();
        mHouseDatabase=FirebaseDatabase.getInstance().getReference("House");
        mHouseDatabase.keepSynced(true);

        recyclerView_delete =(RecyclerView)findViewById(R.id.recyclerView_Delete);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView_delete.setHasFixedSize(true);
        recyclerView_delete.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        deleteHouseAdapter=new DeleteHouseAdapter(House_Delete.this,myDataList);
        recyclerView_delete.setAdapter(deleteHouseAdapter);

        readItem();


        btn_back=findViewById(R.id.imageView_delete_back);
        btn_search=findViewById(R.id.imageView_delete_search);

        String users=getIntent().getStringExtra("Users");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2home = new Intent(House_Delete.this, MainActivity.class);
                intent2home.putExtra("Users", users);
                startActivity(intent2home);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent page = new Intent(House_Delete.this,Search_House_Delete.class);
                startActivity(page);*/
            }
        });
    }

    private void readItem() {
        mHouseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    House_list_class list =snapshot.getValue(House_list_class.class);
                    myDataList.add(list);
                }

                deleteHouseAdapter= new DeleteHouseAdapter(House_Delete.this,myDataList);
                recyclerView_delete.setAdapter(deleteHouseAdapter);
                deleteHouseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        String users=getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(House_Delete.this, MainActivity.class);
        startActivity(intent);
        intent.putExtra("Users", users);
        finish();

    }

}