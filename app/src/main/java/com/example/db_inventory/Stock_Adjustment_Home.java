package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Stock_Adjustment_Home extends AppCompatActivity {

    DatabaseReference SA_Ref;
    RecyclerView recyclerView;
    TextView totalrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjustment_home);

        //DECLARE RECYCLE VIEW
        recyclerView = findViewById(R.id.recyclerView_SA);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //DECLARE TOTAL SA RECORD
        totalrecord = findViewById(R.id.record_SA);

        setTitle("eStock_Stock Adjust List");

        //DECLARE THE SA REFERENCE
        SA_Ref = FirebaseDatabase.getInstance().getReference("StockAdjustmentNo");
        SA_Ref.keepSynced(true);
    }

    public void onStart() {
        super.onStart();

        //GET AND SHOW THE STOCK ADJUSTMENT MADE IN SIDE SERVER

        FirebaseRecyclerOptions<SA_class> SA_Adapter = new FirebaseRecyclerOptions.Builder<SA_class>()
                .setQuery(SA_Ref.orderByChild("SAHouse"), SA_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<SA_class, SAViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SA_class, SAViewHolder>(SA_Adapter) {
            @Override
            protected void onBindViewHolder(@NonNull SAViewHolder holder, int position, @NonNull SA_class model) {
                //RECYCLE VIEW SLIDE FROM LEFT ANIMATION
                Animation animation = AnimationUtils.loadAnimation(holder.mView.getContext(), android.R.anim.slide_in_left);
                holder.mView.startAnimation(animation);

                holder.setSAHouse(model.getSAHouse());
                totalrecord.setText(String.valueOf(getItemCount()));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Stock_Adjustment_List.class);
                        //PASSING THE PARENT NAME (STOCK ADJUSTMENT ID)
                        intent.putExtra("SAName", model.getSAHouse());
                        Toast.makeText(Stock_Adjustment_Home.this, "Entering " + model.getSAHouse(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //LINKING THE RECYCLE LAYOUT
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sa_recycle, parent, false);
                return new SAViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    //VIEW HOLDER
    public static class SAViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public SAViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setSAHouse(String SAHouse) {
            TextView saHouse = mView.findViewById(R.id.textView_SA);
            saHouse.setText(SAHouse);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Home_Page.class);
        startActivity(intent);
        finish();

    }


}