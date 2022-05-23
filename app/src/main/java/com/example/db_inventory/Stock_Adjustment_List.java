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

public class Stock_Adjustment_List extends AppCompatActivity {

    DatabaseReference SA_Ref;
    RecyclerView recyclerView;
    String total;
    TextView totalrecord;
    TextView sa_name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjustment_list);

        //DECLARE RECYCLE VIEW
        recyclerView = findViewById(R.id.recyclerView_SA_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //DECLARE TOTAL SA RECORD
        totalrecord = findViewById(R.id.record_SA_List);


        //GET STOCK ADJUSTMENT NAME FROM ADJUSTMENT HOME
        Intent intent = getIntent();
        final String sa_name = intent.getStringExtra("SAName");

        //SETTING THE STOCK ADJUSTMENT ID AS TITLE OF THE PAGE
        setTitle("eStock_Stock Adjust List " + sa_name);

        sa_name1 = findViewById(R.id.SA_name1);
        sa_name1.setText(sa_name);


        //DECLARE THE STOCK ADJUSTMENT REFERENCE
        SA_Ref = FirebaseDatabase.getInstance().getReference("StockAdjustment").child(sa_name);
        SA_Ref.keepSynced(true);
    }

    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<SA_class> SAList_Adapter = new FirebaseRecyclerOptions.Builder<SA_class>()
                .setQuery(SA_Ref, SA_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<SA_class, SAListViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SA_class, SAListViewHolder>(SAList_Adapter) {
            @Override
            protected void onBindViewHolder(@NonNull SAListViewHolder holder, int position, @NonNull SA_class model) {
                //GET THE DATA TO BE SHOWED IN EACH DATA OF VIEW HOLDER
                holder.setBarcode(model.getBarcode());
                holder.setBookQty(model.getBookQty());
                holder.setQty(model.getQty());
                holder.setAdj(model.getAdj());

                totalrecord.setText(String.valueOf(getItemCount()));

            }

            @NonNull
            @Override
            public SAListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sa_list_recycle, parent, false);
                return new SAListViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    //VIEW HOLDER
    public static class SAListViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public SAListViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setBarcode(String barcode) {
            TextView Barcode = mView.findViewById(R.id.textView_barcode_SA);
            Barcode.setText(barcode);
        }

        public void setBookQty(String bookQty) {
            TextView BookQty = mView.findViewById(R.id.textView_bookqty_SA);
            BookQty.setText(bookQty);
        }

        public void setQty(String qty) {
            TextView Qty = mView.findViewById(R.id.textView_stqty_SA);
            Qty.setText(qty);
        }

        public void setAdj(int adj) {
            TextView Adj = mView.findViewById(R.id.textView_stkadjust_SA);
            Adj.setText(String.valueOf(adj));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Stock_Adjustment_Home.class);
        startActivity(intent);
        finish();

    }
}