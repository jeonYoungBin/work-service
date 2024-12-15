package com.work_service.work.controller;

import com.work_service.work.domain.response.ViewBookResponse;
import com.work_service.work.domain.response.PurchasedBookResponse;
import com.work_service.work.entity.ViewHistory;
import com.work_service.work.exception.CustomException;
import com.work_service.work.service.WorkService;
import io.jsonwebtoken.lang.Collections;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    public ResponseEntity<MemberTokenResponse> saveMember(@RequestBody @Valid MemberSaveRequest request) throws CustomException {
        return ResponseEntity.ok(MemberTokenResponse.builder()
                .token(workService.saveMember(request.getUserId(), request.getPassword(), request.getUserName(), request.getAge()))
                .build());
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<MemberTokenResponse> loginMember(@RequestBody @Valid MemberLoginRequest request) throws CustomException {
        return ResponseEntity.ok(MemberTokenResponse.builder()
                .token(workService.login(request.getUserId(), request.getPassword()))
                .build());
    }

    /**
     * 작품등록
     */
    @PostMapping
    public ResponseEntity<BookSaveResponse> saveBook(@RequestBody @Valid BookSaveRequest request) {
        return ResponseEntity.ok(BookSaveResponse.builder()
                .bookId(workService.saveBook(request.getTitle(), request.getIsFree(), request.getIsEventActive(), request.getGradeType()))
                .build());
    }

    /**
     * 작품 이벤트 ON/OFF
     */
    @PatchMapping("/enable/event")
    public ResponseEntity<BookUpdateEventResponse> updateBookEvent(@RequestBody @Valid BookUpdateEventRequest request) throws CustomException {
        return ResponseEntity.ok(BookUpdateEventResponse.builder()
                .bookId(request.getBookId()).isEventActive(workService.updateBookEvent(request.getIsEventActive(), request.getBookId()))
                .build());
    }

    /**
     * 무료 ON/OFF
     */
    @PatchMapping("/enable/isFree")
    public ResponseEntity<BookUpdateIsFreeResponse> updateBookIsFree(@RequestBody @Valid BookUpdateIsFreeRequest request) throws CustomException {
        return ResponseEntity.ok(BookUpdateIsFreeResponse.builder()
                .bookId(request.getBookId()).isFree(workService.updateBookIsFree(request.getIsFree(), request.getBookId()))
                .build());
    }

    /**
     * 작품 조회 등록
     */
    @PostMapping("/{bookId}/views")
    public ResponseEntity<ViewHistorySaveResponse> saveViewHistory(@PathVariable Long bookId, Authentication authentication) throws CustomException {
        return ResponseEntity.ok(ViewHistorySaveResponse.builder()
                .viewHistoryId(workService.saveViewHistory(bookId, authentication.getName())).build());
    }

    /**
     * 작품 조회 이력
     */
    @GetMapping("/{bookId}/views")
    public ResponseEntity<List<ViewHistoryResponse>> findViewHistory(@PathVariable Long bookId,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        List<ViewHistoryResponse> findViewHistoryList = workService.findViewHistory(bookId, page - 1, size)
                .stream().map(viewHistory -> new ViewHistoryResponse(viewHistory)
        ).collect(Collectors.toList());

        if(Collections.isEmpty(findViewHistoryList))
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok(findViewHistoryList);
    }

    /**
     * 인기 작품 조회 API
     */
    @GetMapping("/popular")
    public ResponseEntity<List<ViewBookResponse>> findTop10PopularWorks() {
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
    static public class BookUpdateEventRequest {
        @NotNull(message = "bookId를 넣어 주세요")
        private Long bookId;
        @NotNull(message = "이벤트 활성 여부를 넣어 주세요")
        private Boolean isEventActive;
    }

    @Getter
    static public class BookUpdateIsFreeRequest {
        @NotNull(message = "bookId를 넣어 주세요")
        private Long bookId;
        @NotNull(message = "무료 여부를 넣어 주세요")
        private Boolean isFree;
    }

    @Getter
    @Builder
    static class BookUpdateEventResponse {
        private Long bookId;
        private Boolean isEventActive;
    }

    @Getter
    @Builder
    static class BookUpdateIsFreeResponse {
        private Long bookId;
        private Boolean isFree;
    }

    @Getter
    @Builder
    static class BookSaveResponse {
        private Long bookId;
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
    static public class MemberSaveRequest {
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
    static public class MemberLoginRequest {
        @NotNull(message = "아이디를 넣어주세요")
        private String userId;
        @NotNull(message = "패스워드를 넣어주세요")
        private String password;
    }

    @Getter
    static public class BookSaveRequest {
        @NotNull(message = "책 제목을 입력하세요")
        private String title;
        private Boolean isFree = true; // 무료 여부
        private Boolean isEventActive = false; // 이벤트 활성화 여부
        @NotNull(message = "등급 타입을 입력하세요")
        @Pattern(regexp = "ALL|YouthNotAllowed", message = "등급 타입은 ALL 또는 YouthNotAllowed 이여야 합니다.")
        private String gradeType;
    }
}
