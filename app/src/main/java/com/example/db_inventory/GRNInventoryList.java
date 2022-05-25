package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db_inventory.Class.GoodReturns_class;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GRNInventoryList extends AppCompatActivity {

    public static String goodReturnNo;
    public static String Id;
    RecyclerView recyclerView;
    TextView t1;
    LinearLayoutManager layoutManager;
    DatabaseReference InventoryGoodReturnsNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grninventory_list);
        setTitle("Good Returns - Inventory List");

        recyclerView = findViewById(R.id.listView_GRN);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        t1 = findViewById(R.id.record_GRN);

        Intent intent = getIntent();
        goodReturnNo = intent.getStringExtra("goodReturnNo");

        InventoryGoodReturnsNo = FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");
    }

    @Override
    public void onStart() {
        super.onStart();

        //GRN INVENTORY ADAPTER
        FirebaseRecyclerOptions<GoodReturns_class> GRN_IAdapter = new FirebaseRecyclerOptions.Builder<GoodReturns_class>()
                .setQuery(InventoryGoodReturnsNo.child(goodReturnNo), GoodReturns_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<GoodReturns_class, GRNMyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GoodReturns_class, GRNMyViewHolder>(GRN_IAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull GRNMyViewHolder holder, int position, @NonNull GoodReturns_class model) {
                holder.setBarcode(model.getBarcode());
                holder.setItemCode(model.getItemCode());
                holder.setQty(model.getQty());
                holder.setDateAndTime(model.getDateAndTime());
                holder.setColor(model.getColor());
                holder.setSize(model.getSize());
                t1.setText(String.valueOf(getItemCount()));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String bc = model.getBarcode();
                        AlertDialog.Builder builder = new AlertDialog.Builder(GRNInventoryList.this);
                        builder.setTitle("Are you sure you want to delete: " + model.getBarcode() + "")
                                .setCancelable(true)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton("Confrim Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        InventoryGoodReturnsNo.child(goodReturnNo).orderByChild("ItemCode").equalTo(model.getItemCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot ds: snapshot.getChildren()){
                                                        InventoryGoodReturnsNo.child(goodReturnNo).child(ds.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isComplete()){
                                                                    Toast.makeText(GRNInventoryList.this, model.getBarcode() + "is deleted" , Toast.LENGTH_SHORT).show();
                                                                    dialogInterface.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                        AlertDialog alrtBuilder = builder.create();
                        alrtBuilder.show();
                    }
                });


            }

            @NonNull
            @Override
            public GRNMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goodretuns_list_item, parent, false);
                return new GRNMyViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), GoodReturnNo_Menu.class);
        intent.putExtra("goodReturnNo", goodReturnNo);
        startActivity(intent);
        finish();

    }

    public class GRNMyViewHolder extends RecyclerView.ViewHolder {
        TextView Barcode, ItemName, Quantity, Date_and_Time, Color, Size;
        View mView;

        public GRNMyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setBarcode(String barcode) {
            Barcode = mView.findViewById(R.id.textView_InventoryList_Barcode_GRN);
            Barcode.setText(barcode);
        }

        public void setItemCode(String itemCode) {
            ItemName = mView.findViewById(R.id.textView_InventoryList_Name_GRN);
            ItemName.setText(itemCode);
        }

        public void setQty(String qty) {
            Quantity = mView.findViewById(R.id.textView_InventoryList_Qty_GRN);
            Quantity.setText(qty);
        }

        public void setDateAndTime(String dateAndTime) {
            Date_and_Time = mView.findViewById(R.id.textView_ShowDateAndTime_GRN);
            Date_and_Time.setText(dateAndTime);
        }

        public void setColor(String color) {
            Color = mView.findViewById(R.id.textView_InventoryList_Color_GRN);
            Color.setText(color);
        }

        public void setSize(String size) {
            Size = mView.findViewById(R.id.textView_InventoryList_Size_GRN);
            Size.setText(size);
        }
    }
}