package com.example.demo.controller;

import com.example.demo.dto.AnswerDTO;
import com.example.demo.dto.AnswerRequest;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.entity.Answer;
import com.example.demo.service.AnswerService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping
    public ResponseEntity<AnswerDTO> createAnswer(
            @RequestBody AnswerRequest answerRequest,
            @RequestHeader("Authorization") String token) {

        // JWT에서 username 추출
        String username = jwtUtil.validateTokenAndGetUsername(token.substring(7));
        AnswerDTO answer = answerService.createAnswer(answerRequest.getQuestionId(), answerRequest.getContent(), username);

        return ResponseEntity.ok(answer);
    }



    @GetMapping("/{questionId}")
    public List<AnswerDTO> getAnswersByQuestionId(@PathVariable Long questionId) {
        return answerService.getAnswersByQuestionId(questionId);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Answer answer = answerService.findById(answerId);

            // 현재 로그인한 사용자가 답변 작성자이거나 관리자인지 확인
            if (answer.getUser().getUsername().equals(userDetails.getUsername()) ||
                    userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                answerService.deleteAnswer(answerId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 삭제 실패");
        }
    }
}
