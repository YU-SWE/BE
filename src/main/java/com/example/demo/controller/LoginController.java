package com.example.demo.controller;

//import com.example.demo.Role;
//import com.example.demo.dto.LoginForm;
//import com.example.demo.service.LoginService;
//import com.example.demo.util.JwtUtil;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;

import com.example.demo.Role;
import com.example.demo.dto.LoginForm;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
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

	private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm loginForm) {

//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword())
//            );
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("user = " + 1);
        User user = userRepository.findByEmail(loginForm.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException(loginForm.getEmail()));
        System.out.println("user = " + user.getPassword());
            if (!passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
                throw new UsernameNotFoundException("비번 틀림");
            }
        System.out.println("user = " + user);
            String token = jwtUtil.generateAccessToken(user);
//            String role = authentication.getAuthorities().stream()
//                    .findFirst()
//                    .map(GrantedAuthority::getAuthority)
//                    .orElse(Role.GUEST.getRole());


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

    
//    @GetMapping("/status")
//    public ResponseEntity<?> checkStatus(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(401).body("로그인 필요");
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "email", authentication.getName(),
//                "message", "로그인된 상태입니다."
//        ));
//    }
}

