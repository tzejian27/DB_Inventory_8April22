package com.example.db_inventory;

public class HouseInventory {

    private String Name;
    private String TotalQty;
    private String TotalType;
    private String Key;
    private String Barcode, Quantity, ItemName, HouseKey, Price, Cost, Date_and_Time, Key2;

    public HouseInventory() {
    }

    public HouseInventory(String name, String totalQty, String totalType, String key, String barcode, String quantity, String itemName, String houseKey, String price, String cost, String date_and_Time, String key2) {
        Name = name;
        TotalQty = totalQty;
        TotalType = totalType;
        Key = key;
        Barcode = barcode;
        Quantity = quantity;
        ItemName = itemName;
        HouseKey = houseKey;
        Price = price;
        Cost = cost;
        Date_and_Time = date_and_Time;
        Key2 = key2;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(String totalQty) {
        TotalQty = totalQty;
    }

    public String getTotalType() {
        return TotalType;
    }

    public void setTotalType(String totalType) {
        TotalType = totalType;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
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

    public String getKey2() {
        return Key2;
    }

    public void setKey2(String key2) {
        Key2 = key2;
    }
}
