package com.example.demo.controller;

import com.example.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class MailController {
    private final EmailService emailService;

    @PostMapping("/verify/send")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam String email) {
        emailService.sendVerificationEmail(email);
        return ResponseEntity.ok("인증 메일이 발송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {
        if (emailService.verifyCode(email, code)) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        }
        return ResponseEntity.badRequest().body("잘못된 인증 코드입니다.");
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        try {
            emailService.sendPasswordResetEmail(email);
            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("해당 이메일의 사용자를 찾을 수 없습니다.");
        }
    }
}