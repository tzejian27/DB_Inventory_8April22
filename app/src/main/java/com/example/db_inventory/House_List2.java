package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

public class House_List2 extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ImageView imageView1, imageView2;
    TextView totalrecord;

    DatabaseReference arightRef;
    String Switch1;
    String Switch2;

    //HOUSELIST WHEN MAKING ***SEARCH***

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list2);
        setTitle("House List");

        totalrecord = findViewById(R.id.record_HL2);

        recyclerView = findViewById(R.id.recyclerView_Inventory_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        imageView1 = findViewById(R.id.imageView_back);
        imageView2 = findViewById(R.id.imageView_search);
        imageView2.setVisibility(View.INVISIBLE);

        imageView1.setOnClickListener(v -> finish());

        databaseReference = FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);
    }

    @Override
    public void onStart() {
        super.onStart();



        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String users = getIntent().getStringExtra("Users");

        //SEARCH FOR HOUSE NAME START WITH "NAME"
        //WE ARE USING START AT AND END AT TO MAKE SURE SEARCH METHOD
        //CAN SEARCH ONLY BY FIRST CHARACTER

        /*BUT STILL CANNOT DETECT BOTH UPPER AND LOWER CASE, SO WHEN SEARCH VARIABLE ARE RETURN RESULT FROM SAME UPPER/LOWER CASE*/
        FirebaseRecyclerOptions<House_list_class> houseAdapter = new FirebaseRecyclerOptions.Builder<House_list_class>()
                .setQuery(databaseReference.orderByChild("Name").startAt(name).endAt(name+"~"), House_list_class.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<House_list_class, AllUsersViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<House_list_class, AllUsersViewHolder>(houseAdapter) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull House_list_class model) {
                holder.setName(model.getName());
                holder.setTotalQty(model.getTotalQty());
                holder.setTotalType(model.getTotalType());
                String houseName=String.valueOf(model.getName());
                totalrecord.setText(String.valueOf(getItemCount()));

                arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");
                arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Switch1 = snapshot.child("SW_ModifyDelete").getValue().toString().trim();
                        Switch2 = snapshot.child("SW_DataClear").getValue().toString().trim();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.mView.setOnClickListener(v -> {
                    final String key = getRef(position).getKey();
                    CharSequence[] option = new CharSequence[]{
                            "Enter", "Modify", "Stock In", "Stock Out"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(House_List2.this);
                    builder.setTitle("Select Option");
                    builder.setItems(option, (dialog, position1) -> {
                        //ENTER HOUSE
                        if (position1 == 0) {
                            Intent intent1 = new Intent(House_List2.this, House_Menu.class);
                            intent1.putExtra("Key", key);
                            intent1.putExtra("name", houseName);
                            intent1.putExtra("Users", users);
                            startActivity(intent1);
                            finish();

                        }
                        //ENTER MODIFY HOUSE NAME
                        if (position1 == 1) {
                            if(Switch1.equals("On")){
                                Intent intent1 = new Intent(House_List2.this, House_Modify.class);
                                intent1.putExtra("Key", key);
                                intent1.putExtra("name", houseName);
                                intent1.putExtra("Users", users);
                                startActivity(intent1);
                                finish();
                            }else if(Switch1.equals("Off")){
                                Toast.makeText(getApplicationContext(), "Modify Access Right Off", Toast.LENGTH_LONG).show();
                            }

                        }
                        //ENTER STOCK IN
                        if (position1 == 2) {
                            Intent intent1 = new Intent(House_List2.this, Stock_In_Scan.class);
                            intent1.putExtra("Key", key);
                            intent1.putExtra("name", houseName);
                            intent1.putExtra("Users", users);
                            Toast.makeText(getApplicationContext(), "Enter " + houseName, Toast.LENGTH_SHORT).show();
                            startActivity(intent1);
                        }
                        //ENTER STOCK OUT
                        if (position1 == 3) {
                            Intent intent1 = new Intent(House_List2.this, Stock_Out_Scan.class);
                            intent1.putExtra("Key", key);
                            intent1.putExtra("name", houseName);
                            intent1.putExtra("Users", users);
                            Toast.makeText(getApplicationContext(), "Enter " + houseName, Toast.LENGTH_SHORT).show();
                            startActivity(intent1);
                        }

                    });
                    builder.show();
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
            TextView Name = mView.findViewById(R.id.textView_Name);
            Name.setText(name);
        }

        public void setTotalQty(String totalQty) {
            TextView TotalQty = mView.findViewById(R.id.textView_TotalQty);
            TotalQty.setText(totalQty);
        }

        public void setTotalType(String totalType) {
            TextView TotalType = mView.findViewById(R.id.textView_TotalType);
            TotalType.setText(totalType);
        }


    }
}