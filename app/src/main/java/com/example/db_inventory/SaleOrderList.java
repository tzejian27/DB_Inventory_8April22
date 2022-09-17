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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaleOrderList extends AppCompatActivity {

    //PENDING
    TextView totalrecord;
    RecyclerView recyclerView;
    DatabaseReference SaleOrderNo;

    //COMPLETE
    TextView totalrecord2;
    RecyclerView recyclerView2;
    DatabaseReference SaleOrderNo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_list);

        //DECLARE RECYCLE VIEW (PENDING)
        recyclerView = findViewById(R.id.recycle_so);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //DECLARE RECYCLE VIEW (COMPLETE)
        recyclerView2 = findViewById(R.id.recycle_so_complete);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setHasFixedSize(true);

        //DECLARE TOTAL SO RECORD
        totalrecord = findViewById(R.id.record_total_so);

        totalrecord2 = findViewById(R.id.record_total_so_complete);

        setTitle("eStock_SalesOrder");

        //DECLARE THE GRN REFERENCE
        SaleOrderNo = FirebaseDatabase.getInstance().getReference("SalesOrderNo");
        SaleOrderNo.keepSynced(true);
    }

    public void onStart() {
        super.onStart();

        //SALES ORDER ADAPTER (PENDING)
        FirebaseRecyclerOptions<HouseInventory> SO_Adapter = new FirebaseRecyclerOptions.Builder<HouseInventory>()
                .setQuery(SaleOrderNo.orderByChild("status").equalTo("pending"), HouseInventory.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<HouseInventory, SOViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HouseInventory, SOViewHolder>(SO_Adapter) {
            @Override
            protected void onBindViewHolder(@NonNull SOViewHolder holder, int position, @NonNull HouseInventory model) {

                //RECYCLE VIEW SLIDE FROM LEFT ANIMATION
                Animation animation = AnimationUtils.loadAnimation(holder.mView.getContext(), android.R.anim.fade_in);
                animation.setDuration(1000);
                holder.mView.startAnimation(animation);

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

        //SALES ORDER ADAPTER (COMPLETE)
        FirebaseRecyclerOptions<HouseInventory> SO_COM_Adapter = new FirebaseRecyclerOptions.Builder<HouseInventory>()
                .setQuery(SaleOrderNo.orderByChild("status").equalTo("completed"), HouseInventory.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<HouseInventory, SOViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<HouseInventory, SOViewHolder>(SO_COM_Adapter) {
            @Override
            protected void onBindViewHolder(@NonNull SOViewHolder holder, int position, @NonNull HouseInventory model) {

                //RECYCLE VIEW SLIDE FROM LEFT ANIMATION
                Animation animation = AnimationUtils.loadAnimation(holder.mView.getContext(), android.R.anim.fade_in);
                holder.mView.startAnimation(animation);

                holder.setSalesOrderNo(model.getSalesOrderNo());
                totalrecord2.setText(String.valueOf(getItemCount()));

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
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.sa_recycle, parent, false);
                return new SOViewHolder(view2);
            }
        };

        recyclerView2.setAdapter(firebaseRecyclerAdapter1);
        firebaseRecyclerAdapter1.startListening();

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