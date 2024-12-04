package com.example.demo.service;

import com.example.demo.dto.NoticeRequest;
import com.example.demo.entity.Notice;
import com.example.demo.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public Notice createNotice(NoticeRequest request) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        String username = authentication.getName();


        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(username)  // 현재 로그인한 사용자의 username 사용
                .build();

        return noticeRepository.save(notice);
    }
    @Transactional(readOnly = true)
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("Notice not found with id: " + noticeId));
    }

    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    @Transactional
    public Notice updateNotice(Long noticeId, NoticeRequest request)
            throws EntityNotFoundException, AccessDeniedException {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!notice.getAuthor().equals(currentUsername)) {
            throw new AccessDeniedException("해당 공지사항의 수정 권한이 없습니다.");
        }

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());

        return noticeRepository.save(notice);
    }
}