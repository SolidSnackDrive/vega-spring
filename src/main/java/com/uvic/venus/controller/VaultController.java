package com.uvic.venus.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uvic.venus.model.SecretInfo;
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
    public List <SecretInfo> getSecrets() {

        List<SecretInfo> secrets  = secretInfoDAO.findAll();

        return secrets;
    }

    @GetMapping (path = "{username}")
    public List <SecretInfo> getUsernameSecrets(@PathVariable("username") String username ) {

        List<SecretInfo> secrets  = secretInfoDAO.findSecretInfoByUsername(username);

        return secrets;
    }

    @PostMapping (path = "{username}/add")
    // @RequestMapping(value="/add", method = RequestMethod.POST)
    public void addNewSecret(@RequestBody SecretInfo secretInfo) {
        //Optional<SecretInfo> secretOptional =  secretInfoDAO.findSecretInfoByName(secretInfo.getName());
        // SecretInfo secretInfo1 = new SecretInfo("Test1", null,  "secretsss");
        System.out.println(secretInfo);
		secretInfoDAO.save(secretInfo);
    }

   
}
