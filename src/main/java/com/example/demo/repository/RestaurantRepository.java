package com.example.demo.repository;

import com.example.demo.dto.RestaurantDTO;
import com.example.demo.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    //@Query("SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.menus WHERE r.rtag = :category")
    List<Restaurant> findByRtag(@Param("rtag") String category);

//    @Query("SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.menus")
//    List<Restaurant> findAllWithMenus();

    List<RestaurantDTO> findTop10ByRtagOrderByRstarDesc(String rtag);
    @Query("SELECT DISTINCT new com.example.demo.dto.RestaurantDTO( r.rid, r.rname, r.rstar, r.image1, r.addr) FROM Restaurant r WHERE r.rtag = :rtag AND r.rloc = :rloc ORDER BY r.rstar DESC LIMIT 10")
    List<RestaurantDTO> findTop10ByRtagAndRlocOrderByRstarDesc(@Param("rtag") String rtag, @Param("rloc") String rloc);

    @Query("SELECT new com.example.demo.dto.RestaurantDTO( r.rid, r.rname, r.rstar, r.image1, r.addr) " +
            "FROM Restaurant r " +
            "WHERE r.rtag = :category AND r.rloc = :location")
    Page<RestaurantDTO> findRestaurantsByCategoryAndLocation(
            @Param("category") String category,
            @Param("location") String location,
            Pageable pageable
    );

    @Query("SELECT DISTINCT new com.example.demo.dto.RestaurantDTO(r.rid, r.rname, r.rstar, r.image1, r.addr) " +
            "FROM Restaurant r " +
            "LEFT OUTER JOIN Keyword k ON r.keyword1 = k.kid " +
            "OR r.keyword2 = k.kid " +
            "OR r.keyword3 = k.kid " +
            "OR r.keyword4 = k.kid " +
            "WHERE k.keyword LIKE %:searchTerm% " +
            "OR r.rtag LIKE %:searchTerm% " +
            "OR r.rname LIKE %:searchTerm%")
    Page<RestaurantDTO> searchByKeywordOrRname(@Param("searchTerm") String searchTerm, Pageable pageable);
}
