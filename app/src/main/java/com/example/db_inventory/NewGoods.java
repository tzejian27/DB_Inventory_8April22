package com.example.db_inventory;

public class NewGoods {

    private String barcode;
    private String name;
    private String price;
    private String cost;

    public NewGoods(){}

    public NewGoods(String barcode, String name, String price, String cost) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.cost = cost;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
