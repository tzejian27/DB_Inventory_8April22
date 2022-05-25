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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaleOrderList extends AppCompatActivity {

    TextView totalrecord;
    RecyclerView recyclerView;
    DatabaseReference SaleOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_list);

        //DECLARE RECYCLE VIEW
        recyclerView = findViewById(R.id.recycle_so);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //DECLARE TOTAL SO RECORD
        totalrecord = findViewById(R.id.record_total_so);

        setTitle("eStock_SalesOrder");

        //DECLARE THE GRN REFERENCE
        SaleOrderNo = FirebaseDatabase.getInstance().getReference("SalesOrderNo");
        SaleOrderNo.keepSynced(true);
    }

    public void onStart() {
        super.onStart();

        //GRN ADAPTER
        FirebaseRecyclerOptions<HouseInventory> SO_Adapter = new FirebaseRecyclerOptions.Builder<HouseInventory>()
                .setQuery(SaleOrderNo.orderByChild("status").equalTo("pending"), HouseInventory.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<HouseInventory, SOViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HouseInventory, SOViewHolder>(SO_Adapter) {
            @Override
            protected void onBindViewHolder(@NonNull SOViewHolder holder, int position, @NonNull HouseInventory model) {
                holder.setSalesOrderNo(model.getSalesOrderNo());
                totalrecord.setText(String.valueOf(getItemCount()));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Sales_Order.class);
                        //PASSING THE PARENT NAME (SALE ORDER ID)
                        intent.putExtra("SONum", model.getSalesOrderNo());
                        Toast.makeText(getApplicationContext(), "Entering " + model.getSalesOrderNo(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SOViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //LINKING THE RECYCLE LAYOUT
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sa_recycle, parent, false);
                return new SOViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    //VIEW HOLDER
    public static class SOViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public SOViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSalesOrderNo(String salesOrderNo) {
            TextView tv = mView.findViewById(R.id.tv_SAHouse);
            tv.setText("SO No: ");
            TextView SOCode = mView.findViewById(R.id.textView_SA);
            SOCode.setText(salesOrderNo);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent2SO_back = new Intent(getApplicationContext(), Home_Page.class);
        startActivity(intent2SO_back);
    }
}