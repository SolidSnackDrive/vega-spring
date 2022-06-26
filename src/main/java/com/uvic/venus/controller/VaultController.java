package com.uvic.venus.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping (path = "admin")
    public ResponseEntity <?> getSecrets() {

        List<SecretInfo> secrets  = secretInfoDAO.findAll();
        return ResponseEntity.ok(secrets);

    }

    @GetMapping (path = "{username}")
    public ResponseEntity <?> getUsernameSecrets(@PathVariable("username") String username ) {

        List<SecretInfo> secrets  = secretInfoDAO.findSecretInfoByUsername(username);
        return ResponseEntity.ok(secrets);
    }

    @PostMapping (path = "{username}/add")

    public void addNewSecret(@PathVariable("username") String username, @RequestBody SecretInfo secretInfo) {
		
        SecretInfo secret  = secretInfoDAO.findSecretInfoByUsernameAndName(username, secretInfo.getName());

        if (secretInfo.getName() == null  || secret != null && secretInfo.getName().equals(secret.getName())) {
            throw new IllegalArgumentException("Secret Name Must Be Not null and unique"); 

        }
        secretInfoDAO.save(secretInfo);   
    }

    @Transactional
    @DeleteMapping(path = "{username}/delete")
    public void deleteSecret(@PathVariable("username") String username, @RequestBody SecretInfo secret) {

        boolean exists = secretInfoDAO.existsByUsernameAndName(username, secret.getName());

        if (!exists) {
            throw new IllegalStateException("secret with name " + secret.getName() + " doesn't exist");
        }

        secretInfoDAO.deleteByName(secret.getName());
    }

    @Transactional
    @PutMapping(path = "{username}/update")
    public void updateSecret(
                        @PathVariable("username") String username,
                        @RequestBody SecretInfoUpdateRequest secretInfoUpdate) 
    {
        String name = secretInfoUpdate.getName();
        String newName = secretInfoUpdate.getNewName();
        String newData  = secretInfoUpdate.getNewData();

        SecretInfo secret = secretInfoDAO.findSecretInfoByUsernameAndName(username, name);

        if (secret == null) {

            throw new IllegalStateException("secret with name " + name + " doesn't exist");
        }


        if (newName != null && !secret.getName().equals(newName)) {
            secret.setName(newName);
        }

        if (newData != null && !secret.getData().equals(newData)) {
            secret.setData(newData);
        }
  
    }
       
}
