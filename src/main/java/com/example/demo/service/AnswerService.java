package com.example.demo.service;

import com.example.demo.dto.AnswerDTO;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.entity.User;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public AnswerDTO createAnswer(Long questionId, String content, String username) {
        // 질문과 사용자 검증
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + questionId));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username: " + username));

        // 답변 생성 및 저장
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setContent(content);
        answer.setUser(user);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());

        Answer savedAnswer = answerRepository.save(answer);

        // 저장된 답변을 DTO로 변환하여 반환
        return new AnswerDTO(savedAnswer.getId(), savedAnswer.getContent(), username, savedAnswer.getCreatedAt(), savedAnswer.getUpdatedAt());
    }

    public List<AnswerDTO> getAnswersByQuestionId(Long questionId) {
        // questionId로 관련된 모든 답변 조회
        List<Answer> answers = answerRepository.findByQuestionId(questionId);

        // 엔티티를 DTO로 변환
        return answers.stream()
                .map(answer -> new AnswerDTO(
                        answer.getId(),
                        answer.getContent(),
                        answer.getUser().getUsername(),
                        answer.getCreatedAt(),
                        answer.getUpdatedAt()))
                .toList();
    }

    public boolean canModifyOrDelete(Long answerId, String username, boolean isAdmin) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));
        return isAdmin || answer.getUser().getUsername().equals(username);
    }

    public void deleteAnswer(Long answerId) {
        answerRepository.deleteById(answerId);
    }

    public Answer findById(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));
    }
}
