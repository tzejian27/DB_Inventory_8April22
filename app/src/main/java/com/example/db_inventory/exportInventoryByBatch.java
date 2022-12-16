package com.example.db_inventory;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class exportInventoryByBatch {
    private String house, currentDateandTime2;
    private static File housefile, filepath;
    private ArrayList<batchObject> allItem = new ArrayList<>();
    private Context context;

    public exportInventoryByBatch(Context context, String house) {
        this.house = house;
        this.context = context;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        currentDateandTime2 = sdf.format(new Date());
        housefile = new File("/storage/emulated/0/Report/" + "eStock" + currentDateandTime2);
        if (!housefile.exists()) {
            housefile.mkdirs();
        }
        setUpList(); // Set up the list and call export method
    }

    public void exportBatchInventory(){

        XSSFWorkbook workbook = buildBodyOfContent();

        filepath = new File(housefile, "Inventory as at_" + currentDateandTime2 + ".xlsx");
        FileOutputStream fileOutputStream = null;

        boolean isSuccess;

        try {
            fileOutputStream = new FileOutputStream(filepath);
            workbook.write(fileOutputStream);
            Log.e("", "Writing file" + filepath);
            isSuccess = true;
        } catch (IOException e) {
            Log.e("", "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e("", "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                    Toast.makeText(context, "Exported successfully !", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private XSSFWorkbook buildBodyOfContent() {
        int rowNumber=0;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Inventory with batch");

        sheet.setColumnWidth(0, 100*40);
        sheet.setColumnWidth(1, 100*30);
        sheet.setColumnWidth(2, 100*50);
        sheet.setColumnWidth(4,100*60);
        sheet.setColumnWidth(5,100*30);

        XSSFRow row = sheet.createRow(rowNumber++);
        row.createCell(0).setCellValue("House: ");
        row.createCell(1).setCellValue(house);

        // Set the header of the table (First row of the table)
        String [] title = {"Barcode", "ItemCode", "Batch No.", "Qty", "DateTime", "Users" };
        row = sheet.createRow(rowNumber++);
        for (int i = 0; i < title.length; i++) {
            row.createCell(i).setCellValue(title[i]);
        }

        // Fill in the data
        for (int i = 0; i < allItem.size(); i++) {
            row = sheet.createRow(rowNumber++);
            batchObject obj = allItem.get(i);
            for (int j = 0; j < title.length; j++) {
                String [] data = obj.getData();
                row.createCell(j).setCellValue(data[j]);
            }
        }
        

        return workbook;
    }

    private void setUpList() {
        DatabaseReference houseRef = FirebaseDatabase.getInstance().getReference("House").child(house);
        houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dss:
                     snapshot.getChildren()) {
                    if(dss.getValue().getClass().equals(String.class)){
                        continue;
                    }else{
                        String barcode, itemCode;
                        barcode = dss.child("Barcode").getValue().toString();
                        itemCode = dss.child("ItemCode").getValue().toString();

                        DatabaseReference batchRef = FirebaseDatabase.getInstance().getReference("Batch").child(house);
                        batchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dss1:
                                     snapshot.getChildren()) {
                                    if(dss1.child("Barcode").getValue().toString().equals(barcode)){
                                        String BatchNo, Qty, Datetime, User;
                                        BatchNo = dss1.getKey();
                                        Qty = dss1.child("Quantity").getValue().toString();
                                        Datetime = dss1.child("DateTime").getValue().toString();
                                        if(dss1.child("User").exists()){
                                            User = dss1.child("User").getValue().toString();
                                        }
                                        else {
                                            User = "-";
                                        }

                                        allItem.add(new batchObject(barcode,itemCode,BatchNo,Qty, Datetime, User));
                                    }

                                }
                                exportBatchInventory();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    class batchObject{
        String barcode, itemCode, BatchNo, Qty, Datetime, User;
        int numericQty;

    public batchObject(String barcode, String itemCode, String batchNo, String qty, String datetime, String user) {
        this.barcode = barcode;
        this.itemCode = itemCode;
        BatchNo = batchNo;
        Qty = qty;
        Datetime = datetime;
        User = user;
        numericQty = Integer.parseInt(Qty);

    }
    private String[] getData(){
        return new String [] {barcode, itemCode, BatchNo, Qty, Datetime, User};
    }
}
}

