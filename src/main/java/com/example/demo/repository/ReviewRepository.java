package com.example.demo.repository;

import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    //List<Review> findByRestaurantRid(Integer rid);
    
    @Query("SELECT r, u.username FROM Review r LEFT JOIN User u ON r.userid = u.id WHERE r.restaurant.rid = :rid")
    List<Object[]> findByRestaurantRidWithUsername(@Param("rid") Integer rid);

    @Modifying
    @Query("UPDATE Restaurant r SET r.rstar = :star WHERE r.rid = :rid")
    void updatestar(@Param("star") double star, @Param("rid") int rid);
}