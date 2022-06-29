package com.uvic.venus.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvic.venus.model.SecretInfo;
import com.uvic.venus.model.SecretInfoUpdateRequest;
import com.uvic.venus.repository.SecretInfoDAO;


@RestController
@RequestMapping ("vault")

public class VaultController {

    private final SecretInfoDAO secretInfoDAO;
     
    @Autowired
    public VaultController(SecretInfoDAO secretInfoDAO) {
        this.secretInfoDAO = secretInfoDAO;
    }
    @GetMapping ({"{username}", "{username}/from={fromDate}&to={toDate}"})
    public ResponseEntity <?> getUsernameSecrets(@PathVariable("username") String username, 
                                                 @PathVariable(name = "fromDate", required = false) String fromDate, 
                                                 @PathVariable(name = "toDate", required = false) String toDate) throws ParseException 

    {
        List<SecretInfo> secrets  = secretInfoDAO.findSecretInfoByUsername(username, Sort.by("timeCreated").descending());

        if (fromDate != null && toDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Timestamp fromTimestamp = new Timestamp ((dateFormat.parse(fromDate)).getTime());
            Timestamp toTimestamp = new Timestamp ((dateFormat.parse(toDate)).getTime());

            secrets  = secretInfoDAO
            .findSecretInfoByTimeCreatedBetweenAndUsername(fromTimestamp,toTimestamp,username, Sort.by("timeCreated").descending());
        }

        return ResponseEntity.ok(secrets);
    }

 
    @PostMapping (path = "{username}/add")

    public void addNewSecret(@PathVariable("username") String username, @RequestBody SecretInfo secretInfo) {
		
        if (secretInfo.getName() == null) {
            throw new IllegalArgumentException("Secret Name Must Be Non Null"); 
        }

        if (secretInfo.getData() == null) {
            throw new IllegalArgumentException("Secret Data Must Be Non Null"); 
        }
        secretInfoDAO.save(secretInfo);   
    }

    @Transactional
    @DeleteMapping(path = "{username}/delete")
    public void deleteSecret(@PathVariable("username") String username, @RequestBody SecretInfo secret) {

        boolean exists = secretInfoDAO.existsByUsernameAndId(username, secret.getId());

        if (!exists) {
            throw new IllegalStateException("secret with id " + secret.getId() + " doesn't exist for user " + username);
        }

        secretInfoDAO.deleteById(secret.getId());
    }

    @Transactional
    @PutMapping(path = "{username}/update")
    public void updateSecret(
                        @PathVariable("username") String username,
                        @RequestBody SecretInfoUpdateRequest secretInfoUpdate) 
    {
        Long id = secretInfoUpdate.getId();
        String newName = secretInfoUpdate.getNewName();
        String newData  = secretInfoUpdate.getNewData();

        SecretInfo secret = secretInfoDAO.findSecretInfoByUsernameAndId(username, id);

        if (secret == null) {

            throw new IllegalStateException("secret with id " + id + " doesn't exist");
        }


        if (newName != null) {
            secret.setName(newName);
        }

        if (newData != null) {
            secret.setData(newData);
        }
  
    }
       
}
