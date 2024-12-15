package com.work_service.work.repository;

import com.work_service.work.domain.response.projection.PurchaseResponseProjection;
import com.work_service.work.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PurchaseJpaDataRepository extends JpaRepository<PurchaseHistory, Long> {
    @Query(value = "SELECT p.book_id AS bookId, b.title AS title, COUNT(p.book_id) AS purchaseCount " +
            "FROM purchase_history p " +
            "JOIN book b ON p.book_id = b.book_id " +
            "GROUP BY p.book_id, b.title " +
            "ORDER BY purchaseCount DESC " +
            "LIMIT 10", nativeQuery = true)
    List<PurchaseResponseProjection> findTop10BooksByPurchases();

    @Modifying
    @Query("DELETE FROM PurchaseHistory v where v.book.id = :bookId")
    void deleteAllByBookId(Long bookId);

    Optional<PurchaseHistory> findByBookIdAndMemberId(Long bookId, Long memberId);
}
