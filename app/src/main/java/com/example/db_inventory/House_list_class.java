package com.example.db_inventory;

public class House_list_class {
    private String Name;
    private String TotalQty;
    private String TotalType;
    private String Key;

    public House_list_class(){}


    public House_list_class(String name, String totalQty, String totalType, String key) {
        Name = name;
        TotalQty = totalQty;
        TotalType = totalType;
        Key = key;
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
}
