package com.work_service.work.service;

import com.work_service.work.domain.GradeType;
import com.work_service.work.domain.response.BookResponse;
import com.work_service.work.domain.response.PurchasedBookResponse;
import com.work_service.work.entity.Book;
import com.work_service.work.entity.Member;
import com.work_service.work.entity.PurchaseHistory;
import com.work_service.work.entity.ViewHistory;
import com.work_service.work.exception.CustomException;
import com.work_service.work.exception.ServiceExceptionCode;
import com.work_service.work.repository.BookJpaDataRepository;
import com.work_service.work.repository.PurchaseJpaDataRepository;
import com.work_service.work.repository.MemberJpaDataRepository;
import com.work_service.work.repository.ViewJpaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final BookJpaDataRepository bookRepository;
    private final MemberJpaDataRepository userRepository;
    private final ViewJpaDataRepository viewRepository;
    private final PurchaseJpaDataRepository purchaseRepository;

    public List<ViewHistory> findViewHistory(Long bookId) {
        return viewRepository.findAllByBookId(bookId);
    }

    public List<BookResponse> findTop10PopularBooks() {
        return viewRepository.findTop10WorksByViews()
                .stream()
                .map(findViewBooks -> new BookResponse(findViewBooks.getBookId(),findViewBooks.getTitle(),findViewBooks.getViewCount()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long savePurchaseHistory(Long workId, Long userId) throws CustomException {
        Member member = userRepository.findById(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        Book book =  bookRepository.findById(workId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));

        if(book.getGradeType().equals(GradeType.NotAllowed.name()) && member.getAge() < 19)
            throw new CustomException(ServiceExceptionCode.NOT_ALLOW_BOOK);

        // 무료 작품인지 확인
        if (!book.isFree() && !book.isEventActive()) {
            throw new RuntimeException("This work is not free.");
        }

        return purchaseRepository.save(PurchaseHistory.builder().member(member).book(book).purchasedAt(LocalDateTime.now()).build()).getId();
    }

    public List<PurchasedBookResponse> findTop10PurchasedPopularBooks() {
        return purchaseRepository.findTop10BooksByPurchases().stream().map( purchasePopularBook ->
                new PurchasedBookResponse(purchasePopularBook.getBookId(), purchasePopularBook.getTitle(), purchasePopularBook.getPurchaseCount())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteWorkWithHistory(Long bookId) {
        bookRepository.deleteById(bookId);
        viewRepository.deleteAllByBookId(bookId);
        purchaseRepository.deleteAllByBookId(bookId);
    }
}
