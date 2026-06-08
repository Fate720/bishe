package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    
    /**
     * 根据ISBN查询图书
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * 根据书名模糊查询
     */
    List<Book> findByTitleContaining(String title);
    
    /**
     * 根据作者模糊查询
     */
    List<Book> findByAuthorContaining(String author);
}