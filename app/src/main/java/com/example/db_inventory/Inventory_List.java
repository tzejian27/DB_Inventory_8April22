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

import java.util.List;

public class Inventory_List extends AppCompatActivity {

    DatabaseReference databaseReference, switchRef;
    RecyclerView recyclerView;
    ImageView iv_back, iv_search;
    String barcode;
    String totaltype;
    int t_type;
    int sum;
    String sum2, switch1;
    InventoryList_Adapter InventoryList_Adapter;
    List<Inventory_class> postList;
    TextView totalrecord;

    DatabaseReference arightRef;
    String Switch1;
    String Switch2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        recyclerView = findViewById(R.id.recyclerView_Inventory_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        iv_back = findViewById(R.id.imageView_IL_back);
        iv_search = findViewById(R.id.imageView_IL_search);
        totalrecord = findViewById(R.id.record_IL);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        String users = getIntent().getStringExtra("Users");

        setTitle("eStock_Inventory List_" + name);

        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent page = new Intent(Inventory_List.this, House_Menu.class);
                page.putExtra("name", name);
                page.putExtra("Key", key);
                page.putExtra("Users", users);
                startActivity(page);
                finish();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory_List.this, Search.class);
                intent.putExtra("name", name);
                intent.putExtra("Key", key);
                intent.putExtra("Users", users);
                startActivity(intent);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
        databaseReference.keepSynced(true);
        switchRef = FirebaseDatabase.getInstance().getReference("Switch");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totaltype = dataSnapshot.child("TotalType").getValue().toString().trim();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        switchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switch1 = snapshot.child("NoNeed").getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onStart() {
        super.onStart();
        String users = getIntent().getStringExtra("Users");

        // final String name = getActivity().getIntent().getExtras().get("visit_hairstylist").toString();
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");


        FirebaseRecyclerOptions<Inventory_class> inventoryAdapter = new FirebaseRecyclerOptions.Builder<Inventory_class>()
                .setQuery(databaseReference.orderByChild("HouseKey").equalTo(key), Inventory_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Inventory_class, AllUsersViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Inventory_class, AllUsersViewHolder>(inventoryAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull Inventory_class model) {
                viewHolder.setBarcode(model.getBarcode());
                viewHolder.setQuantity(model.getQuantity());
                viewHolder.setItemName(model.getItemName());
                viewHolder.setDate_and_Time(model.getDate_and_Time());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setCost(model.getCost());
                viewHolder.setItemCode(model.getItemCode());
                //totalrecord.setText(model.getBarcode().length());
                    totalrecord.setText(String.valueOf(getItemCount()));

                /*for(int i=0;i<model.getBarcode().length(); i++){
                    totalrecord.setText(String.valueOf(i-1));
                }*/

                arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Switch1 = snapshot.child("SW_EditSpec").getValue().toString().trim();
                        if (Switch1.equals("On")) {
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String key2 = getRef(position).getKey();
                                    CharSequence[] option = new CharSequence[]{
                                            "Spec", "Delete", "Modify"
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(Inventory_List.this);
                                    builder.setTitle("Select Option");

                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int position) {
                                            if (position == 0) {
                                                if (switch1.equals("Need")) {
                                                    Intent intent = new Intent(Inventory_List.this, Item_Spec.class);
                                                    intent.putExtra("Key", key);
                                                    intent.putExtra("Key2", key2);
                                                    intent.putExtra("name", name);
                                                    intent.putExtra("ItemName", model.getItemName());
                                                    intent.putExtra("Barcode", model.getBarcode());
                                                    intent.putExtra("Users", users);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Edit Spec is Not Allowed", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                            if (position == 1) {
                                                Intent intent = new Intent(Inventory_List.this, Inventory_Delete_Confirm.class);
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("name", name);
                                                intent.putExtra("ItemName", model.getItemName());
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                            }
                                            if (position == 2) {

                                                //Modify Inventory
                                                Intent intent = new Intent(Inventory_List.this, Inventory_step5.class);
                                                intent.putExtra("name", name);
                                                intent.putExtra("barcode", model.getBarcode());
                                                intent.putExtra("Key", key);
                                                intent.putExtra("Key2", key2);
                                                intent.putExtra("ItemName", model.getItemName());
                                                intent.putExtra("Users", users);
                                                startActivity(intent);
                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            });

                        } else if (Switch1.equals("Off")) {
                            Toast.makeText(Inventory_List.this, "Edit Permission denied", Toast.LENGTH_LONG).show();

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
                return new AllUsersViewHolder(view);
            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter2);
        firebaseRecyclerAdapter2.startListening();
        //totalrecord.setText(String.valueOf(recyclerView.getAdapter().getItemCount()));
    }

    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        super.onBackPressed();
        Intent page = new Intent(Inventory_List.this, House_Menu.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        page.putExtra("Users", users);
        startActivity(page);
        finish();

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

        public void setQuantity(String quantity) {
            TextView Qty = mView.findViewById(R.id.textView_InventoryList_Qty);
            Qty.setText(quantity);
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

        public void setCost(String cost) {
            TextView Cost = mView.findViewById(R.id.textView_InventoryList_Cost);
            Cost.setText(cost);
        }

        public void setItemCode(String itemCode){
            TextView ItemCode = mView.findViewById(R.id.textView_InventoryList_itemCode);
            ItemCode.setText(itemCode);
        }


    }

}