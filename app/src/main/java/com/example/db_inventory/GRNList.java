package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class GRNList extends AppCompatActivity {

    ImageView iv_back, iv_search;
    TextView totalrecord;
    RecyclerView recyclerView;
    DatabaseReference GoodReturnsNo;
    DatabaseReference InventoryGoodReturnsNo;
    public static String goodReturnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grnlist);

        //IMG BUTTON
        iv_back = findViewById(R.id.iv_back);
        iv_search = findViewById(R.id.iv_search);

        //DECLARE RECYCLE VIEW
        recyclerView = findViewById(R.id.recycle_grn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //DECLARE TOTAL GRN RECORD
        totalrecord = findViewById(R.id.record_grn);

        setTitle("eStock_GRN");

        Intent intent = getIntent();
        goodReturnNo = intent.getStringExtra("goodReturnNo");

        //DECLARE THE GRN REFERENCE
        GoodReturnsNo = FirebaseDatabase.getInstance().getReference("GoodReturnsNo");
        GoodReturnsNo.keepSynced(true);
        InventoryGoodReturnsNo = FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(getApplicationContext(), GRN_Home.class);
                page.putExtra("goodReturnNo", goodReturnNo);
                startActivity(page);
                finish();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent page= new Intent(GRNList.this, GRN_Search.class);
                startActivity(page);
            }
        });
    }

    public void onStart() {
        super.onStart();

        //GRN ADAPTER
        FirebaseRecyclerOptions<GoodReturnNo_class> GRN_Adapter = new FirebaseRecyclerOptions.Builder<GoodReturnNo_class>()
                .setQuery(GoodReturnsNo.orderByChild("status").equalTo("pending"), GoodReturnNo_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<GoodReturnNo_class, GRNViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GoodReturnNo_class, GRNViewHolder>(GRN_Adapter) {
            @Override
            protected void onBindViewHolder(@NonNull GRNViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull GoodReturnNo_class model) {
                holder.setCustomerCode(model.getCustomerCode());
                totalrecord.setText(String.valueOf(getItemCount()));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence option[]=new CharSequence[]{
                                "Enter","Delete"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(GRNList.this);
                        builder.setTitle("Select Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0){
                                    Intent intent = new Intent(getApplicationContext(), GoodReturnNo_Menu.class);
                                    //PASSING THE PARENT NAME (GRN NO)
                                    intent.putExtra("goodReturnNo", model.getCustomerCode());
                                    Toast.makeText(getApplicationContext(), "Entering " + model.getCustomerCode(), Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                                if (i == 1) {
                                    final AlertDialog.Builder warningDialog = new AlertDialog.Builder(GRNList.this);
                                    warningDialog.setCancelable(false);
                                    warningDialog.setTitle("Warning !");
                                    warningDialog.setMessage("Confirm to delete this Good Returns No ? ");

                                    warningDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            InventoryGoodReturnsNo.child(model.getCustomerCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        Toast.makeText(GRNList.this, "This good return no is used , cannot be delete", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        GoodReturnsNo.child(model.getCustomerCode()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isComplete()){
                                                                    Toast.makeText(GRNList.this, "Deleted Successfully !", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });
                                    warningDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    warningDialog.show();

                                }
                            }
                        });
                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public GRNViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //LINKING THE RECYCLE LAYOUT
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sa_recycle, parent, false);
                return new GRNViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    //VIEW HOLDER
    public static class GRNViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public GRNViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setCustomerCode(String customerCode) {
            TextView tv = mView.findViewById(R.id.tv_SAHouse);
            tv.setText("GRN No: ");
            TextView CustomerCode = mView.findViewById(R.id.textView_SA);
            CustomerCode.setText(customerCode);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent2GRN_back = new Intent(getApplicationContext(), GRN_Home.class);
        startActivity(intent2GRN_back);
    }
}