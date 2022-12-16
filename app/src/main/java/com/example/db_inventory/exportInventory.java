package com.example.db_inventory;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class exportInventory {

    ArrayList <Inventory_class> listOfItem = new ArrayList<>();
    ArrayList <Inventory_List_All.houseArrayList> houseList = new ArrayList<>();
    private static final String EXCEL_SHEET_NAME = "Master Inventory List";
    private String username;
    private static File housefile, filepath;
    private String currentDateandTime2;
    private HSSFSheet hssfSheet;
    private HSSFRow hssfRow;
    private HSSFCell hssfCell;
    private int houseFieldStart, houseFieldEnd;
    private Context context;
    public exportInventory(Context context, ArrayList<Inventory_class> listOfItem, ArrayList<Inventory_List_All.houseArrayList> houseList, String username) {
        this.listOfItem = listOfItem;
        this.houseList = houseList;
        this.username = username;
        this.context = context;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        currentDateandTime2 = sdf.format(new Date());
        housefile = new File("/storage/emulated/0/Report/" + "eStock" + currentDateandTime2);
        if (!housefile.exists()) {
            housefile.mkdirs();
        }
        exportExcel();
    }

    public void exportExcel() {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

        hssfSheet = hssfWorkbook.createSheet(EXCEL_SHEET_NAME);

        // Set header value
        int columnIndex =0;
        hssfRow = hssfSheet.createRow(0);

        hssfSheet.setColumnWidth(columnIndex, (15 * 100));
        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("No");

        hssfSheet.setColumnWidth(columnIndex, (15 * 400));
        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Barcode");

        hssfSheet.setColumnWidth(columnIndex, (15 * 400));
        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Item_Code");

        hssfSheet.setColumnWidth(columnIndex, (15 * 400));
        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Item");

        hssfSheet.setColumnWidth(columnIndex, (15 * 400));
        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Cost");

        hssfSheet.setColumnWidth(columnIndex, (15 * 400));
        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Price");

        hssfSheet.setColumnWidth(columnIndex, (15 * 200));
        hssfCell = hssfRow.createCell(columnIndex);
        hssfCell.setCellValue("House");


        houseFieldStart = columnIndex;

        HSSFRow houseRow = hssfSheet.createRow(1);
        for (int i = 0; i < houseList.size(); i++) {
            hssfSheet.setColumnWidth(columnIndex, (15 * 200));
            hssfCell = houseRow.createCell(columnIndex++);
            hssfCell.setCellValue(houseList.get(i).getHouseName());

        }
//        hssfCell.setCellStyle(styleOri);
        houseFieldEnd=columnIndex-1;
        if(houseFieldStart!=houseFieldEnd){
            hssfSheet.addMergedRegion(new CellRangeAddress(0,0,houseFieldStart,houseFieldEnd));
        }


        hssfSheet.setColumnWidth(columnIndex, (15 * 200));
        hssfCell = hssfRow.createCell(columnIndex);
        hssfCell.setCellValue("Total Qty");

        hssfSheet.addMergedRegion(new CellRangeAddress(0,1,columnIndex,columnIndex));

        // Merge cells in header
        for (int i = 0; i < 6; i++) {
            hssfSheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }



        for (int i = 0; i < this.listOfItem.size(); i++) {
            Inventory_class item = this.listOfItem.get(i);
            HSSFRow rowData = hssfSheet.createRow(i + 2);

            hssfCell = rowData.createCell(0);
            hssfCell.setCellValue(i+1);

            hssfCell = rowData.createCell(1);
            hssfCell.setCellValue(item.getBarcode());

            hssfCell = rowData.createCell(2);
            hssfCell.setCellValue(item.getItemCode());

            hssfCell = rowData.createCell(3);
            hssfCell.setCellValue(item.getItemName());

            hssfCell = rowData.createCell(4);
            hssfCell.setCellValue(item.getCost());

            hssfCell = rowData.createCell(5);
            hssfCell.setCellValue(item.getPrice());

            HSSFCell [] houseCell = new HSSFCell[houseList.size()];

            for (int j = 0; j < houseList.size() ; j++) {
                houseCell[j] = rowData.createCell(j+houseFieldStart);
            }

            for (int j = 0; j < houseList.size(); j++) {
                for (Inventory_class temp: houseList.get(j)) {
                    if (temp.getBarcode().equals(item.getBarcode())){
                        houseCell[j].setCellValue(Integer.parseInt(temp.getQuantity()));
                        break;
                    }else{
                        houseCell[j].setCellValue(0);
                    }
                }
            }

            // Total of the quantity of respective item
            String startAddress = houseCell[0].getAddress().formatAsString();
            String endAddress = houseCell[houseCell.length-1].getAddress().formatAsString();
            hssfCell = rowData.createCell(houseList.size()+6);
            hssfCell.setCellFormula("SUM("+ startAddress + ":" + endAddress +")");

        }

        HSSFRow rowData = hssfSheet.createRow(listOfItem.size()+2);
        columnIndex = houseFieldStart-1;
        HSSFCell hssfCell = rowData.createCell(columnIndex++);
        hssfCell.setCellValue("Grand Total");

        for (int i = 0; i < houseList.size(); i++) {
            String addressStart = hssfSheet.getRow(2).getCell(columnIndex).getAddress().formatAsString();
            String addressEnd = hssfSheet.getRow(listOfItem.size()+1).getCell(columnIndex).getAddress().formatAsString();
            hssfCell = rowData.createCell(columnIndex++);
            hssfCell.setCellFormula("SUM("+ addressStart + ":" + addressEnd +")");
        }



        filepath = new File(housefile, "Inventory as at_" + currentDateandTime2 + ".xls");
        FileOutputStream fileOutputStream = null;

        boolean isSuccess;

        try {
            fileOutputStream = new FileOutputStream(filepath);
            hssfWorkbook.write(fileOutputStream);
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
}
