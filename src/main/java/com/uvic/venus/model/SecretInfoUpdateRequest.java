package com.uvic.venus.model;

public class SecretInfoUpdateRequest {

    private String name;
    private String newName;
    private String newData;
    private String username;

    public SecretInfoUpdateRequest(String name, String newName, String newData, String username) {
        this.name = name;
        this.newName = newName;
        this.newData = newData;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    

    

    
}
