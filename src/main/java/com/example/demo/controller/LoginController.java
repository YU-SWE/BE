package com.example.demo.controller;

import com.example.demo.dto.LoginForm;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LoginController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm loginForm) {

        System.out.println("user = " + 1);
        User user = userRepository.findByEmail(loginForm.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException(loginForm.getEmail()));
        System.out.println("user = " + user.getPassword());
            if (!passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
                throw new UsernameNotFoundException("비번 틀림");
            }
        System.out.println("user = " + user);
            String token = jwtUtil.generateAccessToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("role", user.getRole());


            return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userDetails.getUsername());
        userInfo.put("isAuthenticated", true);
        
        return ResponseEntity.ok(userInfo);
    }
}

