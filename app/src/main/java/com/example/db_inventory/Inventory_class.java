package com.example.db_inventory;

public class Inventory_class implements Cloneable {
    private String Barcode, Quantity, ItemName, HouseKey, Price, Cost, Date_and_Time, Key, ItemCode;

    public Inventory_class() {
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public Inventory_class(String barcode, String quantity, String itemName, String houseKey, String price, String cost, String date_and_Time, String key, String itemCode) {
        Barcode = barcode;
        Quantity = quantity;
        ItemName = itemName;
        HouseKey = houseKey;
        Price = price;
        Cost = cost;
        Date_and_Time = date_and_Time;
        Key = key;
        ItemCode = itemCode;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getHouseKey() {
        return HouseKey;
    }

    public void setHouseKey(String houseKey) {
        HouseKey = houseKey;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getDate_and_Time() {
        return Date_and_Time;
    }

    public void setDate_and_Time(String date_and_Time) {
        Date_and_Time = date_and_Time;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }
}
