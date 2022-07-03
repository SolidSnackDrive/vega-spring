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
import com.uvic.venus.model.SecretInfoUpdateRequest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.sql.Timestamp;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.hamcrest.Matchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc

public class VaultControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private VaultController vaultController;


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

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
            .perform(post("/vault/testuser/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secretInfo1))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    public void createSecretNoName() throws Exception {

        SecretInfo secretInfo = new SecretInfo("testuser", null, "data",Timestamp.valueOf("2020-01-09 23:59:59.0"));
        ObjectMapper objectMapper = new ObjectMapper();
        
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
            ObjectMapper objectMapper = new ObjectMapper();
            
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
            Then they are able to modify the name or data of a secret
    */
    
    @Test
    @WithMockUser(roles="USER")
    public void updateExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoUpdateRequest updateRequest = new SecretInfoUpdateRequest("newName", "newData", "testuser", secretInfo1.getId());
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
        .perform(put("/vault/testuser/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    public void updateNonExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        SecretInfoUpdateRequest updateRequest = new SecretInfoUpdateRequest("newName", "new", "testuser", secretInfo1.getId());
        vaultController.deleteSecret("testuser", secretInfo1);

        ObjectMapper objectMapper = new ObjectMapper();

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(put("/vault/testuser/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())       
        );


    }

    /* 
    Feature: Delete a secret
        Scenario: Users can delete a secret in their vault
            Given a user has an existing account
            And they have been approved to use Vega services by an admin
            When they navigate to the Vega Vault tab
            Then they are able to delete a secret in their vault
    */

    @Test
    @WithMockUser(roles="USER")
    public void deleteExistingSecret() throws JsonProcessingException, Exception {

        vaultController.addNewSecret("testuser", secretInfo1);
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
        .perform(delete("/vault/testuser/delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(secretInfo1))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    public void deleteNonExistingSecret() throws JsonProcessingException, Exception {

        SecretInfo secretInfo1 = new SecretInfo("testuser","secret1","data", Timestamp.valueOf("2020-01-09 23:59:59.0"));
        ObjectMapper objectMapper = new ObjectMapper();

        assertThrows(NestedServletException.class, () ->         
            mockMvc
            .perform(delete("/vault/testuser/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secretInfo1))
            .accept(MediaType.APPLICATION_JSON))             
       
        );

    }

}