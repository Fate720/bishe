package com.library.service;

import com.library.entity.Notice;
import com.library.exception.BusinessException;
import com.library.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoticeService {
    
    private final NoticeRepository noticeRepository;
    
    public Page<Notice> listNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }
    
    public Notice getById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("通知不存在"));
    }
    
    @Transactional
    public Notice create(Notice notice) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        notice.setAuthor(username);
        notice.setStatus(0); // 草稿
        return noticeRepository.save(notice);
    }
    
    @Transactional
    public Notice update(Long id, Notice notice) {
        Notice existing = getById(id);
        if (notice.getTitle() != null) {
            existing.setTitle(notice.getTitle());
        }
        if (notice.getContent() != null) {
            existing.setContent(notice.getContent());
        }
        return noticeRepository.save(existing);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new BusinessException("通知不存在");
        }
        noticeRepository.deleteById(id);
    }
    
    @Transactional
    public Notice publish(Long id) {
        Notice notice = getById(id);
        notice.setStatus(1); // 已发布
        notice.setPublishTime(LocalDateTime.now());
        return noticeRepository.save(notice);
    }
}