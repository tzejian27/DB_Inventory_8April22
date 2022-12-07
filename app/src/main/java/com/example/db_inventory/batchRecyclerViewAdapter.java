package com.example.db_inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class batchRecyclerViewAdapter extends RecyclerView.Adapter<batchRecyclerViewAdapter.MyViewHolder> {

    StockOut_Checkout mContext;
    public static List<batchItem> mData;
    DatabaseReference arightRef;
    String Switch1;
    String Switch2;
    boolean isOnTextChanged =false;
    private int FinalSubTotal;
    ArrayList<String> ExpAmtArray = new ArrayList<>();

    public batchRecyclerViewAdapter(StockOut_Checkout mContext, List<batchItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.checkout_item, parent, false);
        return new MyViewHolder(row).linkAdapter(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull batchRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.batchQtyIpt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isOnTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                FinalSubTotal = 0;
                int ammendValue = (editable.toString().equals(""))?0:Integer.parseInt(editable.toString());
                int availableValueBatch = mData.get(position).getNumericQuantity();
                if(ammendValue>availableValueBatch){
                    holder.batchQtyIpt.setText(availableValueBatch+"");
                    mData.get(position).setPreset_ipt_qty(availableValueBatch);
                }else{
                    mData.get(position).setPreset_ipt_qty(ammendValue);
                }

                computeResult();
            }
        });
        // Set data of the list
        holder.batchNum.setText(mData.get(position).getBatchNum());
        holder.batchQuantity.setText(mData.get(position).getStringQuantity());
        holder.batchQtyIpt.setText(mData.get(position).getPreset_ipt_qty()+"");

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] option = new CharSequence[]{
                        "Confirm", "Cancel"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Remove from the list ?");
                builder.setItems(option, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i){
                            case 0 : {
                                //Remove the item from the recycler view
                                if(position!= 0 ) {
                                    mData.remove(position);
                                    computeResult();
                                    notifyItemRemoved(position);
                                }
                                break;
                            }
                            case 1:{
                                dialogInterface.dismiss();
                                break;
                            }
                        }
                    }
                });
                AlertDialog alrtBuilder = builder.create();
                alrtBuilder.show();

            }
        });

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

    }

    private void computeResult() {
        // Compute the required quantity
        int stockOutAmount = Integer.parseInt(StockOut_Checkout.txt_totalStockout.getText().toString());
        int sumOfAll=0;

        for (batchItem item
             : mData) {
            sumOfAll += item.getPreset_ipt_qty();
        }
        StockOut_Checkout.txt_subtotal.setText(sumOfAll+"");
        int stockRequired = stockOutAmount - sumOfAll;
        StockOut_Checkout.txt_totalRequired.setText(stockRequired+"");
        if(stockRequired<0){
            StockOut_Checkout.txt_totalRequired.setTextColor(Color.RED);
        }
        else{
            StockOut_Checkout.txt_totalRequired.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public batchItem getItem(int position){
        return mData.get(position);
    }

    public static class batchItem {
        String batchNum, quantity;
        int preset_ipt_qty;

        public batchItem(String batchNum, String quantity, int preset_ipt_qty) {
            this.batchNum = batchNum;
            this.quantity = quantity;
            this.preset_ipt_qty = preset_ipt_qty;
        }

        public String getBatchNum() {
            return batchNum;
        }

        public String getStringQuantity() {
            return quantity;
        }

        public int getNumericQuantity(){
            return Integer.parseInt(quantity);
        }

        public int getPreset_ipt_qty() {
            return preset_ipt_qty;
        }

        public void setPreset_ipt_qty(int preset_ipt_qty) {
            this.preset_ipt_qty = preset_ipt_qty;
        }

        @Override
        public String toString() {
            return "batchItem{" +
                    "batchNum='" + batchNum + '\'' +
                    ", quantity='" + quantity + '\'' +
                    ", preset_ipt_qty=" + preset_ipt_qty +
                    '}';
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView batchNum, batchQuantity;
        EditText batchQtyIpt;
        ImageButton buttonDelete;
        batchRecyclerViewAdapter adapter;

        public MyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            batchNum = itemView.findViewById(R.id.txt_BatchNum);
            batchQuantity = itemView.findViewById(R.id.txt_quantityValue);
            batchQtyIpt = itemView.findViewById(R.id.editText_quantity);
            buttonDelete = itemView.findViewById(R.id.deleteButton);

        }

        private MyViewHolder linkAdapter (batchRecyclerViewAdapter adapter){
            this.adapter = adapter;
            return this;
        }

    }
}
