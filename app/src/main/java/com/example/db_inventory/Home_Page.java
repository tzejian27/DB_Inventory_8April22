package com.example.db_inventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home_Page extends AppCompatActivity implements View.OnClickListener {

    Button btn_stock_take, btn_stock_adjustment;
    Button btn_sales_order, btn_maintain_users;
    Button btn_grn;
    Button btn_stockIn, btn_stockOut;
    Button btn_RFID;
    CardView cardView;

    DatabaseReference arightRef;
    String Switch1;
    String Switch2;

    //Home page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setTitle("eStock_Home Page");

        //DECLARE AND LINK THE VARIABLE
        btn_stock_take = findViewById(R.id.btn_stock_take);
        btn_stock_adjustment = findViewById(R.id.btn_stock_adjustment);
        btn_sales_order = findViewById(R.id.btn_sales_order);
        btn_maintain_users = findViewById(R.id.btn_maintain_user);
        cardView = findViewById(R.id.cv_maintain_user);

        btn_stockIn = findViewById(R.id.btn_stock_in);
        btn_stockOut = findViewById(R.id.btn_stock_out);
        btn_grn = findViewById(R.id.btn_GRN);

        //BUTTON OF RFID
        btn_RFID = findViewById(R.id.btn_RFID);

        btn_stockIn.setOnClickListener(this);
        btn_stockOut.setOnClickListener(this);
        btn_stock_take.setOnClickListener(this);
        btn_stock_adjustment.setOnClickListener(this);
        btn_sales_order.setOnClickListener(this);
        btn_maintain_users.setOnClickListener(this);
        btn_grn.setOnClickListener(this);


        //LINKING TO ACCESS RIGHT FIREBASE PARENT
        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");


    }

    @Override
    public void onClick(View view) {

        //GET USER ROLE
        final DBHandler dbHandler = new DBHandler(this);
        Cursor cursor = dbHandler.fetch();
        cursor.moveToLast();
        String role = cursor.getString(2);

        switch (view.getId()) {

            //INTENT TO STOCK TAKE PAGE
            case R.id.btn_stock_take:
                Intent intent2house = new Intent(Home_Page.this, MainActivity.class);
                intent2house.putExtra("Users", role);
                startActivity(intent2house);
                break;

            //INTENT TO STOCK ADJUSTMENT PAGE
            //STOCK ADJUSTMENT HELP CALCULATE THE QUANTITY OF STOCK ON HAND COMPARE WITH BOOK QUANTITY
            //THE DATA AND CALCULATION WILL BE DONE IN SIDE SERVER
            case R.id.btn_stock_adjustment:
                Intent intent2stock_adjust = new Intent(getApplicationContext(), Stock_Adjustment_Home.class);
                startActivity(intent2stock_adjust);
                break;

            //Intent to Sale Order List
            //THE SALE ORDER DATA CAN BE IMPORT BY EXCEL FILE AT SIDE SERVER
            case R.id.btn_sales_order:
                Intent intent2salesorder = new Intent(Home_Page.this, SaleOrderList.class);
                intent2salesorder.putExtra("Users", role);
                startActivity(intent2salesorder);
                break;

            //INTENT TO MAINTAIN USER (ONLY ALLOW ADMIN TO USE IT)
            case R.id.btn_maintain_user:
                //MAINTAIN ARE ONLY ACCESS BY THE ADMIN
                if (role != null && role.equals("Admin")) {
                    Intent intent2Maintain = new Intent(Home_Page.this, Maintain.class);
                    intent2Maintain.putExtra("Users", role);
                    startActivity(intent2Maintain);
                } else {
                    //WHEN THERE IS NOT ADMIN ROLE RECEIVED THEN SHOW ERROR MESSAGE WHERE NOT ALLOWED USER TO ENTER
                    Toast.makeText(Home_Page.this, "You are not authorized to execute, Please Login as admin", Toast.LENGTH_LONG).show();
                }
                break;

            //INTENT TO STOCK IN (CHECK ACCESS RIGHT FOR USER, ADMIN NO NEED)
            case R.id.btn_stock_in:
                //ALLOWED BUSINESS TO AWARE THEIR STORAGE
                //IT RECORD THE QUANTITY OF STOCK COMING IN
                //ADMIN ARE ALWAYS ALLOWED TO MAKE THE STOCK IN
                //WHEN LOGIN USER'S ROLE ARE USER
                //IT CHECK THE ACCESS RIGHT FOR USER TO ENTER STOCK IN
                if (role != null && role.equals("Admin")) {
                    Intent intent2HouseList = new Intent(Home_Page.this, House_List_Stock_In.class);
                    intent2HouseList.putExtra("Users", role);
                    startActivity(intent2HouseList);
                } else if (role.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch2 = snapshot.child("SW_StockIn").getValue().toString().trim();
                            if (Switch2.equals("On")) {
                                Intent intent2HouseList = new Intent(Home_Page.this, House_List_Stock_In.class);
                                intent2HouseList.putExtra("Users", role);
                                startActivity(intent2HouseList);
                            } else if (Switch2.equals("Off")) {
                                Toast.makeText(Home_Page.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(Home_Page.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }
                break;

            //Intent to Stock Out (Check access right for user, admin no need)
            case R.id.btn_stock_out:
                //ALLOWED BUSINESS TO AWARE THEIR STORAGE
                //IT RECORD THE QUANTITY OF STOCK GOING OUT
                //ADMIN ARE ALWAYS ALLOWED TO MAKE THE STOCK OUT
                //WHEN LOGIN USER'S ROLE ARE USER
                //IT CHECK THE ACCESS RIGHT FOR USER TO ENTER STOCK IN
                if (role != null && role.equals("Admin")) {
                    Intent intent2HouseList2 = new Intent(Home_Page.this, House_List_Stock_Out.class);
                    intent2HouseList2.putExtra("Users", role);
                    startActivity(intent2HouseList2);
                } else if (role.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch1 = snapshot.child("SW_StockOut").getValue().toString().trim();
                            if (Switch1.equals("On")) {
                                Intent intent2HouseList2 = new Intent(Home_Page.this, House_List_Stock_Out.class);
                                intent2HouseList2.putExtra("Users", role);
                                startActivity(intent2HouseList2);
                            } else if (Switch1.equals("Off")) {
                                Toast.makeText(Home_Page.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(Home_Page.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }

                //RECORD THE QUANTITY OF STOCK GOING OUT
                break;

                //GOOD RETURN
            case R.id.btn_GRN:
                Intent intent2grn = new Intent(getApplicationContext(), GRN_Home.class);
                finish();
                break;
            /*case R.id.btn_RFID:
                Intent intent2rfid = new Intent(getApplicationContext(), RFID_MainActivity.class);
                startActivity(intent2rfid);
                finish();
                break;*/
        }
    }

    @Override
    public void onBackPressed() {
        //LOGOUT CONFIRMATION
        AlertDialog.Builder builder = new AlertDialog.Builder(Home_Page.this)
                .setTitle("Logout")
                .setCancelable(false)
                .setMessage("Are you sure to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent2login = new Intent(Home_Page.this, Login_Menu.class);
                        startActivity(intent2login);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();


    }
}