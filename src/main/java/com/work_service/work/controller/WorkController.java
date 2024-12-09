package com.work_service.work.controller;

import com.work_service.work.domain.request.MemberSaveRequestDto;
import com.work_service.work.domain.response.BookResponse;
import com.work_service.work.domain.response.PurchasedBookResponse;
import com.work_service.work.entity.ViewHistory;
import com.work_service.work.exception.CustomException;
import com.work_service.work.service.WorkService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/work")
@Slf4j
@RequiredArgsConstructor
public class WorkController {
    private final WorkService workService;

    /**
     * 회원 가입
     */
    @PostMapping("/sign")
    public ResponseEntity<MemberSaveResponse> saveMember(@RequestBody MemberSaveRequestDto request) throws CustomException {
        return ResponseEntity.ok(MemberSaveResponse.builder().token(workService.saveMember(request)).build());
    }

    /**
     * 작품 조회
     */
    @PostMapping("/{bookId}/views")
    public ResponseEntity<ViewHistorySaveResponse> saveViewHistory(@PathVariable Long bookId, Authentication authentication) throws CustomException {
        return ResponseEntity.ok(ViewHistorySaveResponse.builder().viewHistoryId(workService.saveViewHistory(bookId, authentication.getName())).build());
    }

    /**
     * 작품 조회 이력
     */
    @GetMapping("/{bookId}/views")
    public ResponseEntity<List<ViewHistoryResponse>> findViewHistory(@PathVariable Long bookId) {
        List<ViewHistoryResponse> findViewHistoryList = workService.findViewHistory(bookId).stream().map(viewHistory ->
                new ViewHistoryResponse(viewHistory)
        ).collect(Collectors.toList());

        return ResponseEntity.ok(findViewHistoryList);
    }

    /**
     * 인기 작품 조회 API
     */
    @GetMapping("/popular")
    public ResponseEntity<List<BookResponse>> findTop10PopularWorks() {
        return ResponseEntity.ok(workService.findTop10PopularBooks());
    }

    /**
     * 작품구매 API
     */
    @PostMapping("/{bookId}/purchase")
    public ResponseEntity<PurchaseHistorySaveResponse> purchaseHistorySave(@PathVariable Long bookId, @RequestParam Long userId) throws CustomException, CustomException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PurchaseHistorySaveResponse.builder()
                        .purchaseHistoryId(workService.savePurchaseHistory(bookId, userId))
                        .build());
    }

    /**
     * 구매 인기 작품 조회 API
     */
    @GetMapping("/purchases/popular")
    public ResponseEntity<List<PurchasedBookResponse>> findTop10PurchasedWorks() {
        return ResponseEntity.ok(workService.findTop10PurchasedPopularBooks());
    }

    /**
     * 작품 및 이력 삭제 API
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<BookHistoryDeleteResponse> deleteBookHistory(@PathVariable Long bookId) {
        workService.deleteWorkWithHistory(bookId);
        return ResponseEntity.ok(BookHistoryDeleteResponse.builder().bookId(bookId).build());
    }

    @Getter
    @Builder
    static class ViewHistorySaveResponse{
        private Long viewHistoryId;
    }

    @Getter
    @Builder
    static class MemberSaveResponse{
        private String token;
    }

    @Getter
    @Builder
    static class PurchaseHistorySaveResponse {
        private Long purchaseHistoryId;
    }

    @Getter
    @Builder
    static class BookHistoryDeleteResponse {
        private Long bookId;
    }

    @Getter
    static class ViewHistoryResponse {
        private Long id;
        private String name;
        private Integer age;
        private LocalDateTime viewedAt;
        public ViewHistoryResponse(ViewHistory viewHistory) {
            this.id = viewHistory.getMember().getId();
            this.name = viewHistory.getMember().getUsername();
            this.age = viewHistory.getMember().getAge();
            this.viewedAt = viewHistory.getViewedAt();
        }
    }
}
