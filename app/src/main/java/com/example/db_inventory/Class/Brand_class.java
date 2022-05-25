package com.example.db_inventory.Class;

public class Brand_class {
    String brandCode;
    String brandName;


    public Brand_class() {
    }

    public Brand_class(String brandCode, String brandName) {
        this.brandCode = brandCode;
        this.brandName = brandName;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
