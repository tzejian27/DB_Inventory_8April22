package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HouseList_Adapter extends RecyclerView.Adapter<HouseList_Adapter.MyViewHolder> {

    House_List mContext;
    List<House_list_class> mData;
    DatabaseReference databaseReference;
    DatabaseReference arightRef;
    String Switch1;
    String Switch2;

    public HouseList_Adapter(House_List mContext, List<House_list_class> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.house_list_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseList_Adapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.Name.setText(mData.get(position).getName());
        holder.TotalQty.setText(mData.get(position).getTotalQty());
        holder.Total_type.setText(mData.get(position).getTotalType());

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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String key = mData.get(position).getKey();
                databaseReference = FirebaseDatabase.getInstance().getReference("House").child(key);
                databaseReference.keepSynced(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("Name").getValue().toString().trim();

                        CharSequence[] option = new CharSequence[]{
                                "Enter", "Modify", "Delete"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Select Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                //ENTER HOUSE STOKE TAKE
                                if (position == 0) {
                                    Intent intent = new Intent(mContext, House_Menu.class);
                                    intent.putExtra("Key", key);
                                    intent.putExtra("name", name);
                                    mContext.startActivity(intent);
                                }
                                //ENTER HOUSE NAME MODIFY
                                //CHECK USER ACCESS RIGHT FOR HOUSE NAME MODIFY
                                if (position == 1) {
                                    if (Switch1.equals("On")) {
                                        Intent intent = new Intent(mContext, House_Modify.class);
                                        intent.putExtra("Key", key);
                                        intent.putExtra("name", name);
                                        mContext.startActivity(intent);

                                    } else if (Switch1.equals("Off")) {
                                        Toast.makeText(mContext, "Modify Access Right Off", Toast.LENGTH_LONG).show();
                                    }

                                }
                                //DELETE CONFIRMATION
                                if (position == 2) {

                                    if (Switch2.equals("On")) {
                                        CharSequence[] option2 = new CharSequence[]{
                                                "Delete", "Cancel"
                                        };
                                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(mContext);
                                        deleteBuilder.setTitle("Are you confirm to delete the house?");
                                        deleteBuilder.setItems(option2, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (i == 0) {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("House");
                                                    reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(mContext, "Deleted  successfully", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }

                                                if (i == 1) {
                                                    dialog.cancel();
                                                }


                                            }
                                        });
                                        deleteBuilder.show();

                                    } else if (Switch2.equals("Off")) {
                                        Toast.makeText(mContext, "Delete House Access Right Off", Toast.LENGTH_LONG).show();
                                    }


                                }
                                /*if (position == 3) {
                                    Intent intent = new Intent(mContext, StockMovement.class);
                                    intent.putExtra("Key", key);
                                    intent.putExtra("name", name);
                                    mContext.startActivity(intent);

                                }*/

                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView Name;
        TextView Total_type;
        TextView TotalQty;


        public MyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            Name = itemView.findViewById(R.id.textView_Name);
            TotalQty = itemView.findViewById(R.id.textView_TotalQty);
            Total_type = itemView.findViewById(R.id.textView_TotalType);
        }
    }
}
