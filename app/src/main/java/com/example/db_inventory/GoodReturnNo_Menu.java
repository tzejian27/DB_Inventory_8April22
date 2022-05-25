package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GoodReturnNo_Menu extends AppCompatActivity implements View.OnClickListener{

    TextView grn_no;
    Button btn_inventory, btn_inventorylist, btn_dataclear, btn_back;
    DatabaseReference InventoryGoodReturnsNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_return_no_menu);

        //GRN NO DECLARATION
        grn_no = findViewById(R.id.textView_GRN_NO);

        //BUTTON DECLARATION
        btn_inventory = findViewById(R.id.btn_grn_inventory);
        btn_inventorylist = findViewById(R.id.btn_grn_inventory_list);
        btn_dataclear = findViewById(R.id.btn_grn_data_clear);
        btn_back = findViewById(R.id.btn_grn_menu_back);

        //SET ONCLICK
        btn_inventory.setOnClickListener(this);
        btn_inventorylist.setOnClickListener(this);
        btn_dataclear.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        //GET INTENT
        Intent intent = getIntent();
        final String grnno = intent.getStringExtra("goodReturnNo");

        //SET TITLE GRN NO
        grn_no.setText(grnno);

        //CONNECT WITH FIREBASE(USED FOR DATA CLEAR)
        InventoryGoodReturnsNo= FirebaseDatabase.getInstance().getReference().child("InventoryGoodReturnsNo");

    }

    //BUTTON ONCLICK
    @Override
    public void onClick(View view) {
        //GET INTENT
        Intent intent = getIntent();
        final String grnno = intent.getStringExtra("goodReturnNo");

        switch (view.getId()) {

            //INTENT TO GRN INVENTORY
            case R.id.btn_grn_inventory:
                Intent intent2CreateGRN = new Intent(getApplicationContext(), GRNInventory.class);
                intent2CreateGRN.putExtra("goodReturnNo", grnno);
                startActivity(intent2CreateGRN);
                break;

            //INTENT TO GRN INVENTORY LIST
            case R.id.btn_grn_inventory_list:
                Intent intent2GRN_list = new Intent(getApplicationContext(), GRNInventoryList.class);
                intent2GRN_list.putExtra("goodReturnNo", grnno);
                startActivity(intent2GRN_list);
                break;

            //INTENT TO GRN DATA CLEAR
            case R.id.btn_grn_data_clear:
                final AlertDialog.Builder warningDialog = new AlertDialog.Builder(GoodReturnNo_Menu.this);
                warningDialog.setCancelable(false);
                warningDialog.setTitle("Warning !");
                warningDialog.setMessage("Confirm to Data Clear this good returns no ? ");


                warningDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InventoryGoodReturnsNo.child(grnno).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    Toast.makeText(GoodReturnNo_Menu.this, "Data Clear Successfully !", Toast.LENGTH_SHORT).show();
                                }
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
                break;

            //INTENT TO GRN BACK PAGE
            case R.id.btn_grn_menu_back:
                Intent intent2GRN_back = new Intent(getApplicationContext(), GRN_Home.class);
                startActivity(intent2GRN_back);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent2GRN_back = new Intent(getApplicationContext(), GRN_Home.class);
        startActivity(intent2GRN_back);
    }

}