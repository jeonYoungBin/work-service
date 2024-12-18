package com.work_service.work.repository;

import com.work_service.work.domain.response.projection.BookResponseProjection;
import com.work_service.work.entity.ViewHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ViewJpaDataRepository extends JpaRepository<ViewHistory, Long> {
    @Query(value = "SELECT v.book_id AS bookId, b.title AS title, COUNT(v.view_history_id) AS viewCount " +
            "FROM view_history v " +
            "JOIN book b ON v.book_id = b.book_id " +
            "GROUP BY v.book_id, b.title " +
            "ORDER BY viewCount DESC " +
            "LIMIT 10", nativeQuery = true)
    List<BookResponseProjection> findByTop10BooksByViews();

    List<ViewHistory> findAllByBookId(Long bookId, Pageable pageable);

    @Modifying
    @Query("DELETE from ViewHistory v where v.book.id = :bookId")
    void deleteAllByBookId(Long bookId);

    Optional<ViewHistory> findByBookIdAndMemberId(Long bookId, Long memberId);
}
