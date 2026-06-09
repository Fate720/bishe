package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Page<BorrowRecord> findByUser(User user, Pageable pageable);

    List<BorrowRecord> findByUserIdAndStatus(Long userId, Integer status);

    long countByStatus(Integer status);

    /** JOIN FETCH 查询用户的借阅记录（含图书和用户信息），避免 N+1 查询 */
    @Query("SELECT br FROM BorrowRecord br JOIN FETCH br.book JOIN FETCH br.user WHERE br.user.id = :userId")
    Page<BorrowRecord> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    /** JOIN FETCH 查询所有借阅记录（含图书和用户信息），避免 N+1 查询 */
    @Query(value = "SELECT br FROM BorrowRecord br JOIN FETCH br.book JOIN FETCH br.user",
           countQuery = "SELECT COUNT(br) FROM BorrowRecord br")
    Page<BorrowRecord> findAllWithDetails(Pageable pageable);

    /** 统计用户当前在借数量*/
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 0")
    long countCurrentBorrowsByUserId(@Param("userId") Long userId);

    /** JOIN FETCH 查询指定状态的借阅记录 */
    @Query(value = "SELECT br FROM BorrowRecord br JOIN FETCH br.book JOIN FETCH br.user WHERE br.status = :status",
           countQuery = "SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = :status")
    Page<BorrowRecord> findByStatus(@Param("status") Integer status, Pageable pageable);

    @Query("SELECT br FROM BorrowRecord br JOIN FETCH br.book JOIN FETCH br.user WHERE br.status = 0 AND br.dueDate < :now")
    List<BorrowRecord> findOverdueRecords(@Param("now") LocalDate now);

    /** 查询指定用户的逾期记录（按需更新）*/
    @Query("SELECT br FROM BorrowRecord br JOIN FETCH br.book JOIN FETCH br.user WHERE br.user.id = :userId AND br.status = 0 AND br.dueDate < :now")
    List<BorrowRecord> findOverdueRecordsByUserId(@Param("userId") Long userId, @Param("now") LocalDate now);
}