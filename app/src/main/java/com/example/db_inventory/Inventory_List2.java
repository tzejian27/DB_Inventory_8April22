package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class Inventory_List2 extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ImageView imageView1, imageView2;
    String totaltype;
    int t_type;
    int sum;
    String sum2;
    TextView totalrecord;
    DatabaseReference arightRef;
    String Switch1;

    //Inventory List when search
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list2);
        recyclerView = findViewById(R.id.recyclerView_Inventory_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        totalrecord = findViewById(R.id.record_IL2);

        imageView1 = findViewById(R.id.imageView_back);
        imageView2 = findViewById(R.id.imageView_search);
        imageView2.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        String users = getIntent().getStringExtra("Users");

        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(Inventory_List2.this, House_Menu.class);
                page.putExtra("name", name);
                page.putExtra("Key", key);
                page.putExtra("Users", users);
                startActivity(page);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totaltype = Objects.requireNonNull(dataSnapshot.child("TotalType").getValue()).toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String users = getIntent().getStringExtra("Users");


        Intent intent = getIntent();
        final String barcode = intent.getStringExtra("Barcode");
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        FirebaseRecyclerOptions<Inventory_class> inventoryAdapter = new FirebaseRecyclerOptions.Builder<Inventory_class>()
                .setQuery(databaseReference.orderByChild("Barcode").startAt(barcode).endAt(barcode+"~"), Inventory_class.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Inventory_class, AllUsersViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Inventory_class, AllUsersViewHolder>(inventoryAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull Inventory_class model) {
                viewHolder.setBarcode(model.getBarcode());
                viewHolder.setQuanlity(model.getQuantity());
                viewHolder.setItemName(model.getItemName());
                viewHolder.setDate_and_Time(model.getDate_and_Time());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setItemCode(model.getItemCode());
                totalrecord.setText(String.valueOf(getItemCount()));

                arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    //checking the switch
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Switch1 = snapshot.child("SW_EditSpec").getValue().toString().trim();
                        if(Switch1.equals("On")){
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String key2 = getRef(position).getKey();
                                    CharSequence[] option = new CharSequence[]{
                                            "Spec", "Delete", "Modify"
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(Inventory_List2.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int position) {
                                            if (position == 0) {

                                                Intent intent = new Intent(Inventory_List2.this, Item_Spec.class);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("name", model.getItemName());
                                                intent.putExtra("Barcode", model.getBarcode());
                                                intent.putExtra("ItemName", model.getItemName());
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                                finish();

                                            }
                                            if (position == 1) {
                                                Intent intent = new Intent(Inventory_List2.this, Inventory_Delete_Confirm.class);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("name", model.getItemName());
                                                intent.putExtra("ItemName", model.getItemName());
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                            }
                                            if (position == 2) {
                                                Intent intent = new Intent(Inventory_List2.this, Inventory_step5.class);
                                                intent.putExtra("name", model.getItemName());
                                                intent.putExtra("barcode", model.getBarcode());
                                                intent.putExtra("ItemName", model.getItemName());
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });

                        } else if (Switch1.equals("Off")) {
                            Toast.makeText(Inventory_List2.this, "Edit Permission denied", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_recycle_list_item, parent, false);
                return new Inventory_List2.AllUsersViewHolder(view);
            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter2);
        firebaseRecyclerAdapter2.startListening();
    }


    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setBarcode(String barcode) {
            TextView Barcode = mView.findViewById(R.id.textView_InventoryList_Barcode);
            Barcode.setText(barcode);
        }

        public void setQuanlity(String quanlity) {
            TextView Qty = mView.findViewById(R.id.textView_InventoryList_Qty);
            Qty.setText(quanlity);
        }

        public void setItemName(String itemName) {
            TextView ItemName = mView.findViewById(R.id.textView_InventoryList_Name);
            ItemName.setText(itemName);
        }

        public void setDate_and_Time(String date_and_time) {
            TextView Date_and_Time = mView.findViewById(R.id.textView_InventoryList_Date_Time);
            Date_and_Time.setText(date_and_time);
        }

        public void setPrice(String price) {
            TextView Price = mView.findViewById(R.id.textView_InventoryList_Price);
            Price.setText(price);
        }

        public void setItemCode(String itemCode){
            TextView ItemCode = mView.findViewById(R.id.textView_InventoryList_itemCode);
            ItemCode.setText(itemCode);
        }


    }


}