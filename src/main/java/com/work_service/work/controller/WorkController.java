package com.work_service.work.controller;

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

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/book")
@Slf4j
@RequiredArgsConstructor
public class WorkController {
    private final WorkService workService;

    /**
     * 회원 가입
     */
    @PostMapping("/sign")
    public ResponseEntity<MemberTokenResponse> saveMember(@RequestBody MemberSaveRequestDto request) throws CustomException {
        return ResponseEntity.ok(MemberTokenResponse.builder().token(workService.saveMember(request.getUserId(), request.getPassword(), request.getUserName(), request.getAge())).build());
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<MemberTokenResponse> loginMember(@RequestBody MemberLoginRequestDto request) throws CustomException {
        return ResponseEntity.ok(MemberTokenResponse.builder().token(workService.login(request.getUserId(), request.getPassword())).build());
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
    public ResponseEntity<PurchaseHistorySaveResponse> purchaseHistorySave(@PathVariable Long bookId, Authentication authentication) throws CustomException, CustomException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PurchaseHistorySaveResponse.builder()
                        .purchaseHistoryId(workService.savePurchaseHistory(bookId, authentication.getName()))
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
        workService.deleteBookWithHistory(bookId);
        return ResponseEntity.ok(BookHistoryDeleteResponse.builder().bookId(bookId).build());
    }

    @Getter
    @Builder
    static class ViewHistorySaveResponse{
        private Long viewHistoryId;
    }

    @Getter
    @Builder
    static class MemberTokenResponse {
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

    @Getter
    static public class MemberSaveRequestDto {
        @NotNull(message = "아이디를 넣어주세요")
        private String userId;
        @NotNull(message = "패스워드를 넣어주세요")
        private String password;
        @NotNull(message = "이름을 넣어주세요")
        private String userName;
        @NotNull(message = "나이를 넣어주세요")
        private Integer age;
    }

    @Getter
    static public class MemberLoginRequestDto {
        @NotNull(message = "아이디를 넣어주세요")
        private String userId;
        @NotNull(message = "패스워드를 넣어주세요")
        private String password;
    }
}
