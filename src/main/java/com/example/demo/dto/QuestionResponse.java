package com.example.demo.dto;

public class QuestionResponse {
    private String title;
    private String content;

    // Default Constructor
    public QuestionResponse() {}

    // Getter and Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
