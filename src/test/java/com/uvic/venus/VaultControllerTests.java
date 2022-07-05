package com.uvic.venus;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uvic.venus.controller.VaultController;
import com.uvic.venus.model.SecretInfo;
import com.uvic.venus.model.SecretInfoShareRequest;
import com.uvic.venus.model.SecretInfoUpdateRequest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import  java.sql.Timestamp;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc

public class VaultControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private VaultController vaultController;

    private ObjectMapper objectMapper = new ObjectMapper();


    private SecretInfo secretInfo1 = new SecretInfo("testuser","secret1","data", Timestamp.valueOf("2020-01-09 23:59:59.0"));
    private SecretInfo secretInfo2 = new SecretInfo("testuser","secret2","data", Timestamp.valueOf("2020-01-10 00:00:00.0")); 
    private SecretInfo secretInfo3 = new SecretInfo("testuser","secret3","data", Timestamp.valueOf("2020-01-15 00:00:01.0")); 
    private SecretInfo secretInfo4 = new SecretInfo("testuser","secret4","data", Timestamp.valueOf("2020-01-19 23:59:59.0")); 
    private SecretInfo secretInfo5 = new SecretInfo("testuser","secret5","data", Timestamp.valueOf("2020-01-20 00:00:00.0")); 
    private SecretInfo secretInfo6 = new SecretInfo("testuser","secret6","data", Timestamp.valueOf("2020-01-20 00:00:01.0")); 

    @AfterEach 
    void teardown() {
        vaultController.deleteAll();
    }

    /* 
        Feature: Read secrets
        Scenario: Users can see a list of all secrets in their vault
            Given a user has an existing account
            And they have been approved to use Vega services by an admin
            When they navigate to the Vega Vault tab
            Then they are able to see all secrets contained in their vault
            And they can filter secret entries by date
    */
 
    @Test
    @WithMockUser(roles= "USER")
    public void readEmptySecretList() throws Exception {

                    mockMvc
                            .perform(get("/vault/"+"testuser"))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(content().contentType("application/json"))
                            .andExpect(content().string("[]"));

    }

    @Test
    @WithMockUser(roles="USER")
    public void readNonEmptySecretList() throws Exception {


        vaultController.addNewSecret("testuser", secretInfo1);
        vaultController.addNewSecret("testuser", secretInfo2);

        mockMvc
            .perform(get("/vault/testuser"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[*].id ", Matchers.contains(secretInfo2.getId().intValue(),secretInfo1.getId().intValue()) ))   
            .andExpect(jsonPath("$[*].name ", Matchers.contains("secret2","secret1") ))                    
            .andReturn();

    }


    @Test
    @WithMockUser(roles="USER")
    public void filterSecretList() throws Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        vaultController.addNewSecret("testuser", secretInfo2);
        vaultController.addNewSecret("testuser", secretInfo3);
        vaultController.addNewSecret("testuser", secretInfo4);
        vaultController.addNewSecret("testuser", secretInfo5);
        vaultController.addNewSecret("testuser", secretInfo6);

        mockMvc
            .perform(get("/vault/testuser/from=2020-01-10&to=2020-01-20"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[*].id ", Matchers.contains(5,4,3,2)))   
            .andExpect(jsonPath("$[*].name ", Matchers.contains("secret5", "secret4", "secret3", "secret2")))                    
            .andReturn();

    }

    /*
    Feature: Create a new secret
    Scenario: Users can add a new secret to their vault
        Given a user has an existing account
        And they have been approved to use Vega services by an admin
        When they navigate to the Vega Vault tab
        Then they are able to create a new secret
        And they can save the secret to the vault

    */

    @Test
    @WithMockUser(roles="USER")
    public void createSecret() throws Exception {

        mockMvc
            .perform(post("/vault/testuser/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secretInfo1))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
            
        SecretInfo newSecret = vaultController.findAllByUserName("testuser").get(0);   
        assertNull(newSecret.getParentId());  
        assertEquals(newSecret.getName(),secretInfo1.getName());
        assertEquals(newSecret.getData(),secretInfo1.getData());
        assertEquals(newSecret.getTimeCreated(),secretInfo1.getTimeCreated());

    }

    @Test
    @WithMockUser(roles="USER")
    public void createSecretNoName() throws Exception {

        SecretInfo secretInfo = new SecretInfo("testuser", null, "data",Timestamp.valueOf("2020-01-09 23:59:59.0"));
        
        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(post("/vault/testuser/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secretInfo))
            .accept(MediaType.APPLICATION_JSON))
        );
    }

        @Test
        @WithMockUser(roles="USER")
        public void createSecretNoData() throws Exception {
    
            SecretInfo secretInfo = new SecretInfo("testuser", "Name", null,Timestamp.valueOf("2020-01-09 23:59:59.0"));
            
            assertThrows(NestedServletException.class, () ->         
                mockMvc
                .perform(post("/vault/testuser/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secretInfo))
                .accept(MediaType.APPLICATION_JSON))
            );



    }
    /* 
    Feature: Update a secret
        Scenario: Users can update a secret in their vault
            Given a user has an existing account
            And they have been approved to use Vega services by an admin
            When they navigate to the Vega Vault tab
            Then they are able to modify the name or data of a secret they own
    */
    
    @Test
    @WithMockUser(roles="USER")
    public void updateExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoUpdateRequest updateRequest = new SecretInfoUpdateRequest("newName", "newData", "testuser", secretInfo1.getId());

        mockMvc
        .perform(put("/vault/testuser/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        SecretInfo updatedSecret = vaultController.findAllByUserName("testuser").get(0);        
        assertEquals(updateRequest.getNewName(),updatedSecret.getName());
        assertEquals(updateRequest.getNewData(),updatedSecret.getData());
        assertEquals(secretInfo1.getId(),updatedSecret.getId());
        assertEquals(secretInfo1.getTimeCreated(),updatedSecret.getTimeCreated());
    }
   


    @Test
    @WithMockUser(roles="USER")
    public void updateNonExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoUpdateRequest updateRequest = new SecretInfoUpdateRequest("newName", "new", "testuser", secretInfo1.getId());
        vaultController.deleteSecret("testuser", secretInfo1);

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/testuser/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())       
        );

    }

    @Test
    @WithMockUser(roles="USER")
    public void updateSecretNotOwned() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");
        vaultController.shareSecret("testuser",shareRequest);
        SecretInfo adminSharedSecret = vaultController.findAllByUserName("admin").get(0);  

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/admin/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminSharedSecret))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()) 
        );
    }

     @Test
    @WithMockUser(roles="USER")
    public void updateOwnedSecretThatHasBeenShared() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");
        vaultController.shareSecret("testuser",shareRequest);
        SecretInfoUpdateRequest updateRequest = new SecretInfoUpdateRequest("newName", "newData", "testuser", secretInfo1.getId());

        mockMvc
        .perform(put("/vault/testuser/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        SecretInfo updatedTestUserSecret = vaultController.findAllByUserName("testuser").get(0);        
        assertEquals(updateRequest.getNewName(),updatedTestUserSecret.getName());
        assertEquals(updateRequest.getNewData(),updatedTestUserSecret.getData());
        assertEquals(secretInfo1.getId(),updatedTestUserSecret.getId());
        assertEquals(secretInfo1.getTimeCreated(),updatedTestUserSecret.getTimeCreated());

        SecretInfo testAdminUpdatedSecret = vaultController.findAllByUserName("admin").get(0);
        assertEquals(updateRequest.getNewName(),testAdminUpdatedSecret.getName());
        assertEquals(updateRequest.getNewData(),testAdminUpdatedSecret.getData());
        assertEquals(secretInfo1.getId(),testAdminUpdatedSecret.getParentId());
        assertEquals(secretInfo1.getTimeCreated(),testAdminUpdatedSecret.getTimeCreated());
    }

    /* 

    Feature: Delete a secret
        Scenario: Users can delete a secret in their vault
            Given a user has an existing account
            And they have been approved to use Vega services by an admin
            When they navigate to the Vega Vault tab
            Then they are able to delete a secret they own in their vault
    */

    @Test
    @WithMockUser(roles="USER")
    public void deleteExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfo secret = vaultController.findAllByUserName("testuser").get(0);    


        mockMvc
        .perform(delete("/vault/testuser/delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(secretInfo1))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        assertThat(vaultController.findAllByUserName("testuser"),not(hasItem(secret)) );        

    }

    @Test
    @WithMockUser(roles="USER")
    public void deleteNonExistingSecret() throws JsonProcessingException, Exception {

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(delete("/vault/testuser/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secretInfo1))
            .accept(MediaType.APPLICATION_JSON)) 
            .andExpect(status().isOk())            
       
        );

    }

    @Test
    @WithMockUser(roles="USER")
    public void deleteSecretNotOwned() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");
        vaultController.shareSecret("testuser",shareRequest);
        SecretInfo adminSharedSecret = vaultController.findAllByUserName("admin").get(0);    

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(delete("/vault/admin/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminSharedSecret))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())             
       
        );

    }

    @Test
    @WithMockUser(roles="USER")
    public void deleteOwnedSecretThatHasBeenShared() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");
        vaultController.shareSecret("testuser",shareRequest);
        SecretInfo adminSharedSecret = vaultController.findAllByUserName("admin").get(0);   
        SecretInfo testUserSecret = vaultController.findAllByUserName("testuser").get(0);    
 
              
            mockMvc
            .perform(delete("/vault/testuser/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secretInfo1))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());             
    
        assertThat(vaultController.findAllByUserName("testuser"), not(hasItem(testUserSecret)));

        assertThat(vaultController.findAllByUserName("admin"), not(hasItem(adminSharedSecret)));
    }


    
    /* 
    Feature: Share a secret
        Scenario: Users can share a secret they own in their vault with another user
            Given a user has an existing account
            And they have been approved to use Vega services by an admin
            When they navigate to the Vega Vault tab
            Then they are able to share a secret with another approved user
            And the other user can see the shared secret in their vault
     */


    @Test
    @WithMockUser(roles="USER")
    public void shareExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");

        mockMvc
        .perform(put("/vault/testuser/share")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(shareRequest))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        SecretInfo adminSharedSecret = vaultController.findAllByUserName("admin").get(0);  

        assertEquals(secretInfo1.getId(), adminSharedSecret.getParentId());
        assertNotEquals(secretInfo1.getId(), adminSharedSecret.getId());
        assertEquals(secretInfo1.getName(), adminSharedSecret.getName());
        assertEquals(secretInfo1.getData(), adminSharedSecret.getData());
        assertEquals(secretInfo1.getTimeCreated(), adminSharedSecret.getTimeCreated());


    }

    @Test
    @WithMockUser(roles="USER")
    public void shareNonExistingSecret() throws JsonProcessingException, Exception {

        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/testuser/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shareRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())         
    
        );
        
    }

    @Test
    @WithMockUser(roles="USER")
    public void shareSecretNotOwned() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"admin");
        vaultController.shareSecret("testuser",shareRequest);
        SecretInfo adminSharedSecret = vaultController.findAllByUserName("admin").get(0);    

        SecretInfoShareRequest shareAdminRequest = new SecretInfoShareRequest(adminSharedSecret.getId(),"paulaguilar");

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/admin/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shareAdminRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())        

        );
       }

    @Test
    @WithMockUser(roles="USER")
    public void shareSecretWithSelf() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        ObjectMapper objectMapper = new ObjectMapper();
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secretInfo1.getId(),"testuser");

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/testuser/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shareRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())        

        );
    }

    /*
    Feature: Transfer ownership of a secret
        Scenario: Users can transfer ownership of a secret to another user
            Given a user has an existing account
            And they have been approved to use Vega services by an admin
            When they navigate to the Vega Vault tab
            Then they are able to transfer ownership of a secret to another approved   user
            And the other user can perform all CRUD operations on the secret
    */

    @Test
    @WithMockUser(roles="USER")
    public void transferExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfo secret = vaultController.findAllByUserName("testuser").get(0);  
        SecretInfo testUserSecret = vaultController.findAllByUserName("testuser").get(0);    
        secret.setOwner("admin"); 

        mockMvc
        .perform(put("/vault/testuser/transfer")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(secret))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        assertThat(vaultController.findAllByUserName("testuser"), not(hasItem(testUserSecret)));

        SecretInfo adminSecret = vaultController.findAllByUserName("admin").get(0);  

        assertEquals(secret.getId(), adminSecret.getId());
        assertEquals(secret.getName(), adminSecret.getName());
        assertEquals(secret.getData(), adminSecret.getData());
        assertEquals(secret.getTimeCreated(), adminSecret.getTimeCreated());
        assertEquals("admin", adminSecret.getOwner());

    }

    @Test
    @WithMockUser(roles="USER")
    public void transferNonExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfo secret = vaultController.findAllByUserName("testuser").get(0); 
        vaultController.deleteAll();

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/testuser/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secret))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())         
    
        );
    }

        @Test
        @WithMockUser(roles="USER")
        public void transferSecretNotOwned() throws JsonProcessingException, Exception {
    
            vaultController.addNewSecret("testuser", secretInfo1);
            SecretInfo secret = vaultController.findAllByUserName("testuser").get(0); 
            SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(secret.getId(),"admin");
            vaultController.shareSecret("testuser",shareRequest);
            SecretInfo adminSharedSecret = vaultController.findAllByUserName("admin").get(0); 

    
            assertThrows(NestedServletException.class, () ->         
                mockMvc
                .perform(put("/vault/admin/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminSharedSecret))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())         
        
            );
        
    }

    @Test
    @WithMockUser(roles="USER")
    public void transferOwnedSecretThatHasBeenShared() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfo secret = vaultController.findAllByUserName("testuser").get(0);  
        SecretInfo testUserSecret = vaultController.findAllByUserName("testuser").get(0);  
        
        SecretInfoShareRequest shareRequest = new SecretInfoShareRequest(testUserSecret.getId(),"paulaguilar");
        vaultController.shareSecret("testuser",shareRequest);

        secret.setOwner("admin"); 

        mockMvc
        .perform(put("/vault/testuser/transfer")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(secret))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        assertThat(vaultController.findAllByUserName("testuser"), not(hasItem(testUserSecret)));

        SecretInfo adminSecret = vaultController.findAllByUserName("admin").get(0);  

        assertEquals(secret.getId(), adminSecret.getId());
        assertEquals(secret.getName(), adminSecret.getName());
        assertEquals(secret.getData(), adminSecret.getData());
        assertEquals(secret.getTimeCreated(), adminSecret.getTimeCreated());
        assertEquals("admin", adminSecret.getOwner());


        SecretInfo paulaguilarSecret = vaultController.findAllByUserName("paulaguilar").get(0);  
        assertEquals(secret.getId(), paulaguilarSecret.getParentId());
        assertEquals(secret.getName(), paulaguilarSecret.getName());
        assertEquals(secret.getData(), paulaguilarSecret.getData());
        assertEquals(secret.getTimeCreated(), paulaguilarSecret.getTimeCreated());
        assertEquals("admin", paulaguilarSecret.getOwner());

}

}