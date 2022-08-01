package com.uvic.venus.model;

import java.sql.Timestamp;

public class SecretInfoRequest {

    private Long id;
    private String username;
    private String owner;
    private String name;
    private String data;
    private Timestamp timeCreated; 

    public SecretInfoRequest(String username, String name, String data, Timestamp timeCreated) {
        this.username = username;
        this.name = name; 
        this.data = data;
        this.timeCreated = timeCreated;               
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    // @Override
    // public String toString() {
    //     return "SecretInfo [ \r\nid= " + id + "\r\nparent id=" + parentId + "\r\nusername= " + username  + "\r\nname= " + name + "\r\ndata= " + data + "\r\ntimeCreated= " + timeCreated + "\r\n]";
    // }

}
