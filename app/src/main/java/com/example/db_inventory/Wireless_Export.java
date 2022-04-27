package com.example.db_inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Export Inventory list of a house
public class Wireless_Export extends AppCompatActivity {

    private static final String TAG = "";
    public static File filePath;
    private static final String EXCEL_SHEET_NAME = "House & Inventory 1";
    private static HSSFSheet hssfSheet;
    private static HSSFRow hssfRow;
    private static HSSFCell hssfCell;
    Button Export;
    TextView n1, k1;
    TextView stockTake_no;
    DatabaseReference houseRef;
    DatabaseReference stockTakeRef;
    List<HouseInventory> houseInventoryList;
    String currentDateandTime2;
    private File housefile;
    FirebaseUser user;
    DatabaseReference userRef;


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
        setContentView(R.layout.activity_wireless_export);

        Export = findViewById(R.id.button_export_inventory);
        n1 = findViewById(R.id.name_export2);
        k1 = findViewById(R.id.key1_export2);
        stockTake_no = findViewById(R.id.stock_take_num);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        houseRef = FirebaseDatabase.getInstance().getReference("House").child(key);
        stockTakeRef = FirebaseDatabase.getInstance().getReference("StockTakeRecord");

        user = FirebaseAuth.getInstance().getCurrentUser();

        final DBHandler dbHandler = new DBHandler(this);
        Cursor cursor = dbHandler.fetch();
        cursor.moveToLast();
        final TextView userName = findViewById(R.id.stock_take_username);
        userName.setText(cursor.getString(1));


        houseRef.keepSynced(true);

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        currentDateandTime2 = sdf.format(new Date());

        stockTake_no.setText("StockTake_" + name + "_" + currentDateandTime2);

        housefile = new File("/storage/emulated/0/Report/" + "eStock" + currentDateandTime2);


        if (!housefile.exists()) {
            housefile.mkdirs();
        }

        fetchHouseInventory();

        houseInventoryList = new ArrayList<>();

        filePath = new File(housefile, "HouseInventoryList.xls");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        n1.setText(name);
        k1.setText(key);

        Export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    exportExcel(houseInventoryList);
                    ExportStockTake();
                    Toast.makeText(Wireless_Export.this, "Generating House and Inventory List", Toast.LENGTH_SHORT).show();

                } catch (DocumentFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(Wireless_Export.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //export Stock take to firebase
    private void ExportStockTake(){
        Intent intent = getIntent();
        final String nameFile = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        final DBHandler dbHandler = new DBHandler(this);
        Cursor cursor = dbHandler.fetch();
        cursor.moveToLast();
        final TextView userName = findViewById(R.id.stock_take_username);
        userName.setText(cursor.getString(1));

        String username1 = userName.getText().toString().trim();

        //Save stock take record
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        currentDateandTime2 = sdf.format(new Date());
        //set default status
        String status = "Pending";

        houseRef.orderByChild("HouseKey").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String ST_Barcode = snapshot.child("Barcode").getValue().toString();
                    String ST_Qty= snapshot.child("Quantity").getValue().toString();
                    String ST_ItemName = snapshot.child("ItemName").getValue().toString();
                    String ST_Price= snapshot.child("Price").getValue().toString();
                    String ST_Cost = snapshot.child("Cost").getValue().toString();
                    String ST_HouseKey= snapshot.child("HouseKey").getValue().toString();
                    String ST_Date_and_Time = snapshot.child("Date_and_Time").getValue().toString();

                    stockTake_no.setText("StockTake_" + nameFile + "_" + currentDateandTime2);
                    String recordName = stockTake_no.getText().toString().trim();
                    Map dataMap = new HashMap();
                    dataMap.put("Barcode", ST_Barcode);
                    dataMap.put("Qty", ST_Qty);
                    dataMap.put("ItemName", ST_ItemName);
                    dataMap.put("Price", ST_Price);
                    dataMap.put("Cost", ST_Cost);
                    dataMap.put("HouseKey", ST_HouseKey);
                    dataMap.put("DateAndTime", ST_Date_and_Time);
                    dataMap.put("Status", status);
                    dataMap.put("StorageLocation", nameFile);
                    dataMap.put("Username", username1);

                    stockTakeRef.child(recordName).child(snapshot.child("Barcode").getValue().toString()).updateChildren(dataMap);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void fetchHouseInventory() {
        Intent intent = getIntent();
        final String nameFile = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");
        houseRef.orderByChild("HouseKey").equalTo(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //creating an object and setting to display
                    HouseInventory houses = new HouseInventory();
                    houses.setBarcode(snapshot.child("Barcode").getValue().toString());
                    houses.setQuantity(snapshot.child("Quantity").getValue().toString());
                    houses.setItemName(snapshot.child("ItemName").getValue().toString());
                    houses.setHouseKey(snapshot.child("HouseKey").getValue().toString());
                    houses.setPrice(snapshot.child("Price").getValue().toString());
                    houses.setCost(snapshot.child("Cost").getValue().toString());
                    houses.setDate_and_Time(snapshot.child("Date_and_Time").getValue().toString());
                    houses.setKey2(snapshot.child("Key").getValue().toString());

                    //Save stock take record
                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                    currentDateandTime2 = sdf.format(new Date());

                    Intent intent = getIntent();
                    final String name = intent.getStringExtra("name");
                    String status = "Pending";

                    /*String ST_Barcode = snapshot.child("Barcode").getValue().toString();
                    String ST_Qty= snapshot.child("Quantity").getValue().toString();
                    String ST_ItemName = snapshot.child("ItemName").getValue().toString();
                    String ST_Price= snapshot.child("Price").getValue().toString();
                    String ST_Cost = snapshot.child("Cost").getValue().toString();
                    String ST_HouseKey= snapshot.child("HouseKey").getValue().toString();
                    String ST_Date_and_Time = snapshot.child("Date_and_Time").getValue().toString();*/

                    /*stockTake_no.setText("StockTake_" + name + "_" + currentDateandTime2);
                    String recordName = stockTake_no.getText().toString().trim();
                    Map dataMap = new HashMap();
                    dataMap.put("Barcode", snapshot.child("Barcode").getValue().toString());
                    dataMap.put("Qty", snapshot.child("Quantity").getValue().toString());
                    dataMap.put("ItemName", snapshot.child("ItemName").getValue().toString());
                    dataMap.put("Price", snapshot.child("Price").getValue().toString());
                    dataMap.put("Cost", snapshot.child("Cost").getValue().toString());
                    dataMap.put("HouseKey", snapshot.child("HouseKey").getValue().toString());
                    dataMap.put("DateAndTime", snapshot.child("Date_and_Time").getValue().toString());
                    dataMap.put("Status", status);
                    dataMap.put("StorageLocation", name);

                    stockTakeRef.child(recordName).child(snapshot.child("Barcode").getValue().toString()).updateChildren(dataMap);*/

                    Log.d("House", "HouseKey: " + houses.getHouseKey());

                    /* The error before was cause by giving incorrect data type
                    You were adding an object of type House yet the arraylist expects obejct of type DisabledUsers
                     */
                    houseInventoryList.add(houses);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void exportExcel(List<HouseInventory> houseInventoryList) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

        hssfSheet = hssfWorkbook.createSheet(EXCEL_SHEET_NAME);
        hssfSheet.setColumnWidth(0, (15 * 400));
        hssfSheet.setColumnWidth(1, (15 * 400));
        hssfSheet.setColumnWidth(2, (15 * 400));
        hssfSheet.setColumnWidth(3, (15 * 400));
        hssfSheet.setColumnWidth(4, (15 * 400));
        hssfSheet.setColumnWidth(5, (15 * 400));
        hssfSheet.setColumnWidth(6, (15 * 400));
        hssfSheet.setColumnWidth(7, (15 * 400));

        hssfRow = hssfSheet.createRow(0);

        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("Barcode");

        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("Quantity");

        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellValue("Item Name");

        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("House Key");

        hssfCell = hssfRow.createCell(4);
        hssfCell.setCellValue("Price");

        hssfCell = hssfRow.createCell(5);
        hssfCell.setCellValue("Cost");

        hssfCell = hssfRow.createCell(6);
        hssfCell.setCellValue("Date and Time");

        hssfCell = hssfRow.createCell(7);
        hssfCell.setCellValue("Key");

        for (int i = 0; i < this.houseInventoryList.size(); i++) {
            HouseInventory house = this.houseInventoryList.get(i);
            HSSFRow rowData = hssfSheet.createRow(i + 1);

            hssfCell = rowData.createCell(0);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getBarcode());

            hssfCell = rowData.createCell(1);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getQuantity());

            hssfCell = rowData.createCell(2);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getItemName());

            hssfCell = rowData.createCell(3);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getHouseKey());

            hssfCell = rowData.createCell(4);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getPrice());

            hssfCell = rowData.createCell(5);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getCost());

            hssfCell = rowData.createCell(6);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getDate_and_Time());

            hssfCell = rowData.createCell(7);
            hssfCell.setCellValue(this.houseInventoryList.get(i).getKey2());

        }


        String users = getIntent().getStringExtra("Users");

        Intent intent = getIntent();
        final String nameFile = intent.getStringExtra("name");
        intent.putExtra("Users", users);

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        currentDateandTime2 = sdf.format(new Date());


        filePath = new File(housefile, "StockTake" + nameFile + "_" + currentDateandTime2 + ".xls");
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

    public void onBackPressed() {
        String users = getIntent().getStringExtra("Users");
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String key = intent.getStringExtra("Key");

        super.onBackPressed();
        Intent page = new Intent(Wireless_Export.this, House_Menu.class);
        page.putExtra("name", name);
        page.putExtra("Key", key);
        page.putExtra("Users", users);
        startActivity(page);
        finish();

    }
}