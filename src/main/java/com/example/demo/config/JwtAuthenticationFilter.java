////package com.example.demo.config;
////
////import java.io.IOException;
////import java.util.ArrayList;
////
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
////import org.springframework.stereotype.Component;
////import org.springframework.web.filter.OncePerRequestFilter;
////
////import com.example.demo.util.JwtUtil;
////
////import jakarta.servlet.FilterChain;
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
////
////@Component
////public class JwtAuthenticationFilter extends OncePerRequestFilter {
////
////    private final JwtUtil jwtUtil;
////
////    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
////        this.jwtUtil = jwtUtil;
////    }
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
////            throws ServletException, IOException {
////        String authHeader = request.getHeader("Authorization");
////        if (authHeader != null && authHeader.startsWith("Bearer ")) {
////            String token = authHeader.substring(7);
////            try {
////                String username = jwtUtil.validateTokenAndGetUsername(token);
////                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
////                            username, null, new ArrayList<>());
////                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                    SecurityContextHolder.getContext().setAuthentication(authentication);
////                }
////            } catch (Exception e) {
////                SecurityContextHolder.clearContext();
////                logger.error("JWT 인증 실패: " + e.getMessage());
////            }
////        }
////        filterChain.doFilter(request, response);
////    }
////}
////
//
//
//package com.example.demo.config;
//
//import java.io.IOException;
//import java.util.Collections;
//
//import com.example.demo.security.JwtTokenProvider;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            String jwt = getJwtFromRequest(request);
//
//            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
//                String username = jwtTokenProvider.getUsernameFromJWT(jwt);
//                String role = jwtTokenProvider.getRoleFromJWT(jwt);
//
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        username,
//                        null,
//                        Collections.singletonList(new SimpleGrantedAuthority(role))
//                );
//
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (Exception ex) {
//            logger.error("Could not set user authentication in security context", ex);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}

package com.example.demo.config;

import com.example.demo.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("request = " + request);
        try {
            String jwt = getJwtFromRequest(request);
            System.out.println("jwt = " + jwt);

            if (jwt == null || jwt == "null") {
                filterChain.doFilter(request, response);
            }

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                //String name = jwtTokenProvider.getUsernameFromJWT(jwt);
                String email = jwtTokenProvider.getEmailFromJWT(jwt);
                System.out.println("email = " + email);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email); // username
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Debug log
                logger.debug("Set Authentication to security context for '{}', uri: {}", email, request.getRequestURI());
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}