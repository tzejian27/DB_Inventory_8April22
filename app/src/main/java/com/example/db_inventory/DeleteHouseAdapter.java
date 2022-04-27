package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

//old house delete recycle view adapter
//but "Not more used"
public class DeleteHouseAdapter extends RecyclerView.Adapter<DeleteHouseAdapter.ViewHolder> {

    private final Context mContext;
    private final List<House_list_class> myDataList;
    private String post_key = "";

    private String Name = "";
    private String Total_type = "";
    private String TotalQty = "";

    public DeleteHouseAdapter(Context mContext, List<House_list_class> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.house_list_item, parent, false);
        return new DeleteHouseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteHouseAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final House_list_class data = myDataList.get(position);
        //Show data
        holder.Name.setText(myDataList.get(position).getName());
        holder.TotalQty.setText(myDataList.get(position).getTotalQty());
        holder.Total_type.setText(myDataList.get(position).getTotalType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = myDataList.get(position).getKey();

                Name = data.getName();
                TotalQty = data.getTotalQty();
                Total_type = data.getTotalType();

                CharSequence[] option = new CharSequence[]{
                        "Delete"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Select Option");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("House");
                        reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Total_type;
        TextView TotalQty;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.textView_Name);
            TotalQty = itemView.findViewById(R.id.textView_TotalQty);
            Total_type = itemView.findViewById(R.id.textView_TotalType);
        }
    }
}
