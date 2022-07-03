package com.uvic.venus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {
    
    @Autowired
    MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles="ADMIN")
    public void testFetchUsers() throws Exception{
        MvcResult req = mockMvc.perform(get("/admin/fetchusers")).andDo(print()).andReturn();
        
        assertEquals(200, req.getResponse().getStatus());
        assertNotNull(req.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testEnableUser() throws Exception{
        MvcResult req = mockMvc.perform(get("/admin/enableuser")
                        .param("username", "testuser@venus.com")
                        .param("enable", "true"))
                        .andReturn();
        
        assertEquals(200, req.getResponse().getStatus());
        assertEquals("User Updated Successfully", req.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testChangeRole() throws Exception{
        MvcResult req = mockMvc.perform(get("/admin/changerole")
                        .param("username", "testuser@venus.com")
                        .param("role", "ROLE_STAFF"))
                        .andDo(print())
                        .andReturn();
        
        assertEquals(200, req.getResponse().getStatus());
        assertEquals("User Updated Successfully", req.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file","filename.txt", "text/plain", "some xml".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/handlefileupload")
                .file(file))
                .andExpect(status().is(200));
    }

}
