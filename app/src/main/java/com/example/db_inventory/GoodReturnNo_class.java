package com.example.db_inventory;

public class GoodReturnNo_class {
    public String customerCode;

    public GoodReturnNo_class(){

    }

    public GoodReturnNo_class(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
