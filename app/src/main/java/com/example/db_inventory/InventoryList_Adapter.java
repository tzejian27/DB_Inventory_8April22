package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//No more used
public class InventoryList_Adapter extends RecyclerView.Adapter<InventoryList_Adapter.MyViewHolder> {

    Inventory_List mContext;
    List<Inventory_class> mData;


    public InventoryList_Adapter(Inventory_List mContext, List<Inventory_class> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.inventory_recycle_list_item, parent, false);
        return new MyViewHolder(row);
    }

    public void onBindViewHolder(@NonNull InventoryList_Adapter.MyViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        final Inventory_class model = mData.get(position);


        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        viewHolder.setBarcode(model.getBarcode());
        viewHolder.setQuanlity(model.getQuantity());
        viewHolder.setItemName(model.getItemName());
        viewHolder.setDate_and_Time(model.getDate_and_Time());
        viewHolder.setPrice(model.getPrice());
        viewHolder.setItemCode(model.getItemCode());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final String key2 = mData.get(position).getKey();
                CharSequence[] option = new CharSequence[]{
                        "Spec", "Delete", "Modify"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Select Option");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        if (position == 0) {

                            Intent intent = new Intent(mContext, Item_Spec.class);
                            intent.putExtra("Key", key);
                            intent.putExtra("Key2", key2);
                            intent.putExtra("name", name);
                            mContext.startActivity(intent);


                        }
                        if (position == 1) {
                            Intent intent = new Intent(mContext, Inventory_Delete_Confirm.class);
                            intent.putExtra("Key", key);
                            intent.putExtra("Key2", key2);
                            intent.putExtra("name", name);
                            mContext.startActivity(intent);
                        }

                        if (position == 2) {


                            Intent intent = new Intent(mContext, Item_Spec_Modify.class);
                            intent.putExtra("name", name);
                            intent.putExtra("Barcode", model.getBarcode());
                            intent.putExtra("Key", key);
                            intent.putExtra("Key2", key2);
                            mContext.startActivity(intent);

                        }
                    }
                });
                builder.show();
            }
        });
    }

    private Intent getIntent() {
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        return intent;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setBarcode(String barcode) {
            TextView Barcode = mView.findViewById(R.id.textView_InventoryList_Barcode);
            Barcode.setText(barcode);
        }

        public void setQuanlity(String quanlity) {
            TextView Qty = mView.findViewById(R.id.textView_InventoryList_Qty);
            Qty.setText(quanlity);
        }

        public void setItemName(String itemName) {
            TextView ItemName = mView.findViewById(R.id.textView_InventoryList_Name);
            ItemName.setText(itemName);
        }

        public void setDate_and_Time(String date_and_time) {
            TextView Date_and_Time = mView.findViewById(R.id.textView_InventoryList_Date_Time);
            Date_and_Time.setText(date_and_time);
        }

        public void setPrice(String price) {
            TextView Price = mView.findViewById(R.id.textView_InventoryList_Price);
            Price.setText(price);
        }

        public void setItemCode(String itemCode){
            TextView ItemCode = mView.findViewById(R.id.textView_InventoryList_itemCode);
            ItemCode.setText(itemCode);
        }



    }
}
