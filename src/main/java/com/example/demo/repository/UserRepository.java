package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.password = :newpw WHERE u.username = :name")
    void updatepw(@Param("newpw") String newpw, @Param("name") String name);
}


