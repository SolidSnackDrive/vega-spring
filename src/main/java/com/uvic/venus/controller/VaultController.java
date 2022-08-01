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
import com.uvic.venus.model.SecretInfoRequest;
import com.uvic.venus.model.SecretInfoShareRequest;
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

    public void addNewSecret(@PathVariable("username") String username, @RequestBody SecretInfoRequest secretInfoRequest) {

        SecretInfo secretInfo = new SecretInfo(secretInfoRequest.getUsername(), 
                                                secretInfoRequest.getName(), 
                                                secretInfoRequest.getData(), 
                                                secretInfoRequest.getTimeCreated());
	
        if (secretInfo.getName() == null) {
            throw new IllegalArgumentException("Secret Name Must Be Non Null"); 
        }
        if (secretInfo.getData() == null) {
            throw new IllegalArgumentException("Secret Data Must Be Non Null"); 
        }
        secretInfo.setOwner(username);
        secretInfoDAO.save(secretInfo);   
    }

    @Transactional
    @DeleteMapping(path = "{username}/delete")
    public void deleteSecret(@PathVariable("username") String username, @RequestBody SecretInfoRequest secretRequest) {

        SecretInfo secret = secretInfoDAO.findSecretInfoByUsernameAndId(username, secretRequest.getId());

        if (secret == null) {
            throw new IllegalStateException("secret with id " + secretRequest.getId() + " doesn't exist for user " + username);
        }

        if (!username.equals(secret.getOwner())) {
            throw new IllegalArgumentException("username" + username + "does not own secret with id" + secretRequest.getId());
        }

        secretInfoDAO.deleteById(secret.getId());
        secretInfoDAO.deleteByParentId(secret.getId());
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
        if (!username.equals(secret.getOwner())) {
            throw new IllegalArgumentException("username " + username + " does not own secret with id" + secret.getId());
        }


        if (newName != null) {
            secret.setName(newName);
        }

        if (newData != null) {
            secret.setData(newData);
        }

        List<SecretInfo> childSecrets = secretInfoDAO.findSecretInfoByParentId(secret.getId());

        for (SecretInfo s : childSecrets) {
            if (newName != null) {
                s.setName(newName);
            }
    
            if (newData != null) {
                s.setData(newData);
            }
        }  
    }

    @Transactional
    @PutMapping(path = "{username}/share")
    public void shareSecret(
                        @PathVariable("username") String username,
                        @RequestBody SecretInfoShareRequest secretInfoShare) 
    {
        Long id = secretInfoShare.getId();
        String sharedUser = secretInfoShare.getShareWithUsername();
        SecretInfo secret = secretInfoDAO.findSecretInfoByUsernameAndId(username, id);

        if (secret == null) {
            throw new IllegalStateException("secret with id " + id + " doesn't exist");
        }

        if (!username.equals(secret.getOwner())) {
            throw new IllegalArgumentException("username" + username + "does not own secret with id" + id);
        }

        if (username.equals(sharedUser)) {
            throw new IllegalArgumentException("username" + username + "can't share with itself" + id);
        }
               
        SecretInfo sharedSecret = new SecretInfo();
        sharedSecret.setParentId(secret.getId());
        sharedSecret.setName(secret.getName());
        sharedSecret.setData(secret.getData());
        sharedSecret.setTimeCreated(secret.getTimeCreated());
        sharedSecret.setOwner(username);
        sharedSecret.setUsername(sharedUser);

        secretInfoDAO.save(sharedSecret);  
    }

    @Transactional
    @PutMapping(path = "{username}/transfer")
    public void transferOwnership(
                        @PathVariable("username") String username,
                        @RequestBody SecretInfoRequest secretInfoRequest) 
    {        
        Long id = secretInfoRequest.getId();
        SecretInfo secret = secretInfoDAO.findSecretInfoByUsernameAndId(username, id);

        if (secret == null) {
            throw new IllegalStateException("secret with id " + id + " doesn't exist");
        }

        if (!username.equals(secret.getOwner())) {
            throw new IllegalArgumentException("username" + username + "does not own secret with id" + id);
        }

        if (username.equals(secretInfoRequest.getOwner())) {
            throw new IllegalArgumentException("username" + username + "can't transfer with itself" + id);
        }

        secret.setOwner(secretInfoRequest.getOwner());
        secret.setUsername(secretInfoRequest.getOwner());

        List<SecretInfo> childSecrets = secretInfoDAO.findSecretInfoByParentId(secret.getId());

        for (SecretInfo s : childSecrets) {
            s.setOwner(secretInfoRequest.getOwner());
        }        
    }

    // methods below added for using with VaultControllerTests

    public void deleteAll() {
        secretInfoDAO.deleteAll();
    }

    public List<SecretInfo> findAllByUserName(String username){
        return secretInfoDAO.findSecretInfoByUsername(username,Sort.by("timeCreated").descending());
    }
       
}
