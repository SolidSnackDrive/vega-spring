package com.uvic.venus.model;

public class SecretInfoShareRequest {

    
    private Long id;
    private String shareWithUsername;
    

    public SecretInfoShareRequest(Long id, String shareWithUsername) {
  
      
        this.id = id;
        this.shareWithUsername = shareWithUsername;

    }


    
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    
    public String getShareWithUsername() {
        return shareWithUsername;
    }


    public void setShareWithUsername(String shareWithUsername) {
        this.shareWithUsername = shareWithUsername;
    }
    

    

    
}
