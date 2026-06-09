package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final int MAX_BORROW_COUNT = 5;

    /**
     * 借阅图书
     */
    @Transactional
    public BorrowRecord borrowBook(Long bookId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("图书不存在"));

        if (book.getStock() == null || book.getStock() <= 0) {
            throw new BusinessException("图书库存不足");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账户已被禁用，无法借阅");
        }

        long currentBorrowCount = borrowRecordRepository.countCurrentBorrowsByUserId(user.getId());
        if (currentBorrowCount >= MAX_BORROW_COUNT) {
            throw new BusinessException("借阅数量已达上限(" + MAX_BORROW_COUNT + "本)，请先归还部分图书");
        }

        List<BorrowRecord> activeBorrows = borrowRecordRepository.findByUserIdAndStatus(user.getId(), 0);
        if (activeBorrows.stream().anyMatch(r -> r.getBook().getId().equals(bookId))) {
            throw new BusinessException("您已借阅该书且未归还");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(30));
        record.setStatus(0);
        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }
    
    /**
     * 归还图书
     */
    @Transactional
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BusinessException("借阅记录不存在"));
        
        if (record.getStatus() != 0 && record.getStatus() != 2) {
            throw new BusinessException("该图书已归还");
        }
        
        record.setReturnDate(LocalDate.now());
        record.setStatus(1);
        
        Book book = record.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);
        
        return borrowRecordRepository.save(record);
    }
    
    /**
     * 查询我的借阅记录（使用 JOIN FETCH 避免 N+1）
     */
    @Transactional(readOnly = true)
    public Page<BorrowRecord> getMyBorrowRecords(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        markOverdueRecords(user.getId());
        
        return borrowRecordRepository.findByUserIdWithDetails(user.getId(), pageable);
    }

    /**
     * 查询所有借阅记录-管理员（使用 JOIN FETCH 避免 N+1）
     */
    @Transactional(readOnly = true)
    public Page<BorrowRecord> getAllBorrowRecords(Pageable pageable, Integer status) {
        markOverdueRecords(null);
        
        if (status != null) {
            return borrowRecordRepository.findByStatus(status, pageable);
        }
        return borrowRecordRepository.findAllWithDetails(pageable);
    }
    
    /**
     * 标记逾期记录（按需更新）
     */
    private void markOverdueRecords(Long userId) {
        List<BorrowRecord> overdueRecords;
        if (userId != null) {
            overdueRecords = borrowRecordRepository.findOverdueRecordsByUserId(userId, LocalDate.now());
        } else {
            overdueRecords = borrowRecordRepository.findOverdueRecords(LocalDate.now());
        }
        if (!overdueRecords.isEmpty()) {
            for (BorrowRecord record : overdueRecords) {
                record.setStatus(2);
            }
            borrowRecordRepository.saveAll(overdueRecords);
        }
    }
}