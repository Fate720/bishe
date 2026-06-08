package com.library.dto;

import com.library.entity.BorrowRecord;
import lombok.Data;

import java.time.LocalDate;

/**
 * 借阅记录 DTO
 * 避免 Hibernate 懒加载代理对象的序列化问题
 */
@Data
public class BorrowRecordDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private Integer status;

    public static BorrowRecordDTO fromEntity(BorrowRecord record) {
        BorrowRecordDTO dto = new BorrowRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUser().getId());
        dto.setUsername(record.getUser().getUsername());
        dto.setBookId(record.getBook().getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setBookAuthor(record.getBook().getAuthor());
        dto.setBookIsbn(record.getBook().getIsbn());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setDueDate(record.getDueDate());
        dto.setStatus(record.getStatus());
        return dto;
    }
}
