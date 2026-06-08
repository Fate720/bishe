package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("bookCount", bookRepository.count());
        stats.put("userCount", userRepository.count());
        stats.put("borrowCount", borrowRecordRepository.count());
        stats.put("activeBorrowCount", borrowRecordRepository.countByStatus(0));
        
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // 获取最近5条借阅记录
        Pageable topFive = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdTime"));
        List<BorrowRecord> recentRecords;
        
        if (isAdmin) {
            // 管理员：获取所有用户的最近借阅记录
            recentRecords = borrowRecordRepository.findAll(topFive).getContent();
        } else {
            // 普通用户：只获取自己的最近借阅记录
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("用户不存在"));
            recentRecords = borrowRecordRepository.findByUser(user, topFive).getContent();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> recentBorrows = recentRecords.stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookTitle", record.getBook().getTitle());
                    map.put("username", record.getUser().getUsername());
                    map.put("borrowDate", record.getBorrowDate().format(formatter));
                    return map;
                })
                .collect(Collectors.toList());
        
        stats.put("recentBorrows", recentBorrows);
        return stats;
    }
}
