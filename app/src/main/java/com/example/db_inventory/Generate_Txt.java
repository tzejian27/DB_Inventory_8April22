package com.example.db_inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.DocumentFormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Generate_Txt extends AppCompatActivity {

    private static final String TAG = "";
    public static File filePath, filePath2;
    private static final String EXCEL_SHEET_NAME = "House List 1";
    private static final String EXCEL_SHEET_NAME2 = "New Good 1";
    private static HSSFSheet hssfSheet;
    private static HSSFRow hssfRow;
    private static HSSFCell hssfCell;
    private static HSSFSheet hssfSheet2;
    private static HSSFRow hssfRow2;
    private static HSSFCell hssfCell2;
    //creating a list of objects house
    List<House_list_class> houseListClassList;
    List<NewGoods> newGoodsList;
    Button btn_excel_export, btn_excel_newGoods;
    String currentDateandTime;
    String currentDateandTime2;
    private DatabaseReference houseRef, newGoodRef;
    private File housefile;

    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_txt);
        setTitle("Generate Excel");

        btn_excel_export = findViewById(R.id.button_excel_report);
        btn_excel_newGoods = findViewById(R.id.button_excel_newGoods);

        houseRef = FirebaseDatabase.getInstance().getReference().child("House");

        newGoodRef = FirebaseDatabase.getInstance().getReference().child("New_Goods");

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        currentDateandTime = sdf.format(new Date());

        housefile = new File("/storage/emulated/0/Report/" + "eStock" + currentDateandTime);

        if (!housefile.exists()) {
            housefile.mkdirs();
        }

        houseListClassList = new ArrayList<>();
        newGoodsList = new ArrayList<>();

        //fetch house details;
        fetchHouse();
        fetchNewGoods();

        filePath = new File(housefile, "HouseList.xls");
        filePath2 = new File(housefile, "GoodsList.xls");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        btn_excel_export.setOnClickListener(new View.OnClickListener() {
            final FileOutputStream fileOutputStream = null;

            @Override
            public void onClick(View view) {
                try {
                    exportExcel(houseListClassList);
                    Toast.makeText(Generate_Txt.this, "Generating House List", Toast.LENGTH_SHORT).show();

                } catch (DocumentFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(Generate_Txt.this, "House List Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_excel_newGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    exportNewGoods(newGoodsList);
                    Toast.makeText(Generate_Txt.this, "Generating Good List", Toast.LENGTH_SHORT).show();

                } catch (DocumentFormatException f) {
                    f.printStackTrace();
                    Toast.makeText(Generate_Txt.this, "Good List Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void fetchNewGoods() {
        newGoodRef.orderByChild("Barcode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        NewGoods newGoods = new NewGoods();

                        newGoods.setName(dataSnapshot.child("Name").getValue().toString());
                        //If fatal exception at here, that mean some good miss their barcode (Firebase)
                        newGoods.setBarcode(dataSnapshot.child("Barcode").getValue().toString());
                        newGoods.setCost(dataSnapshot.child("Cost").getValue().toString());
                        newGoods.setPrice(dataSnapshot.child("Price").getValue().toString());


                        Log.d("New_Goods", "Name: " + newGoods.getName());
                        Log.d("New_Goods", "Barcode: " + newGoods.getBarcode());
                        Log.d("New_Goods", "Cost: " + newGoods.getCost());
                        Log.d("New_Goods", "Price: " + newGoods.getPrice());

                        newGoodsList.add(newGoods);
                    } else {
                        Toast.makeText(getApplicationContext(), "Something is missing", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchHouse() {
        houseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //creating an object and setting to display
                    House_list_class houses = new House_list_class();

                    houses.setKey(snapshot.child("Key").getValue().toString());
                    houses.setName(snapshot.child("Name").getValue().toString());
                    houses.setTotalQty(snapshot.child("TotalQty").getValue().toString());
                    houses.setTotalType(snapshot.child("TotalType").getValue().toString());

                    //this just log details fetched from db(you can use Timber for logging
                    Log.d("House", "Key: " + houses.getKey());
                    Log.d("House", "Name: " + houses.getName());
                    Log.d("House", "TotalQty: " + houses.getTotalQty());
                    Log.d("House", "TotalType: " + houses.getTotalType());

                    /* The error before was cause by giving incorrect data type
                    You were adding an object of type House yet the arraylist expects obejct of type DisabledUsers
                     */
                    houseListClassList.add(houses);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void exportNewGoods(List<NewGoods> newGoodsList) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

        //Create House Excel sheet
        hssfSheet2 = hssfWorkbook.createSheet(EXCEL_SHEET_NAME2);
        hssfSheet2.setColumnWidth(0, (15 * 400));
        hssfSheet2.setColumnWidth(1, (15 * 400));
        hssfSheet2.setColumnWidth(2, (15 * 400));
        hssfSheet2.setColumnWidth(3, (15 * 400));

        hssfRow2 = hssfSheet2.createRow(0);

        hssfCell2 = hssfRow2.createCell(0);
        hssfCell2.setCellValue("Name");

        hssfCell2 = hssfRow2.createCell(1);
        hssfCell2.setCellValue("Barcode");

        hssfCell2 = hssfRow2.createCell(2);
        hssfCell2.setCellValue("Cost");

        hssfCell2 = hssfRow2.createCell(3);
        hssfCell2.setCellValue("Price");

        for (int j = 0; j < this.newGoodsList.size(); j++) {
            NewGoods newGoods = this.newGoodsList.get(j);
            HSSFRow rowData = hssfSheet2.createRow(j + 1);

            hssfCell2 = rowData.createCell(0);
            hssfCell2.setCellValue(this.newGoodsList.get(j).getName());

            hssfCell2 = rowData.createCell(1);
            hssfCell2.setCellValue(this.newGoodsList.get(j).getBarcode());

            hssfCell2 = rowData.createCell(2);
            hssfCell2.setCellValue(this.newGoodsList.get(j).getCost());

            hssfCell2 = rowData.createCell(3);
            hssfCell2.setCellValue(this.newGoodsList.get(j).getPrice());

        }

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        currentDateandTime = sdf.format(new Date());

        filePath2 = new File(housefile, "GoodsList_" + currentDateandTime + ".xls");
        FileOutputStream fileOutputStream = null;

        boolean isSuccess;


        try {
            fileOutputStream = new FileOutputStream(filePath2);
            hssfWorkbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + filePath2);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    public void exportExcel(List<House_list_class> houseListClassList) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

        //Create House Excel sheet
        hssfSheet = hssfWorkbook.createSheet(EXCEL_SHEET_NAME);
        hssfSheet.setColumnWidth(0, (15 * 400));
        hssfSheet.setColumnWidth(1, (15 * 400));
        hssfSheet.setColumnWidth(2, (15 * 400));
        hssfSheet.setColumnWidth(3, (15 * 400));

        hssfRow = hssfSheet.createRow(0);

        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("Key");

        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("Name");

        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellValue("Total Quantity");

        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Total Type");


        for (int i = 0; i < this.houseListClassList.size(); i++) {
            House_list_class house = this.houseListClassList.get(i);
            HSSFRow rowData = hssfSheet.createRow(i + 1);

            hssfCell = rowData.createCell(0);
            hssfCell.setCellValue(this.houseListClassList.get(i).getKey());

            hssfCell = rowData.createCell(1);
            hssfCell.setCellValue(this.houseListClassList.get(i).getName());

            hssfCell = rowData.createCell(2);
            hssfCell.setCellValue(this.houseListClassList.get(i).getTotalQty());

            hssfCell = rowData.createCell(3);
            hssfCell.setCellValue(this.houseListClassList.get(i).getTotalType());

        }

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        currentDateandTime = sdf.format(new Date());


        filePath = new File(housefile, "HouseList_" + currentDateandTime + ".xls");
        FileOutputStream fileOutputStream = null;

        boolean isSuccess;


        try {
            fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + filePath);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        super.onBackPressed();
        Intent intent = new Intent(Generate_Txt.this, MainActivity.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }

}