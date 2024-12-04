package com.example.demo.config;

import com.example.demo.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider tokenProvider;

    public OAuth2SuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String token = tokenProvider.generateToken(oAuth2User);

        // CORS 헤더 설정
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(String.format("""
                <script>
                    window.opener.postMessage(
                        {
                            type: 'social-login-success',
                            token: '%s',
                            email: '%s',
                            name: '%s',
                            role: 'ROLE_USER'
                        },
                        'http://localhost:3000'
                    );
                    window.close();
                </script>
                """, token, oAuth2User.getAttribute("email"), oAuth2User.getAttribute("name")));
    }
}