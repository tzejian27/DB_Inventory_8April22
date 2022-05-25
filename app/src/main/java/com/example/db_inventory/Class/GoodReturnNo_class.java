package com.example.db_inventory.Class;

public class GoodReturnNo_class {
    private String goodReturnNo,customerCode;

    public GoodReturnNo_class(){}

    public GoodReturnNo_class(String goodReturnNo, String customerCode) {
        this.goodReturnNo = goodReturnNo;
        this.customerCode = customerCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getGoodReturnNo() {
        return goodReturnNo;
    }

    public void setGoodReturnsNo(String goodReturnsNo) {
        this.goodReturnNo = goodReturnNo;
    }


}
