package com.example.socialmediatdcproject.model;

public class Role {
    private int id;
    private String roleName;

    public Role() {
        this.roleName = "";
        this.id = -1;
    }

    public Role(int id, String roleName) {
        this.roleName = roleName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
