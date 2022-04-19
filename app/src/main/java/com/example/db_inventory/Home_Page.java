package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home_Page extends AppCompatActivity implements View.OnClickListener{

    Button btn_home_home, btn_stock_adjustment;
    Button btn_sales_order, btn_maintain_users;
    Button btn_stockIn, btn_stockOut;
    CardView cardView;

    DatabaseReference arightRef;
    String Switch1;
    String Switch2;
    String Switch3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btn_home_home=findViewById(R.id.btn_House_intent);
        btn_stock_adjustment=findViewById(R.id.btn_stock_adjustment);
        btn_sales_order=findViewById(R.id.btn_sales_order);
        btn_maintain_users=findViewById(R.id.btn_maintain_user);
        cardView=findViewById(R.id.cv_maintain_user);

        btn_stockIn=findViewById(R.id.btn_stock_in);
        btn_stockOut=findViewById(R.id.btn_stock_out);

        btn_stockIn.setOnClickListener(this);
        btn_stockOut.setOnClickListener(this);

        btn_home_home.setOnClickListener(this);
        btn_stock_adjustment.setOnClickListener(this);
        btn_sales_order.setOnClickListener(this);

        String users=getIntent().getStringExtra("Users");
        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");


        btn_maintain_users.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        String users=getIntent().getStringExtra("Users");
        switch (view.getId()){

            case R.id.btn_House_intent:
                Intent intent2house = new Intent(Home_Page.this, MainActivity.class);
                intent2house.putExtra("Users", users);
                startActivity(intent2house);
                break;
            case R.id.btn_stock_adjustment:
                //stock adjustment help calculate the quantity of stock on hand compare with book quantity
                Toast.makeText(getApplicationContext(),"Stock adjustment still under construction",Toast.LENGTH_SHORT).show();
                //Intent intent2stock_adjust = new Intent(Home_Page.this, Stock_Adjustment_Home.class);
                //startActivity(intent2stock_adjust);
                break;
            case R.id.btn_sales_order:
                Toast.makeText(getApplicationContext(),"Sales Order still under construction",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_maintain_user:
                //maintain user are only access by the admin
                if(users != null && users.equals("Admin")){
                    Intent intent2MaintainUser = new Intent(Home_Page.this, Maintain_User.class);
                    intent2MaintainUser.putExtra("Users", users);
                    startActivity(intent2MaintainUser);
                }else{
                    //when there is not admin role received then show error message where not allowed user to enter
                    Toast.makeText(Home_Page.this, "You are not authorized to execute, Please Login as admin", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_stock_in:
                //allowed business to aware their storage
                //it record the quantity of stock coming in

                if (users != null && users.equals("Admin")) {
                    Intent intent2HouseList = new Intent(Home_Page.this, House_List_Stock_In.class);
                    intent2HouseList.putExtra("Users", users);
                    startActivity(intent2HouseList);
                } else if (users.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch2 = snapshot.child("SW_StockIn").getValue().toString().trim();
                            if (Switch2.equals("On")) {
                                Intent intent2HouseList = new Intent(Home_Page.this, House_List_Stock_In.class);
                                intent2HouseList.putExtra("Users", users);
                                startActivity(intent2HouseList);
                            } else if (Switch2.equals("Off")) {
                                Toast.makeText(Home_Page.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(Home_Page.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_stock_out:
                if (users != null && users.equals("Admin")) {
                    Intent intent2HouseList2 = new Intent(Home_Page.this, House_List_Stock_Out.class);
                    intent2HouseList2.putExtra("Users", users);
                    startActivity(intent2HouseList2);
                } else if (users.equals("User")) {
                    arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Switch1 = snapshot.child("SW_StockOut").getValue().toString().trim();
                            if (Switch1.equals("On")) {
                                Intent intent2HouseList2 = new Intent(Home_Page.this, House_List_Stock_Out.class);
                                intent2HouseList2.putExtra("Users", users);
                                startActivity(intent2HouseList2);
                            } else if (Switch1.equals("Off")) {
                                Toast.makeText(Home_Page.this, "Permission denied", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(Home_Page.this, "Something go wrong, pls sign in again", Toast.LENGTH_LONG).show();
                }

                //record the quantity of stock going out
                //Toast.makeText(getApplicationContext(),"Stock Out still under construction",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2login = new Intent(Home_Page.this, Login_Menu.class);
        startActivity(intent2login);
    }
}