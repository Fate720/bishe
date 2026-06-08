package com.library.controller;

import com.library.common.Result;
import com.library.dto.BorrowRecordDTO;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
@Tag(name = "借阅管理", description = "图书借阅相关接口")
public class BorrowController {
    
    private final BorrowService borrowService;
    
    @PostMapping("/{bookId}")
    @Operation(summary = "借阅图书", description = "用户借阅指定图书")
    public Result<BorrowRecordDTO> borrowBook(@PathVariable Long bookId) {
        BorrowRecord record = borrowService.borrowBook(bookId);
        return Result.success(BorrowRecordDTO.fromEntity(record));
    }
    
    @PutMapping("/return/{recordId}")
    @Operation(summary = "归还图书", description = "用户归还已借阅的图书")
    public Result<BorrowRecordDTO> returnBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.returnBook(recordId);
        return Result.success(BorrowRecordDTO.fromEntity(record));
    }
    
    @GetMapping("/my")
    @Operation(summary = "我的借阅记录", description = "查询当前用户的借阅历史")
    public Result<Page<BorrowRecordDTO>> getMyRecords(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BorrowRecord> records = borrowService.getMyBorrowRecords(pageable);
        return Result.success(records.map(BorrowRecordDTO::fromEntity));
    }
    
    @GetMapping
    @Operation(summary = "所有借阅记录（管理员）", description = "管理员查看所有用户的借阅记录")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<BorrowRecordDTO>> getAllRecords(
            @RequestParam(required = false) Integer status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BorrowRecord> records = borrowService.getAllBorrowRecords(pageable, status);
        return Result.success(records.map(BorrowRecordDTO::fromEntity));
    }
}
