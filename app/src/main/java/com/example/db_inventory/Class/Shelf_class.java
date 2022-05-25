package com.example.db_inventory.Class;

public class Shelf_class {
    String shelfName;
    int Id;

    public Shelf_class() {
    }

    public Shelf_class(String shelfName) {
        this.shelfName = shelfName;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
