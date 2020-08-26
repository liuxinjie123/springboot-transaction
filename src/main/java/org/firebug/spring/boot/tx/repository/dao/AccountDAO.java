package org.firebug.spring.boot.tx.repository.dao;

import org.firebug.spring.boot.tx.repository.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDAO extends JpaRepository<AccountEntity, Integer> {
    @Modifying
    @Query(value = "update t_account set balance=0", nativeQuery = true)
    void reset();
}