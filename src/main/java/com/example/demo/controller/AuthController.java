//package com.example.demo.controller;
//
//import java.util.UUID;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import demo.app.service.MemberService;
//import bitcamp.app.vo.Member;
//import bitcamp.util.ErrorCode;
//import bitcamp.util.PasswordChecker;
//import bitcamp.util.RestResult;
//import bitcamp.util.RestStatus;
//import jakarta.servlet.http.HttpSession;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    Logger log = LogManager.getLogger(getClass());
//
//    {
//        log.trace("AuthController 생성됨!");
//    }
//
//    @Autowired private MemberService memberService;
//
//    @PostMapping("signup")
//    public Object signup(
//            String nickname,
//            String email,
//            String password,
//            HttpSession session) throws Exception {
//
//        if(nickname.length() <= 50 ||
//                email.contains("@") ||
//                PasswordChecker.isValidPassword(password)) {
//
//            String token = UUID.randomUUID().toString();
//
//            Member member = new Member();
//            member.setNickname(nickname);
//            member.setEmail(email);
//            member.setPassword(password);
//            member.setToken(token);
//
//            memberService.add(member);
//
//            return new RestResult()
//                    .setStatus(RestStatus.SUCCESS);
//        }
//
//        return new RestResult()
//                .setErrorCode(ErrorCode.rest.CONTROLLER_EXCEPTION)
//                .setStatus(RestStatus.FAILURE);
//    }
//
//    @GetMapping("verify")
//    public Object verifyEmail(HttpSession session, @RequestParam String token) {
//        Member member = memberService.updateByVerifyToken(token);
//
//        if (member != null) {
//            session.setAttribute("loginUser", member);
//
//            return new RestResult()
//                    .setStatus(RestStatus.SUCCESS);
//        } else {
//            return new RestResult()
//                    .setErrorCode(ErrorCode.rest.NO_DATA)
//                    .setStatus(RestStatus.FAILURE);
//        }
//    }
//
//}