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
import com.google.firebase.database.core.Context;

import java.util.List;

public class HouseList_Adapter extends RecyclerView.Adapter<HouseList_Adapter.MyViewHolder>{

    House_List mContext;
    List<House_list_class> mData;
    DatabaseReference databaseReference;

    public HouseList_Adapter(House_List mContext, List<House_list_class> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.house_list_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseList_Adapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.Name.setText(mData.get(position).getName());
        holder.TotalQty.setText(mData.get(position).getTotalQty());
        holder.Total_type.setText(mData.get(position).getTotalType());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String key = mData.get(position).getKey();
                databaseReference= FirebaseDatabase.getInstance().getReference("House").child(key);
                databaseReference.keepSynced(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("Name").getValue().toString().trim();

                        CharSequence option[]=new CharSequence[]{
                                "Enter","Modify","Delete"
                        };

                        AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
                        builder.setTitle("Select Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if (position == 0){

                                    Intent intent=new Intent(mContext,House_Menu.class);
                                    intent.putExtra("Key",key);
                                    intent.putExtra("name",name);
                                    mContext.startActivity(intent);


                                }
                                if (position == 1){
                                    Intent intent=new Intent(mContext,House_Modify.class);
                                    intent.putExtra("Key",key);
                                    intent.putExtra("name",name);
                                    mContext.startActivity(intent);

                                }
                                if (position==2){
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("House");
                                    reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "Deleted  successfully", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
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
            Name=itemView.findViewById(R.id.textView_Name);
            TotalQty=itemView.findViewById(R.id.textView_TotalQty);
            Total_type=itemView.findViewById(R.id.textView_TotalType);
        }
    }
}