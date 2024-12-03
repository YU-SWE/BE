package com.example.demo.service;

import com.example.demo.dto.NoticeRequest;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.entity.Notice;
import com.example.demo.entity.Question;
import com.example.demo.entity.User;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Question createQuestion(String title, String content, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setUser(user);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        question.setViews(0); // views 초기값 설정

        return questionRepository.save(question);
    }

    public List<QuestionDTO> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();

        return questions.stream()
                .map(question -> new QuestionDTO(
                        question.getId(),
                        question.getTitle(),
                        question.getContent(),
                        question.getUser().getUsername(),
                        question.getCreatedAt(),
                        question.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    // 특정 질문 조회
    public Optional<QuestionDTO> getQuestionById(Long id) {
        return questionRepository.findById(id)
                .map(question -> new QuestionDTO(
                        question.getId(),
                        question.getTitle(),
                        question.getContent(),
                        question.getUser() != null ? question.getUser().getUsername() : "알 수 없음",
                        question.getCreatedAt(),
                        question.getUpdatedAt()
                ));
    }

    public boolean canModifyOrDeleteQuestion(Long questionId, String username, boolean isAdmin) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));

        return isAdmin || question.getUser().getUsername().equals(username);
    }


    @Transactional
    public void deleteQuestion(Long questionId) {
        // 1. 답글 삭제
        answerRepository.deleteByQuestionId(questionId);

        // 2. 질문 삭제
        questionRepository.deleteById(questionId);
    }

//    @Transactional
//    public Question updateQuestion(Long questionId, QuestionRequest request)
//            throws EntityNotFoundException, AccessDeniedException {
//
//        Question question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//
    ////        if (!question.getAuthor().equals(currentUsername)) {
    ////            throw new AccessDeniedException("해당 공지사항의 수정 권한이 없습니다.");
    ////        }
//        if (!question.getUser().getUsername().equals(currentUsername)) {
//            throw new AccessDeniedException("해당 질문의 수정 권한이 없습니다.");
//        }
//        if (question.getUser().getRole().equals("ADMIN")) {
//            throw new AccessDeniedException("ADMIN");
//        }
//
//        question.setTitle(request.getTitle());
//        question.setContent(request.getContent());
//        question.setUpdatedAt(LocalDateTime.now());
//
//        return questionRepository.save(question);
//    }

    @Transactional
    public Question updateQuestion(Long questionId, QuestionRequest request)
            throws EntityNotFoundException, AccessDeniedException {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !question.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setUpdatedAt(LocalDateTime.now());

        return questionRepository.save(question);
    }

}
