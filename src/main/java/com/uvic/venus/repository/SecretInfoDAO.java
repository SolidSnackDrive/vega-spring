package com.uvic.venus.repository;

import com.uvic.venus.model.SecretInfo;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretInfoDAO extends JpaRepository <SecretInfo, Long>  {
    List<SecretInfo> findSecretInfoByUsername(String username, org.springframework.data.domain.Sort sort);
    List<SecretInfo> findSecretInfoByTimeCreatedBetweenAndUsername(Timestamp fromDate, Timestamp toDate, String username, org.springframework.data.domain.Sort sort);
    SecretInfo findSecretInfoByUsernameAndId (String username, Long id);
    List<SecretInfo> findSecretInfoByParentId(Long parentId);
    boolean existsByUsernameAndId (String username, Long id);
    void deleteByName(String name);
    void deleteByParentId(Long parentId);
}
