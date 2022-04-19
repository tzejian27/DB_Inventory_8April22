package com.example.db_inventory;

public class User_List_class {
    private String Username;
    private String Role;
    private String Password;

    public User_List_class() {
    }

    public User_List_class(String username, String role, String password) {
        Username = username;
        Role = role;
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        Username = Username;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
