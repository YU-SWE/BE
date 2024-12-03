package com.example.demo.controller;

import com.example.demo.dto.RestaurantDTO;
import com.example.demo.entity.Restaurant;
import com.example.demo.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yufood")
@CrossOrigin(origins = "http://localhost:3000")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

//    @GetMapping("/category/{category}")
//    public ResponseEntity<List<Restaurant>> getRestaurantsByCategory(
//            @PathVariable("category") String category) {
//        String decodedCategory = URLDecoder.decode(category, StandardCharsets.UTF_8);
//        List<Restaurant> restaurants = restaurantService.getRestaurantsByCategory(decodedCategory);
//        return new ResponseEntity<>(restaurants, HttpStatus.OK);
//    }

    @ResponseBody
    @GetMapping(value ="/category/{rtag}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RestaurantDTO> getTop10RestaurantsByCategory(@PathVariable("rtag") String rtag) {
        return restaurantService.getTop10RestaurantsByCategory(rtag);
    }

    @ResponseBody
    @GetMapping(value = "/category/{rtag}/location/{rloc}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByTagAndLocation(
            @PathVariable("rtag") String rtag,
            @PathVariable("rloc") String rloc) {
        
        List<RestaurantDTO> restaurants = restaurantService.findTop10ByTagAndLocation(rtag, rloc);
        //System.out.println("1111111111111111111111111111111" + restaurants);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(
            @PathVariable(value = "id", required = false) Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        
        System.out.println("상세 정보 API 호출됨: " + id);
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        
        if (restaurant != null) {
            return ResponseEntity.ok(restaurant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/paged/category/{category}/location/{location}")
    public ResponseEntity<?> getRestaurants(
            @PathVariable("category") String category,
            @PathVariable("location") String location,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<RestaurantDTO> restaurants = restaurantService.getRestaurantsByCategoryAndLocation(category, location, page, size);
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRestaurants(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "rating") String sortBy) {
        try {
            Page<RestaurantDTO> restaurants = restaurantService.searchRestaurants(query, page, size, sortBy);
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}
