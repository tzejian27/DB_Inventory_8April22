package com.example.db_inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockMovement extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    TextView tvdatefrom, tvdateto;
    TextView tvhousename;
    DatabaseReference stockMovRef;
    DatabaseReference stockMovement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_movement);

        Button datefrom = findViewById(R.id.btnDateFrom);
        Button dateto = findViewById(R.id.btnDateTo);
        Button apply = findViewById(R.id.btn_apply);

        Intent intent1 = getIntent();
        String name = intent1.getStringExtra("name");

        setTitle("eStock_Stock Movement_" + name);

        tvdatefrom = findViewById(R.id.tvDateFrom);
        tvdateto = findViewById(R.id.tvDateTo);
        tvhousename = findViewById(R.id.tvHouseName);
        stockMovRef = FirebaseDatabase.getInstance().getReference("StockMovRef");
        stockMovement = FirebaseDatabase.getInstance().getReference("StockMovement").child(name);

        tvhousename.setText("House Name: " + name);


        datefrom.setOnClickListener(new View.OnClickListener() {
            String day1;
            String month1;

            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(StockMovement.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //tvdatefrom.setText(day + "/" + (month + 1) + "/" + year);
                        if (day < 10) {
                            day1 = "0" + String.valueOf(day);
                        } else {
                            day1 = String.valueOf(day);
                        }

                        if (month < 10) {
                            month1 = "0" + String.valueOf(month + 1);
                        } else {
                            month1 = String.valueOf(month + 1);
                        }
                        datefrom.setText(day1 + "/" + month1 + "/" + year);
                        tvdatefrom.setText(year + "" + month1 + "" + day1 + "000000");
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        dateto.setOnClickListener(new View.OnClickListener() {
            String day1;
            String month1;

            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(StockMovement.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //tvdateto.setText(day + "" + (month + 1) + "" + year);
                        if (day < 10) {
                            day1 = "0" + String.valueOf(day);
                        } else {
                            day1 = String.valueOf(day);
                        }

                        if (month < 10) {
                            month1 = "0" + String.valueOf(month + 1);
                        } else {
                            month1 = String.valueOf(month + 1);
                        }

                        dateto.setText(day1 + "/" + month1 + "/" + year);

                        tvdateto.setText(year + "" + month1 + "" + day1 + "235959");
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Tdatefrom = tvdatefrom.getText().toString();
                String Tdateto = tvdateto.getText().toString();

                Long Idatefrom = Long.parseLong(Tdatefrom);
                Long Idateto = Long.parseLong(Tdateto);

                if(Idateto<Idatefrom){
                    dateto.setError("Date To are earlier than Date From");
                    Toast.makeText(getApplicationContext(), "Date To are earlier than Date From", Toast.LENGTH_SHORT).show();
                    return;
                }


                stockMovement.orderByChild("QtyInOut_Date2").startAt(Tdatefrom).endAt(Tdateto).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Map dataMap = new HashMap();
                            //get date from database between date
                            if (snapshot1.child("Barcode").exists()) {

                                String ST_Barcode = snapshot1.child("Barcode").getValue().toString();
                                dataMap.put("Barcode", ST_Barcode);
                                String ST_HouseName = snapshot1.child("HouseName").getValue().toString();
                                String ST_Name = snapshot1.child("Name").getValue().toString();
                                String ST_Qty = snapshot1.child("Qty").getValue().toString();
                                String ST_QtyIn = snapshot1.child("QtyIn").getValue().toString();
                                String ST_QtyOut = snapshot1.child("QtyOut").getValue().toString();
                                String ST_QtyInOut_Date = snapshot1.child("QtyInOut_Date").getValue().toString();
                                String ST_QtyInOut_Date2 = snapshot1.child("QtyInOut_Date2").getValue().toString();
                                String ST_TotalQty = snapshot1.child("TotalQty").getValue().toString();
                                String ST_ParentName = snapshot1.child("ParentName").getValue().toString();


                                if (snapshot1.child("StockAdj").exists()) {
                                    String ST_StockAdj = snapshot1.child("StockAdj").getValue().toString();
                                    dataMap.put("StockAdj", ST_StockAdj);
                                } else {
                                    String ST_StockAdj = "-";
                                    dataMap.put("StockAdj", ST_StockAdj);
                                }

                                //save stock movement record
                                dataMap.put("HouseName", ST_HouseName);
                                dataMap.put("ParentName", ST_ParentName);
                                dataMap.put("Name", ST_Name);
                                dataMap.put("Qty", ST_Qty);
                                dataMap.put("QtyIn", ST_QtyIn);
                                dataMap.put("QtyOut", ST_QtyOut);
                                dataMap.put("QtyInOut_Date", ST_QtyInOut_Date);
                                dataMap.put("QtyInOut_Date2", ST_QtyInOut_Date2);
                                dataMap.put("TotalQty", ST_TotalQty);


                                String parentname = "StockMovement_" + ST_HouseName + "_" + Tdatefrom + "_to_" + Tdateto;

                                Map dataMap2 = new HashMap();
                                dataMap2.put(ST_HouseName + "/" + ST_ParentName + "/", dataMap);

                                stockMovRef.child(parentname).updateChildren(dataMap2);

                                stockMovRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.child("housename").exists()) {
                                            Map map = new HashMap();
                                            map.put("housename", name);
                                            stockMovRef.child(parentname).child(name).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    Toast.makeText(getApplicationContext(), "Add Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Barcode Not Exist ", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}