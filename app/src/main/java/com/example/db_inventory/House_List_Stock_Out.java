package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class House_List_Stock_Out extends AppCompatActivity {

    ImageView btn_back, btn_search;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView totalrecord;

    //STOCK OUT HOUSE LIST
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list_stock_out);
        setTitle("eStock_House List_Stock Out");

        recyclerView = findViewById(R.id.recyclerView_House_SO);
        LinearLayoutManager layoutManagerHouse = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManagerHouse);

        //LINK TO FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);

        totalrecord = findViewById(R.id.record_SO);

        btn_back = findViewById(R.id.imageView_house_back_SO);
        btn_search = findViewById(R.id.imageView_house_search_SO);

        String users = getIntent().getStringExtra("Users");

        //EXIT BUTTON
        btn_back.setOnClickListener(view -> {
            Intent intent2home = new Intent(House_List_Stock_Out.this, Home_Page.class);
            intent2home.putExtra("Users", users);
            startActivity(intent2home);
        });

        //SEARCH BUTTON
        btn_search.setOnClickListener(v -> {
            Intent page = new Intent(House_List_Stock_Out.this, Search_House.class);
            page.putExtra("Users", users);
            startActivity(page);

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String users = getIntent().getStringExtra("Users");

        FirebaseRecyclerOptions<House_list_class> houseAdapter = new FirebaseRecyclerOptions.Builder<House_list_class>()
                .setQuery(databaseReference, House_list_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<House_list_class, House_List_Stock_Out.HouseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<House_list_class, House_List_Stock_Out.HouseViewHolder>(houseAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull House_List_Stock_Out.HouseViewHolder holder, int position, @NonNull House_list_class model) {
                holder.Name.setText(model.getName());
                holder.TotalQty.setText(model.getTotalQty());
                holder.Total_type.setText(model.getTotalType());
                totalrecord.setText(String.valueOf(getItemCount()));

                holder.mView.setOnClickListener(v -> {

                    final String key = model.getKey();
                    databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
                    databaseReference.keepSynced(true);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String name = Objects.requireNonNull(dataSnapshot.child("Name").getValue()).toString().trim();

                            //ENTER STOCK OUT OF SELECTED HOUSE
                            Intent intent = new Intent(getApplicationContext(), Stock_Out_Scan.class);
                            intent.putExtra("Key", key);
                            intent.putExtra("name", name);
                            intent.putExtra("Users", users);
                            Toast.makeText(getApplicationContext(), "Enter " + name, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                });
            }

            @NonNull
            @Override
            public House_List_Stock_Out.HouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_list_item, parent, false);
                return new HouseViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(House_List_Stock_Out.this, Home_Page.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }

    public static class HouseViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView Name;
        TextView Total_type;
        TextView TotalQty;


        public HouseViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            Name = itemView.findViewById(R.id.textView_Name);
            TotalQty = itemView.findViewById(R.id.textView_TotalQty);
            Total_type = itemView.findViewById(R.id.textView_TotalType);
        }
    }
}