package com.uvic.venus.repository;

import com.uvic.venus.model.SecretInfo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretInfoDAO extends JpaRepository <SecretInfo, Long>  {

    List<SecretInfo> findSecretInfoByUsername (String username);
    SecretInfo findSecretInfoByUsernameAndName (String username, String name);
    boolean existsByUsernameAndName (String username, String name);
    void deleteByName(String name);
}
