package com.example.demo.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class CustomClientRegistrationRepo {
    // 생성자 주입 방식
    private final SocialClientRegistration socialClientRegistration;

    public CustomClientRegistrationRepo(SocialClientRegistration socialClientRegistration){
        this.socialClientRegistration = socialClientRegistration;
    }

    public ClientRegistrationRepository clientRegistrationRepository(){
        // 인메모리 방식과 JDBC(DB에 저장)방식 두 가지가 있는데
        // 네이버, 카카오, 구글 등 10가지 이내이므로 인메모리 방식으로 저장해도 무방
        return new InMemoryClientRegistrationRepository(socialClientRegistration.naverClientRegistration(), socialClientRegistration.googleClientRegistration(), socialClientRegistration.kakaoClientRegistration(), socialClientRegistration.facebookClientRegistration());
    }
}