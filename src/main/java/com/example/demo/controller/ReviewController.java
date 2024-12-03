package com.example.demo.controller;

import com.example.demo.ResourceNotFoundException;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ReviewService;
import com.example.demo.service.StorageService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/review_info")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestParam("reviewstar") Integer reviewstar,
            @RequestParam("content") String content,
            @RequestParam("rid") Integer rid,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) {

        String email = authentication.getName(); // JWT에서 이메일 추출
        System.out.println(email);
        Review review = new Review();

        // 이메일로 사용자 찾기
        Optional<User> userByEmail = userRepository.findByUsername(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            review.setReviewstar(reviewstar);
            review.setContent(content);
            review.setRid(rid);
            review.setUser(user);
        } else {
            // 이메일로 사용자를 찾지 못한 경우 예외 처리
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 이미지가 있을 경우 저장
        if (image != null && !image.isEmpty()) {
            String filename = storageService.store(image);
            String imageUrl = "/uploads/" + filename;
            review.setImageUrl(imageUrl);
        }

        // 리뷰 저장
        Review savedReview = reviewService.saveReview(review, userByEmail.get().getUsername());

        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }


    // 특정 식당의 모든 리뷰 조회
    @GetMapping("/restaurant/{rid}")
    public ResponseEntity<List<ReviewDTO>> 
    getReviewsByRestaurant(@PathVariable("rid") Integer rid) {
        try {
            List<ReviewDTO> reviews = 
            		reviewService.getReviewsByRestaurantId(rid);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 리뷰 수정
    @PutMapping("/{reviewid}")
    public ResponseEntity<Review> updateReview(@PathVariable("reviewid") Integer reviewid, 
                                             @RequestBody Review review) {
        try {
            Review updatedReview = reviewService.updateReview(reviewid, review);
            return ResponseEntity.ok(updatedReview);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewid}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewid") Integer reviewid) {
        try {
            reviewService.deleteReview(reviewid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/updatestar")
    public void updateReviewStar(@RequestParam("star") double star, @RequestParam("rid") int rid) {
        reviewService.updatestar(star, rid);
    }
}