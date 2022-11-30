package com.example.db_inventory;

import android.util.Log;

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
    private File housefile;
    private String currentDateandTime2;
    private HSSFSheet hssfSheet;
    private HSSFRow hssfRow;
    private HSSFCell hssfCell;
    private int houseFieldStart, houseFieldEnd;

    public exportInventory(ArrayList<Inventory_class> listOfItem, ArrayList<Inventory_List_All.houseArrayList> houseList, String username) {
        this.listOfItem = listOfItem;
        this.houseList = houseList;
        this.username = username;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        currentDateandTime2 = sdf.format(new Date());
        housefile = new File("/storage/emulated/0/Report/" + "eStock" + currentDateandTime2);
        exportExcel();
    }

    public void exportExcel() {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

        hssfSheet = hssfWorkbook.createSheet(EXCEL_SHEET_NAME);
        hssfSheet.setColumnWidth(0, (15 * 100));
        hssfSheet.setColumnWidth(1, (15 * 400));
        hssfSheet.setColumnWidth(2, (15 * 400));
        hssfSheet.setColumnWidth(3, (15 * 400));
        hssfSheet.setColumnWidth(4, (15 * 400));
        hssfSheet.setColumnWidth(5, (15 * 400));
        hssfSheet.setColumnWidth(6, (15 * 400));
        hssfSheet.setColumnWidth(7, (15 * 400));
        hssfSheet.setColumnWidth(8, (15 * 400));
        hssfSheet.setColumnWidth(9, (15 * 400));

        // Set header value
        int columnIndex =0;
        hssfRow = hssfSheet.createRow(0);

        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("No");

        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Barcode");

        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Item_Code");

        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Item");

        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Cost");

        hssfCell = hssfRow.createCell(columnIndex++);
        hssfCell.setCellValue("Price");

        hssfCell = hssfRow.createCell(columnIndex);
        hssfCell.setCellValue("House");
        houseFieldStart = columnIndex;

        HSSFRow houseRow = hssfSheet.createRow(1);
        for (int i = 0; i < houseList.size(); i++) {
            hssfCell = houseRow.createCell(columnIndex++);
            hssfCell.setCellValue(houseList.get(i).getHouseName());

        }
        houseFieldEnd=columnIndex-1;
        hssfSheet.addMergedRegion(new CellRangeAddress(1,1,houseFieldStart,houseFieldEnd));

        hssfCell = hssfRow.createCell(columnIndex);
        hssfCell.setCellValue("Total Qty");

        hssfSheet.addMergedRegion(new CellRangeAddress(0,1,columnIndex,columnIndex));

        // Merge cells in header
        for (int i = 0; i < 5; i++) {
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
                houseCell[j] = rowData.createCell(j+houseFieldStart-1);
            }

            for (int j = 0; j < houseList.size(); j++) {
                for (Inventory_class temp: houseList.get(j)) {
                    if (temp.getBarcode().equals(item.getBarcode())){
                        houseCell[j].setCellValue(temp.getQuantity());
                        break;
                    }else{
                        houseCell[j].setCellValue(0);
                    }
                }
            }
            String startAddress = houseCell[0].getAddress().formatAsString();
            String endAddress = houseCell[houseCell.length-1].getAddress().formatAsString();
            hssfCell = rowData.createCell(houseList.size()+6);
            hssfCell.setCellFormula("SUM("+ startAddress + ":" + endAddress +")");
            hssfCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);

        }

        File filePath = new File(housefile, "Inventory as at_" + currentDateandTime2 + ".xls");
        FileOutputStream fileOutputStream = null;

        boolean isSuccess;

        try {
            fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);
            Log.e("", "Writing file" + filePath);
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
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
