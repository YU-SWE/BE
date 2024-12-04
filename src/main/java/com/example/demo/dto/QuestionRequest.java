package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class QuestionRequest {
    private String title;
    private String content;
    private String user;
    private LocalDateTime updatedAt;
}
