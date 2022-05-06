package com.example.db_inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Calendar;

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
        stockMovement = FirebaseDatabase.getInstance().getReference("StockMovement");

        tvhousename.setText("House Name: " + name);


        datefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(StockMovement.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvdatefrom.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        dateto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(StockMovement.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvdateto.setText(day + "/" + (month + 1) + "/" + year);
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

            }
        });


    }
}