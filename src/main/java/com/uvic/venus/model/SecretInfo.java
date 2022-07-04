package com.uvic.venus.model;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="secretinfo")
public class SecretInfo {
    
    @Id
    @SequenceGenerator(
        name = "secret_sequence",
        sequenceName = "secret_sequence",
        allocationSize = 1
    )

    @GeneratedValue (
        strategy = GenerationType.SEQUENCE,
        generator = "secret_sequence"
    )

    private Long id;
    private Long parentId;
    private String username;
    private String owner;
    private String name;
    private String data;
    private Timestamp timeCreated;
   

    public SecretInfo(String username, String name, String data, Timestamp timeCreated) {
        this.username = username;
        this.name = name; 
        this.data = data;
        this.timeCreated = timeCreated;               
    }

    public SecretInfo() {
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

    @Override
    public String toString() {
        return "SecretInfo [ \r\nid= " + id + "\r\nparent id=" + parentId + "\r\nusername= " + username  + "\r\nname= " + name + "\r\ndata= " + data + "\r\ntimeCreated= " + timeCreated + "\r\n]";
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (!(obj instanceof SecretInfo))
        return false;

        SecretInfo s = (SecretInfo) obj;
        return (this.id.equals(s.getId()));
    }
    
        
}
