package com.library.service;

import com.library.entity.Book;
import com.library.exception.BusinessException;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    
    /**
     * 分页查询图书
     */
    public Page<Book> listBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    /**
     * 多条件分页搜索图书
     */
    public Page<Book> searchBooks(String title, String author, String isbn, String publisher, String category, Pageable pageable) {
        return bookRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title.trim() + "%"));
            }

            if (author != null && !author.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("author"), "%" + author.trim() + "%"));
            }

            if (isbn != null && !isbn.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("isbn"), "%" + isbn.trim() + "%"));
            }

            if (publisher != null && !publisher.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("publisher"), "%" + publisher.trim() + "%"));
            }

            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category.trim()));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }
    

    
    /**
     * 根据关键字搜索图书支持按书名、作者、ISBN、ID搜索
     */
    public Page<Book> searchByKeyword(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }
        String pattern = "%" + keyword.trim() + "%";
        return bookRepository.findAll((root, query, cb) -> {
            jakarta.persistence.criteria.Predicate titleLike = cb.like(root.get("title"), pattern);
            jakarta.persistence.criteria.Predicate authorLike = cb.like(root.get("author"), pattern);
            jakarta.persistence.criteria.Predicate isbnLike = cb.like(root.get("isbn"), pattern);
            jakarta.persistence.criteria.Predicate result = cb.or(titleLike, authorLike, isbnLike);
            try {
                Long id = Long.parseLong(keyword.trim());
                result = cb.or(result, cb.equal(root.get("id"), id));
            } catch (NumberFormatException ignored) {}
            return result;
        }, pageable);
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("图书不存在"));
    }
    
    @Transactional
    public Book create(Book book) {
        // 验证ISBN唯一性
        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
                throw new BusinessException("ISBN已存在");
            }
        }
        return bookRepository.save(book);
    }
    
    @Transactional
    public Book update(Long id, Book book) {
        Book existing = getById(id);
        // 逐字段判空更新，避免 null 覆盖已有数据
        if (book.getTitle() != null) existing.setTitle(book.getTitle());
        if (book.getAuthor() != null) existing.setAuthor(book.getAuthor());
        if (book.getIsbn() != null) existing.setIsbn(book.getIsbn());
        if (book.getPublisher() != null) existing.setPublisher(book.getPublisher());
        if (book.getPublishDate() != null) existing.setPublishDate(book.getPublishDate());
        if (book.getPrice() != null) existing.setPrice(book.getPrice());
        if (book.getStock() != null) existing.setStock(book.getStock());
        if (book.getCategory() != null) existing.setCategory(book.getCategory());
        if (book.getDescription() != null) existing.setDescription(book.getDescription());
        if (book.getLocation() != null) existing.setLocation(book.getLocation());
        return bookRepository.save(existing);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BusinessException("图书不存在");
        }
        bookRepository.deleteById(id);
    }
}