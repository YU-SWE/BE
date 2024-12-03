package com.example.demo.controller;

import com.example.demo.dto.NoticeRequest;
import com.example.demo.dto.NoticeResponse;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Notice;
import com.example.demo.repository.NoticeRepository;
import com.example.demo.service.NoticeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NoticeController {
    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponse>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        List<NoticeResponse> responses = notices.stream()
                .map(notice -> NoticeResponse.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .author(notice.getAuthor())
                        .createdAt(notice.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 특정 공지사항 조회
    @GetMapping("/notice/{id}")  // /api/notice/{id}로 접근
    public ResponseEntity<?> getNoticeById(@PathVariable("id") Long id) {
        return noticeRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다."));
    }

    // 공지사항 생성
    @PostMapping("/notice")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Notice> createNotice(@RequestBody NoticeRequest request) throws AccessDeniedException {
        Notice notice = noticeService.createNotice(request);
        return ResponseEntity.ok(notice);
    }

    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<?> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            noticeService.deleteNotice(noticeId);
            return ResponseEntity.ok(noticeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패");
        }
    }

    @PutMapping("/notice/{noticeId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeRequest request) {
        try {
            Notice updatedNotice = noticeService.updateNotice(noticeId, request);
            return ResponseEntity.ok(updatedNotice);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("공지사항을 찾을 수 없습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("수정 권한이 없습니다.");
        }
    }
}