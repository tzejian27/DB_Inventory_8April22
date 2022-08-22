package com.example.db_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Access_Right extends AppCompatActivity {
    Switch sw_new_house;
    Switch sw_edit_spec;
    Switch sw_data_clear;
    Switch sw_stock_in;
    Switch sw_stock_out;
    Switch sw_modify_delete;
    Switch sw_setting;
    Switch sw_house_list;
    DatabaseReference arightRef;

    String Switch1;
    String Switch2;
    String Switch3;
    String Switch4;
    String Switch5;
    String Switch6;
    String Switch7;
    String Switch8;

    //USER ACCESS RIGHT FOR E-STOCK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_right);
        setTitle("User Access Right");

        sw_new_house = findViewById(R.id.switch_new_house);
        sw_edit_spec = findViewById(R.id.switch_edit_spec);
        sw_data_clear = findViewById(R.id.switch_data_clear);
        sw_stock_in = findViewById(R.id.switch_stock_in);
        sw_stock_out = findViewById(R.id.switch_SO);
        sw_modify_delete = findViewById(R.id.switch_modify_delete);
        sw_setting = findViewById(R.id.switch_setting);
        sw_house_list = findViewById(R.id.switch_house_list);

        arightRef = FirebaseDatabase.getInstance().getReference("Access_Right");
        arightRef.keepSynced(true);


        //SET THE NEW ADD SWITCH STATUS TO DATABASE
        sw_new_house.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_new_house.isChecked()) {
                    Switch1 = "On";
                    arightRef.child("SW_NewHouse").setValue(Switch1).toString().trim();

                } else {
                    Switch1 = "Off";
                    arightRef.child("SW_NewHouse").setValue(Switch1).toString().trim();
                }
            }
        });

        sw_edit_spec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_edit_spec.isChecked()) {
                    Switch2 = "On";
                    arightRef.child("SW_EditSpec").setValue(Switch2).toString().trim();
                } else {
                    Switch2 = "Off";
                    arightRef.child("SW_EditSpec").setValue(Switch2).toString().trim();
                }
            }
        });

        sw_data_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_data_clear.isChecked()) {
                    Switch3 = "On";
                    arightRef.child("SW_DataClear").setValue(Switch3).toString().trim();
                } else {
                    Switch3 = "Off";
                    arightRef.child("SW_DataClear").setValue(Switch3).toString().trim();
                }
            }
        });

        sw_stock_in.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_stock_in.isChecked()) {
                    Switch4 = "On";
                    arightRef.child("SW_StockIn").setValue(Switch4).toString().trim();
                } else {
                    Switch4 = "Off";
                    arightRef.child("SW_StockIn").setValue(Switch4).toString().trim();
                }
            }
        });

        sw_stock_out.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_stock_out.isChecked()) {
                    Switch5 = "On";
                    arightRef.child("SW_StockOut").setValue(Switch5).toString().trim();
                } else {
                    Switch5 = "Off";
                    arightRef.child("SW_StockOut").setValue(Switch5).toString().trim();
                }
            }
        });

        sw_modify_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_modify_delete.isChecked()) {
                    Switch6 = "On";
                    arightRef.child("SW_ModifyDelete").setValue(Switch6).toString().trim();
                } else {
                    Switch6 = "Off";
                    arightRef.child("SW_ModifyDelete").setValue(Switch6).toString().trim();
                }
            }
        });

        sw_setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_setting.isChecked()) {
                    Switch7 = "On";
                    arightRef.child("SW_Setting").setValue(Switch7).toString().trim();
                } else {
                    Switch7 = "Off";
                    arightRef.child("SW_Setting").setValue(Switch7).toString().trim();
                }
            }
        });

        sw_house_list.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_house_list.isChecked()) {
                    Switch8 = "On";
                    arightRef.child("SW_HouseList").setValue(Switch8).toString().trim();
                } else {
                    Switch8 = "Off";
                    arightRef.child("SW_HouseList").setValue(Switch8).toString().trim();
                }
            }
        });

        String users = getIntent().getStringExtra("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();
        arightRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //CHECKING EXISTING OF SWITCH TABLE IN FIREBASE
                //IF NOT EXIST IT WILL AUTO ADD THE TABLE TO THE FIREBASE
                if (snapshot.exists()) {
                    //GET THE SWITCH DATA IN THE DATABASE
                    //THEN SHOW THE SWITCH STATUS BY CHECKED OR UNCHECKED
                    String sw_nh = snapshot.child("SW_NewHouse").getValue().toString().trim();
                    String sw_eSpec = snapshot.child("SW_EditSpec").getValue().toString().trim();
                    String sw_dc = snapshot.child("SW_DataClear").getValue().toString().trim();
                    String sw_SI = snapshot.child("SW_StockIn").getValue().toString().trim();
                    String sw_SO = snapshot.child("SW_StockOut").getValue().toString().trim();
                    String sw_MD = snapshot.child("SW_ModifyDelete").getValue().toString().trim();
                    String sw_set = snapshot.child("SW_Setting").getValue().toString().trim();
                    String sw_HL = snapshot.child("SW_HouseList").getValue().toString().trim();

                    if (sw_nh.equals("Off")) {
                        sw_new_house.setChecked(false);
                    } else if (sw_nh.equals("On")) {
                        sw_new_house.setChecked(true);
                    }

                    if (sw_eSpec.equals("Off")) {
                        sw_edit_spec.setChecked(false);
                    } else if (sw_eSpec.equals("On")) {
                        sw_edit_spec.setChecked(true);
                    }

                    if (sw_dc.equals("Off")) {
                        sw_data_clear.setChecked(false);
                    } else if (sw_dc.equals("On")) {
                        sw_data_clear.setChecked(true);
                    }

                    if (sw_SI.equals("Off")) {
                        sw_stock_in.setChecked(false);
                    } else if (sw_SI.equals("On")) {
                        sw_stock_in.setChecked(true);
                    }

                    if (sw_SO.equals("Off")) {
                        sw_stock_out.setChecked(false);
                    } else if (sw_SO.equals("On")) {
                        sw_stock_out.setChecked(true);
                    }

                    if (sw_MD.equals("Off")) {
                        sw_modify_delete.setChecked(false);
                    } else if (sw_MD.equals("On")) {
                        sw_modify_delete.setChecked(true);
                    }

                    if (sw_set.equals("Off")) {
                        sw_setting.setChecked(false);
                    } else if (sw_set.equals("On")) {
                        sw_setting.setChecked(true);
                    }

                    if (sw_HL.equals("Off")) {
                        sw_house_list.setChecked(false);
                    } else if (sw_HL.equals("On")) {
                        sw_house_list.setChecked(true);
                    }

                } else {
                    //add switch data when not exist
                    arightRef.child("SW_NewHouse").setValue("On");
                    arightRef.child("SW_EditSpec").setValue("On");
                    arightRef.child("SW_DataClear").setValue("On");
                    arightRef.child("SW_StockIn").setValue("On");
                    arightRef.child("SW_StockOut").setValue("On");
                    arightRef.child("SW_ModifyDelete").setValue("On");
                    arightRef.child("SW_Setting").setValue("On");
                    arightRef.child("SW_HouseList").setValue("On");

                    sw_new_house.setChecked(true);
                    sw_edit_spec.setChecked(true);
                    sw_data_clear.setChecked(true);
                    sw_stock_in.setChecked(true);
                    sw_stock_out.setChecked(true);
                    sw_modify_delete.setChecked(true);
                    sw_setting.setChecked(true);
                    sw_house_list.setChecked(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent2back = new Intent(getApplicationContext(), Maintain_User.class);
        intent2back.putExtra("Users", users);
        startActivity(intent2back);
        finish();
    }
}