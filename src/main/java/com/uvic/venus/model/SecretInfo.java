package com.uvic.venus.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="secretinfo")
public class SecretInfo {
    
    @Id
    private String name;
    private String data;
    private Timestamp timeCreated;
   

    public SecretInfo(String name, String data, Timestamp timeCreated) {
        this.name = name; 
        this.data = data;
        this.timeCreated = timeCreated;
       
    }

    public SecretInfo() {
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

    @Override
    public String toString() {
        return "SecretInfo [name=" + name + ", data=" + data + ", timeCreated=" + timeCreated + "]";
    }   

        
}
