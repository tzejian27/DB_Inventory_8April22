package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class Inventory_List_All extends AppCompatActivity {

    DatabaseReference databaseReference, switchRef;
    RecyclerView recyclerView;
    ImageView iv_back;
    Button export;
    Long totaltype;
    String switch1;
    public static TextView totalrecord;
    Inventory_List_All context = this;
    DatabaseReference arightRef;
    String Switch1;
    private static ArrayList<Inventory_class> list;
    private static ArrayList <houseArrayList> houselist = new ArrayList();
    InventoryList_Adapter adapter;
    private SearchView searchView;
    ScanReader scanReader;
    private String barcodeStr;

    private final BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode));
            if (barcode != null) {
                barcodeStr = new String(barcode);
                searchView.setQuery(barcodeStr,true);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list_all);
        Inventory_List_All context = this;
        setTitle("eStock_Inventory List");

        //DECLARE AND LINK
        recyclerView = findViewById(R.id.recyclerView_Inventory_List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        iv_back = findViewById(R.id.imageView_IL_back);
        totalrecord = findViewById(R.id.record_IL);
        export = findViewById(R.id.exportButton);

        Intent intent = getIntent();
        String users = getIntent().getStringExtra("Users"); //


        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new exportInventory(list,houselist,users);
            }
        });

        //LINK ACCESS RIGHT FIREBASE
        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");

        //BACK IMAGE OF INVENTORY LIST
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent page = new Intent(Inventory_List_All.this, Home_Page.class);
                page.putExtra("Users", users);
                startActivity(page);
                finish();
            }
        });

        //INTENT TO SEARCH
//        iv_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Inventory_List_All.this, SearchAll.class);
//                intent.putExtra("Users", users);
//                startActivity(intent);
//                finish();
//            }
//        });

        //LINK TO ITEM LIST OF CURRENT HOUSE BY USING HOUSE KEY
        databaseReference = FirebaseDatabase.getInstance().getReference("House");
        databaseReference.keepSynced(true);
        switchRef = FirebaseDatabase.getInstance().getReference("Switch");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                totaltype = Long.parseLong((String)dataSnapshot.child("TotalType").getValue());
                 list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    houseArrayList temp_house = new houseArrayList(ds.getKey());

                    for(DataSnapshot dss : ds.getChildren()) {
                        if (dss.exists() && !dss.getValue().getClass().equals(String.class)) {
                            final Inventory_class item = dss.getValue(Inventory_class.class);
                            if (item != null) {
                                temp_house.add(item);
                                String tempBarcode = item.getBarcode();
                                final Inventory_class[] temp = new Inventory_class[1];
                                if(list.stream().anyMatch(b -> {
                                    temp[0] = b;
                                    return b.getBarcode().equals(tempBarcode);
                                })){
                                    temp[0].setQuantity(String. valueOf(
                                                    Integer.parseInt(temp[0].getQuantity())+Integer.parseInt(item.getQuantity())                                            )
                                            );
                                    continue;
                                }

                                list.add(item);
                            }
                        }
                    }
                    houselist.add(temp_house);
                }
                list.sort(new Comparator<Inventory_class>() {
                    @Override
                    public int compare(Inventory_class inventory_class, Inventory_class t1) {
                        return inventory_class.getBarcode().compareTo(t1.getBarcode());
                    }
                });
                final InventoryList_Adapter inventoryAdapter = new InventoryList_Adapter(context,list);
                adapter = inventoryAdapter;
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        switchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switch1 = snapshot.child("NoNeed").getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();
        this.searchView = searchView;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();
        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Inventory_class> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Inventory_class item : list) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getBarcode().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    public void onStart() {
        super.onStart();
        String users = getIntent().getStringExtra("Users");
        // final String name = getActivity().getIntent().getExtras().get("visit_hairstylist").toString();
        Intent intent = getIntent();

//        FirebaseRecyclerOptions<Inventory_class> inventoryAdapter = new FirebaseRecyclerOptions.Builder<Inventory_class>()
//                .setQuery(databaseReference.orderByChild("HouseKey").equalTo(key), Inventory_class.class)
//                .setLifecycleOwner(this)
//                .build();


//        FirebaseRecyclerAdapter<Inventory_class, AllUsersViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Inventory_class, AllUsersViewHolder>(inventoryAdapter) {
//            @Override
//            protected void onBindViewHolder(@NonNull AllUsersViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull Inventory_class model) {
//                viewHolder.setBarcode(model.getBarcode());
//                viewHolder.setQuantity(model.getQuantity());
//                viewHolder.setItemName(model.getItemName());
//                viewHolder.setDate_and_Time(model.getDate_and_Time());
//                viewHolder.setPrice(model.getPrice());
//                viewHolder.setCost(model.getCost());
//                viewHolder.setItemCode(model.getItemCode());
//                //totalrecord.setText(model.getBarcode().length());
//                    totalrecord.setText(String.valueOf(getItemCount()));
//
//                /*for(int i=0;i<model.getBarcode().length(); i++){
//                    totalrecord.setText(String.valueOf(i-1));
//                }*/
//
//                arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Switch1 = snapshot.child("SW_EditSpec").getValue().toString().trim();
//                        if (Switch1.equals("On")) {
//                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    final String key2 = getRef(position).getKey();
//                                    CharSequence[] option = new CharSequence[]{
//                                            "Spec", "Delete", "Modify"
//                                    };
//
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(Inventory_List_All.this);
//                                    builder.setTitle("Select Option");
//
//                                    builder.setItems(option, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int position) {
//                                            //INTENT TO EDIT AND VIEW ITEM SPEC PAGE
//                                            if (position == 0) {
//                                                if (switch1.equals("Need")) {
//                                                    Intent intent = new Intent(Inventory_List_All.this, Item_Spec.class);
//                                                    intent.putExtra("Key", key);
//                                                    intent.putExtra("Key2", key2);
//                                                    intent.putExtra("name", name);
//                                                    intent.putExtra("ItemName", model.getItemName());
//                                                    intent.putExtra("Barcode", model.getBarcode());
//                                                    intent.putExtra("Users", users);
//                                                    startActivity(intent);
//                                                } else {
//                                                    Toast.makeText(getApplicationContext(), "Edit Spec is Not Allowed", Toast.LENGTH_SHORT).show();
//                                                }
//
//
//                                            }
//                                            //ITEM DELETE CONFIRMATION
//                                            if (position == 1) {
//                                                Intent intent = new Intent(Inventory_List_All.this, Inventory_Delete_Confirm.class);
//                                                intent.putExtra("Key", key);
//                                                intent.putExtra("Key2", key2);
//                                                intent.putExtra("name", name);
//                                                intent.putExtra("ItemName", model.getItemName());
//                                                intent.putExtra("Users", users);
//                                                startActivity(intent);
//                                            }
//                                            if (position == 2) {
//
//                                                //MODIFY INVENTORY
//                                                Intent intent = new Intent(Inventory_List_All.this, Inventory_step5.class);
//                                                intent.putExtra("name", name);
//                                                intent.putExtra("barcode", model.getBarcode());
//                                                intent.putExtra("Key", key);
//                                                intent.putExtra("Key2", key2);
//                                                intent.putExtra("ItemName", model.getItemName());
//                                                intent.putExtra("Users", users);
//                                                startActivity(intent);
//                                            }
//
//                                        }
//                                    });
//                                    builder.show();
//                                }
//                            });
//
//                        } else if (Switch1.equals("Off")) {
//                            Toast.makeText(Inventory_List_All.this, "Edit Permission denied", Toast.LENGTH_LONG).show();
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//
//            }
//
//            @NonNull
//            @Override
//            public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_recycle_list_item, parent, false);
//                return new AllUsersViewHolder(view);
//            }
//
//        };
//        recyclerView.setAdapter(firebaseRecyclerAdapter2);
//        firebaseRecyclerAdapter2.startListening();
        //totalrecord.setText(String.valueOf(recyclerView.getAdapter().getItemCount()));
    }

    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        super.onBackPressed();
        Intent page = new Intent(Inventory_List_All.this, House_Menu.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        page.putExtra("Users", users);
        startActivity(page);
        finish();

    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setBarcode(String barcode) {
            TextView Barcode = mView.findViewById(R.id.textView_InventoryList_Barcode);
            Barcode.setText(barcode);

        }

        public void setQuantity(String quantity) {
            TextView Qty = mView.findViewById(R.id.textView_InventoryList_Qty);
            Qty.setText(quantity);
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

        public void setCost(String cost) {
            TextView Cost = mView.findViewById(R.id.textView_InventoryList_Cost);
            Cost.setText(cost);
        }

        public void setItemCode(String itemCode){
            TextView ItemCode = mView.findViewById(R.id.textView_InventoryList_itemCode);
            ItemCode.setText(itemCode);
        }


    }

    public static class houseArrayList extends ArrayList<Inventory_class> {
        String HouseName;

        public houseArrayList(String houseName) {
            HouseName = houseName;
        }

        public String getHouseName() {
            return HouseName;
        }

        public void setHouseName(String houseName) {
            HouseName = houseName;
        }
    }
}