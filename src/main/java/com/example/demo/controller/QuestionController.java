package com.example.demo.controller;

import com.example.demo.dto.NoticeRequest;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.entity.Notice;
import com.example.demo.entity.Question;
import com.example.demo.service.QuestionService;
import com.example.demo.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private JwtUtil jwtUtil;

    // 질문 작성
    @PostMapping
    public Question createQuestion(@RequestBody QuestionDTO questionRequest) {
        return questionService.createQuestion(
                questionRequest.getTitle(),
                questionRequest.getContent(),
                questionRequest.getUsername()
        );
    }

    @PreAuthorize("permitAll()")
    // 모든 질문 조회
    @GetMapping
    public List<QuestionDTO> getAllQuestions() {
        return questionService.getAllQuestions();
    }


    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        Optional<QuestionDTO> questionDTO = questionService.getQuestionById(id);
        return questionDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패");
        }
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionRequest request) {
        try {
            Question updatedQuestion = questionService.updateQuestion(questionId, request);
            return ResponseEntity.ok(updatedQuestion);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("질문을 찾을 수 없습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("수정 권한이 없습니다.");
        }
    }
}
