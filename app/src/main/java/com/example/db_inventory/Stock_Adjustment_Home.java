package com.example.db_inventory;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stock_Adjustment_Home extends AppCompatActivity {
    public static final String TAG = "ExcelUtil";
    private static final int REQUEST_CODE_DOC = 1234;
    private static HSSFSheet hssfSheet;
    private static HSSFWorkbook hssfWorkbook;
    private static List<Inventory_class> importedExcelData;
    Button btn_import;

    public static List<Inventory_class> readFromExcelWorkbook(Context context, String fileName) {
        return retrieveExcelFromStorage(context, fileName);
    }

    private static List<Inventory_class> retrieveExcelFromStorage(Context context, String fileName) {
        importedExcelData = new ArrayList<>();

        File file = new File(context.getExternalFilesDir(null), fileName);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            Log.e(TAG, "Reading from Excel" + file);

            hssfWorkbook = new HSSFWorkbook(fileInputStream);

            hssfSheet = hssfWorkbook.getSheetAt(0);

            for (Row row : hssfSheet) {
                int index = 0;
                List<String> rowDataList = new ArrayList<>();
                List<Inventory_class> inventoryClassList = new ArrayList<>();

                if (row.getRowNum() > 0) {
                    Iterator<Cell> hssfCellIterator = row.cellIterator();

                    while (hssfCellIterator.hasNext()) {
                        HSSFCell hssfCell = (HSSFCell) hssfCellIterator.next();

                        rowDataList.add(index, hssfCell.getStringCellValue());
                        index++;
                    }

                    for (int i = 1; i < rowDataList.size(); i++) {
                        inventoryClassList.add(new Inventory_class());
                    }
                    importedExcelData.add(new Inventory_class());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return importedExcelData;
    }

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
        setContentView(R.layout.activity_stock_adjustment_home);

        btn_import = findViewById(R.id.import_Excel);

        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] mimeTypes =
                        {"application/vnd.ms-excel"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                startActivityForResult(Intent.createChooser(intent, "ChooseFile"), REQUEST_CODE_DOC);

                //File file = Environment.getExternalStorageDirectory();
                //File gpxfile = new File(file, FilenameFilter);
                //Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.fromFile(file),"");
                //startActivity(intent);
            }
        });
    }


}