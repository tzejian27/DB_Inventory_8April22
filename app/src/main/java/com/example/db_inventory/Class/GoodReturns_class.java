package com.example.db_inventory.Class;

public class GoodReturns_class {
    private String Barcode,Color,DateAndTime,ItemCode,Qty,Size,salesOrderNo;

    public GoodReturns_class()
    {

    }

    public GoodReturns_class(String Barcode, String Color, String DateAndTime, String ItemCode, String Qty, String Size, String salesOrderNo)
    {
        this.Barcode = Barcode;
        this.Color  = Color;
        this.DateAndTime = DateAndTime;
        this.ItemCode = ItemCode;
        this.Qty = Qty;
        this.Size = Size;
        this.salesOrderNo = salesOrderNo;
    }


    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }
}
