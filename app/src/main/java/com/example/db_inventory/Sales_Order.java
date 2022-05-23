package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sales_Order extends AppCompatActivity {

    DatabaseReference saleOrderRef;
    RecyclerView recyclerView;
    TextView totalrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);
        setTitle("eStock_Sales Order List");

        //DECLARE AND LINK THE RECYCLE LIST
        recyclerView = findViewById(R.id.recyclerView_SaleOrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //LINKED WITH SALE ORDER LIST IN FIREBASE
        saleOrderRef = FirebaseDatabase.getInstance().getReference("SalesOrderImport").child("Stock");

        totalrecord = findViewById(R.id.record_SO);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<HouseInventory> saleOrderAdapter = new FirebaseRecyclerOptions.Builder<HouseInventory>()
                .setQuery(saleOrderRef, HouseInventory.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<HouseInventory, SaleOrderViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HouseInventory, SaleOrderViewHolder>(saleOrderAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull SaleOrderViewHolder holder, int position, @NonNull HouseInventory model) {
                holder.setBarcode(model.getBarcode());
                holder.setItemName(model.getItemName());
                holder.setQuantity(model.getQuantity());
                holder.setCost(model.getCost());
                holder.setPrice(model.getPrice());
                totalrecord.setText(String.valueOf(getItemCount()));
            }

            @NonNull
            @Override
            public SaleOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_order, parent, false);
                return new SaleOrderViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    //VIEW HOLDER
    public static class SaleOrderViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public SaleOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setBarcode(String barcode) {
            TextView Barcode = mView.findViewById(R.id.SO_Barcode);
            Barcode.setText(barcode);

        }

        public void setQuantity(String quantity) {
            TextView Qty = mView.findViewById(R.id.SO_Qty);
            Qty.setText(quantity);
        }

        public void setItemName(String itemName) {
            TextView ItemName = mView.findViewById(R.id.SO_itemCode);
            ItemName.setText(itemName);
        }

        public void setPrice(String price) {
            TextView Price = mView.findViewById(R.id.SO_Price);
            Price.setText(price);
        }

        public void setCost(String cost) {
            TextView Cost = mView.findViewById(R.id.SO_Cost);
            Cost.setText(cost);
        }

    }

    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        super.onBackPressed();
        Intent page = new Intent(Sales_Order.this, Home_Page.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        page.putExtra("Users", users);
        startActivity(page);
        finish();

    }
}