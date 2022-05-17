package com.example.db_inventory;

public class SA_class {
    private String SAHouse;
    private String BookQty;
    private String Barcode;
    private String Qty;
    private int Adj;

    public SA_class() {

    }

    public SA_class(String SAHouse, String bookQty, String barcode, String qty, int adj) {
        this.SAHouse = SAHouse;
        BookQty = bookQty;
        Barcode = barcode;
        Qty = qty;
        Adj = adj;
    }

    public String getSAHouse() {
        return SAHouse;
    }

    public void setSAHouse(String SAHouse) {
        this.SAHouse = SAHouse;
    }

    public String getBookQty() {
        return BookQty;
    }

    public void setBookQty(String bookQty) {
        BookQty = bookQty;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public int getAdj() {
        return Adj;
    }

    public void setAdj(int adj) {
        Adj = adj;
    }
}
