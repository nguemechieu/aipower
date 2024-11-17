package com.sopotek.aipower.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.sopotek.aipower.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


   @Query(
            "SELECT u FROM User u " +
            "WHERE u.username = :username " +
            "AND u.password = :password"
   )
    User findByUsername(String username, String password);

    @Query(
            "SELECT u FROM User u " +
            "WHERE u.username = :username")
    boolean existsByUsername(String username);

    @Query(
            "SELECT u FROM User u " +
            "WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query(
            "SELECT u FROM User u " +
            "WHERE u.email = :email AND u.resetToken = :token")

    Optional<User> findByResetToken(String token);


@Query(
        "SELECT u " +
        "FROM User u " +
        "WHERE u.username IN :username " +
        "ORDER BY u.friendCount DESC, u.postCount DESC, u.followerCount DESC, u.followingCount DESC " +
        "LIMIT :limit"

)
    Optional<User> findByUsernames(String username);

}
