package com.example.awesomepizza.repository;

import com.example.awesomepizza.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
//    String query = "" +
//        "SELECT CASE WHEN NOT COUNT(u) > 0 THEN " +
//        "TRUE ELSE FALSE END " +
//        "FROM User u " +
//        "WHERE u.email = ?1";
//    @Query(query)
//    Boolean selectEmailExists(String email);
//
//    User getUserByEmail(String email);

    User findByEmail(String email);
}
