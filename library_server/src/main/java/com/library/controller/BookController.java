package com.library.controller;

import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "图书管理", description = "图书增删改查接口")
public class BookController {
    
    private final BookService bookService;
    
    @GetMapping
    @Operation(summary = "分页查询图书", description = "获取所有图书列表，支持分页")
    public Result<Page<Book>> list(@PageableDefault(size = 10) Pageable pageable) {
        return Result.success(bookService.listBooks(pageable));
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索图书支持关键字和多条件搜索", description = "根据书名、作者、ISBN、出版社、分类进行模糊搜索，支持分页")
    public Result<Page<Book>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return Result.success(bookService.searchByKeyword(keyword, pageable));
        }
        return Result.success(bookService.searchBooks(title, author, isbn, publisher, category, pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取图书详情", description = "根据ID获取图书详细信息")
    public Result<Book> get(@PathVariable Long id) {
        return Result.success(bookService.getById(id));
    }
    
    @PostMapping
    @Operation(summary = "新增图书", description = "添加新图书到系统")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Book> create(@Validated @RequestBody Book book) {
        return Result.success(bookService.create(book));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "修改图书", description = "更新图书信息")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Book> update(@PathVariable Long id, @Validated @RequestBody Book book) {
        return Result.success(bookService.update(id, book));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除图书", description = "从系统中删除图书")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success(null);
    }
}
