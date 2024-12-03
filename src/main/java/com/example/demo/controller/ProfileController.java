//package com.example.demo.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import com.example.demo.entity.User;
//import com.example.demo.repository.UserRepository;
//
//@RestController
//@RequestMapping("/api")
//public class ProfileController {
//
//    private final UserRepository userRepository;
//
//    public ProfileController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    // 프로필 정보 조회
//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
//        }
//
//        String name = authentication.getName(); // JWT에서 이메일 추출
//
//        return userRepository.findByUsername(name)
//                .<ResponseEntity<?>>map(user -> {
//                    Map<String, String> response = new HashMap<>();
//                    response.put("username", user.getUsername());
//                    response.put("email", user.getEmail());
//                    response.put("name", user.getName());
//                    return ResponseEntity.ok(response);
//                })
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
//    }
//
//    // 프로필 정보 수정
//    @PutMapping("/profile/update")
//    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody Map<String, String> updates) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
//        }
//
//        String name = authentication.getName(); // JWT에서 이메일 추출
//
//        return userRepository.findByUsername(name)
//                .<ResponseEntity<?>>map(user -> {
//                    String newUsername = updates.get("username");
//                    String newName = updates.get("name");
//
//                    user.setUsername(newUsername);
//                    user.setName(newName);
//
//                    userRepository.save(user); // 변경된 값 저장
//
//                    Map<String, String> response = new HashMap<>();
//                    response.put("message", "프로필 정보가 성공적으로 수정되었습니다.");
//                    return ResponseEntity.ok(response);
//                })
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
//    }
//}

package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.demo.dto.UpdatePasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        System.out.println("in getprofile");
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
//        }

        String email = authentication.getName(); // JWT에서 이메일 추출
        System.out.println("gaysex"+email);

        // 먼저 이메일로 검색
        Optional<User> userByEmail = userRepository.findByEmail(email);

        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            Map<String, String> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            return ResponseEntity.ok(response);
        }

        // 이메일로 찾지 못한 경우 username으로 검색
        Optional<User> userByUsername = userRepository.findByUsername(email);

        return userByUsername
                .<ResponseEntity<?>>map(user -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("name", user.getName());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.error("User not found with email or username: {}", email);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
                });
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody Map<String, String> updates) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String email = authentication.getName();

        // 먼저 이메일로 검색
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            // 이메일로 찾지 못한 경우 username으로 검색
            userOptional = userRepository.findByUsername(email);
        }

        return userOptional
                .<ResponseEntity<?>>map(user -> {
                    if (updates.containsKey("username")) {
                        user.setUsername(updates.get("username"));
                    }
                    if (updates.containsKey("name")) {
                        user.setName(updates.get("name"));
                    }

                    userRepository.save(user);

                    Map<String, String> response = new HashMap<>();
                    response.put("message", "프로필 정보가 성공적으로 수정되었습니다.");
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("name", user.getName());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    @PutMapping("/change-password")
    public void changePassword(@RequestBody UpdatePasswordForm updatePasswordForm, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = userRepository.findByUsername(email);

        if (!passwordEncoder.matches(updatePasswordForm.getOldPassword(), user.get().getPassword())) {
            throw new UsernameNotFoundException("비번 틀림");
        }
        System.out.println(updatePasswordForm.getNewPassword());
        System.out.println(email);
        userRepository.updatepw(passwordEncoder.encode(updatePasswordForm.getNewPassword()), email);

    }
}
