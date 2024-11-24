package com.sopotek.aipower.repository;

import com.sopotek.aipower.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {


    @Query(
            "SELECT t FROM Token t WHERE t.username = :username"
            // If you want to use a custom query, you can specify it here instead of using the @Query annotation.
    )
    List<Token> findByUsername(String username);
}
