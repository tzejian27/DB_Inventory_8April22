package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class House_List2 extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ImageView imageView1,imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Inventory_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        imageView1=(ImageView)findViewById(R.id.imageView_back);
        imageView2=(ImageView)findViewById(R.id.imageView_search) ;
        imageView2.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);
    }

    @Override
    public void onStart(){
        super.onStart();


        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String users=getIntent().getStringExtra("Users");

        FirebaseRecyclerOptions<House_list_class> houseAdapter = new FirebaseRecyclerOptions.Builder<House_list_class>()
                .setQuery(databaseReference.orderByChild("Name").equalTo(name), House_list_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<House_list_class, AllUsersViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<House_list_class, AllUsersViewHolder>(houseAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull House_list_class model) {
                holder.setName (model.getName());
                holder.setTotalQty (model.getTotalQty());
                holder.setTotalType (model.getTotalType());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String key = getRef(position).getKey();
                        CharSequence option[]=new CharSequence[]{
                                "Enter","Modify","Stock In"
                        };

                        AlertDialog.Builder builder =new AlertDialog.Builder(House_List2.this);
                        builder.setTitle("Select Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if (position == 0){

                                    Intent intent=new Intent(House_List2.this,House_Menu.class);
                                    intent.putExtra("Key",key);
                                    intent.putExtra("name",name);
                                    intent.putExtra("Users", users);
                                    startActivity(intent);
                                    finish();

                                }
                                if (position == 1){
                                    Intent intent=new Intent(House_List2.this,House_Modify.class);
                                    intent.putExtra("Key",key);
                                    intent.putExtra("name",name);
                                    intent.putExtra("Users", users);
                                    startActivity(intent);
                                    finish();
                                }
                                if (position==2){
                                    Intent intent=new Intent(House_List2.this,Stock_In_Scan.class);
                                    intent.putExtra("Key",key);
                                    intent.putExtra("name",name);
                                    intent.putExtra("Users", users);
                                    startActivity(intent);
                                }

                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_list_item, parent, false);
                return new AllUsersViewHolder(view);
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

        public void setName(String name) {
            TextView Name = (TextView) mView.findViewById(R.id.textView_Name);
            Name.setText(name);
        }

        public void setTotalQty(String totalQty) {
            TextView TotalQty = (TextView) mView.findViewById(R.id.textView_TotalQty);
            TotalQty.setText(totalQty);
        }

        public void setTotalType(String totalType) {
            TextView TotalType = (TextView) mView.findViewById(R.id.textView_TotalType);
            TotalType.setText(totalType);
        }


    }
}