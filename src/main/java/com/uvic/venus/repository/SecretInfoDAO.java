package com.uvic.venus.repository;

import com.uvic.venus.model.SecretInfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretInfoDAO extends JpaRepository <SecretInfo, String>  {

    Optional<SecretInfo> findSecretInfoByName (String name);
}
