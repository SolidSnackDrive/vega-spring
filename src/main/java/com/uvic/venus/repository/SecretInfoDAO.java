package com.uvic.venus.repository;

import com.uvic.venus.model.SecretInfo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretInfoDAO extends JpaRepository <SecretInfo, Long>  {

    List<SecretInfo> findSecretInfoByUsername (String username);
    
}
