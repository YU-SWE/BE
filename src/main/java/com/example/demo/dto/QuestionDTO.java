package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private String username; // 작성자 이름
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
