////package com.example.demo.service;
////
////import com.example.demo.Role;
////import com.example.demo.entity.User;
////import com.example.demo.repository.UserRepository;
////import com.example.demo.security.CustomOAuth2User;
////import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
////import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
////import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
////import org.springframework.security.oauth2.core.user.OAuth2User;
////import org.springframework.stereotype.Service;
////
////@Service
////public class CustomOAuth2UserService extends DefaultOAuth2UserService {
////
////    private final UserRepository userRepository;
////
////    public CustomOAuth2UserService(UserRepository userRepository) {
////        this.userRepository = userRepository;
////    }
////
////    @Override
////    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
////        OAuth2User oAuth2User = super.loadUser(userRequest);
////
////        // 제공자(provider) 정보 가져오기
////        String provider = userRequest.getClientRegistration().getRegistrationId();
////
////        // 사용자 정보 추출
////        String email = "";
////        String name = "";
////        String providerId = "";
////
////        if (provider.equals("google")) {
////            email = oAuth2User.getAttribute("email");
////            name = oAuth2User.getAttribute("name");
////            providerId = oAuth2User.getAttribute("sub");
////        }
////        // 다른 provider들에 대한 처리도 추가 가능
////
////        // username은 email에서 @ 앞부분 추출
////        String username = email.substring(0, email.indexOf("@"));
////
////        // DB에서 해당 email로 사용자 찾기
////        User user = userRepository.findByEmail(email).orElse(null);
////
////        if (user == null) {
////            // 새 사용자 생성
////            user = new User();
////            user.setEmail(email);
////            user.setUsername(username);
////            user.setName(name);
////            user.setProvider(provider);
////            user.setProvider_id(provider + "_" + providerId);  // provider_id 필드명에 맞춤
////            user.setRole(Role.USER); // 기본 역할 설정
////            userRepository.save(user);
////        } else {
////            // 기존 사용자 정보 업데이트
////            user.setName(name);
////            user.setProvider(provider);
////            user.setProvider_id(provider + "_" + providerId);  // provider_id 필드명에 맞춤
////            userRepository.save(user);
////        }
////
////        return new CustomOAuth2User(oAuth2User.getAttributes(), user.getRole());
////    }
////}
//
//package com.example.demo.service;
//
//import com.example.demo.Role;
//import com.example.demo.entity.User;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.security.CustomOAuth2User;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
//    private final UserRepository userRepository;
//
//    public CustomOAuth2UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//
//        String email;
//        String name;
//        String providerId;
//
//        // 제공자별 데이터 매핑
//        if (provider.equals("kakao")) {
//            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
//            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//
//            email = (String) kakaoAccount.get("email");
//            name = (String) profile.get("nickname");
//            providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
//
//            logger.debug("Kakao user data - email: {}, name: {}", email, name);
//        } else if (provider.equals("google")) {
//            email = oAuth2User.getAttribute("email");
//            name = oAuth2User.getAttribute("name");
//            providerId = oAuth2User.getAttribute("sub");
//        } else {
//            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
//        }
//
//        User user = userRepository.findByEmail(email)
//                .orElseGet(() -> {
//                    // 새 사용자 생성
//                    User newUser = new User();
//                    newUser.setEmail(email);
//                    newUser.setName(name);
//                    newUser.setUsername(email.substring(0, email.indexOf("@")));
//                    newUser.setProvider(provider);
//                    newUser.setProvider_id(provider + "_" + providerId);
//                    newUser.setRole(Role.USER);
//                    return userRepository.save(newUser);
//                });
//
//        return new CustomOAuth2User(oAuth2User.getAttributes(), user.getRole());
//    }
//}


package com.example.demo.service;

import com.example.demo.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomOAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email;
        String name;
        String providerId;

        try {
            switch (provider) {
                case "naver":
                    Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                    email = (String) response.get("email");
                    name = (String) response.get("name");
                    providerId = (String) response.get("id");
                    logger.debug("Naver user data - email: {}, name: {}", email, name);
                    break;

                case "kakao":
                    Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    email = (String) kakaoAccount.get("email");
                    name = (String) profile.get("nickname");
                    providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
                    logger.debug("Kakao user data - email: {}, name: {}", email, name);
                    break;

                case "google":
                    email = oAuth2User.getAttribute("email");
                    name = oAuth2User.getAttribute("name");
                    providerId = oAuth2User.getAttribute("sub");
                    logger.debug("Google user data - email: {}, name: {}", email, name);
                    break;

                default:
                    throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
            }

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setName(name);
                        newUser.setUsername(email.substring(0, email.indexOf("@")));
                        newUser.setProvider(provider);
                        newUser.setProvider_id(provider + "_" + providerId);
                        newUser.setRole(Role.USER);
                        return userRepository.save(newUser);
                    });

            return new CustomOAuth2User(oAuth2User.getAttributes(), user.getRole());

        } catch (Exception e) {
            logger.error("Error processing OAuth2 user: ", e);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user");
        }
    }
}