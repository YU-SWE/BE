package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository; // User 레포지토리 추가
    private final PasswordEncoder passwordEncoder;

    @Value("${mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Transactional
    public void sendPasswordResetEmail(String email) {
        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));

        // 2. 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();

        // 3. 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        userRepository.flush();

        // 4. 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("임시 비밀번호 발급");
        message.setText("임시 비밀번호: " + temporaryPassword +
                "\n로그인 후 반드시 비밀번호를 변경해주세요.");

        javaMailSender.send(message);
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }


    public void sendVerificationEmail(String email) {
        String verificationCode = generateVerificationCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);

        redisTemplate.opsForValue()
                .set(email, verificationCode,
                        authCodeExpirationMillis, TimeUnit.MILLISECONDS);

        javaMailSender.send(message);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return storedCode != null && storedCode.equals(code);
    }
}