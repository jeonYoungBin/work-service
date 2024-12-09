package com.work_service.work.service;

import com.work_service.work.domain.GradeType;
import com.work_service.work.domain.request.MemberSaveRequestDto;
import com.work_service.work.domain.response.BookResponse;
import com.work_service.work.domain.response.PurchasedBookResponse;
import com.work_service.work.entity.Book;
import com.work_service.work.entity.Member;
import com.work_service.work.entity.PurchaseHistory;
import com.work_service.work.entity.ViewHistory;
import com.work_service.work.exception.CustomException;
import com.work_service.work.exception.ServiceExceptionCode;
import com.work_service.work.jwtUtills.JwtTokenUtil;
import com.work_service.work.repository.BookJpaDataRepository;
import com.work_service.work.repository.PurchaseJpaDataRepository;
import com.work_service.work.repository.MemberJpaDataRepository;
import com.work_service.work.repository.ViewJpaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final BookJpaDataRepository bookRepository;
    private final MemberJpaDataRepository userRepository;
    private final ViewJpaDataRepository viewRepository;
    private final PurchaseJpaDataRepository purchaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberJpaDataRepository memberRepository;
    private final JwtTokenUtil jwtTokenProvider;

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

        return purchaseRepository.save(PurchaseHistory.builder().member(member).book(book).purchasedAt(LocalDateTime.now()).build()).getId();
    }

    public List<PurchasedBookResponse> findTop10PurchasedPopularBooks() {
        return purchaseRepository.findTop10BooksByPurchases()
                .stream()
                .map( purchasePopularBook -> new PurchasedBookResponse(purchasePopularBook.getBookId(), purchasePopularBook.getTitle(), purchasePopularBook.getPurchaseCount())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteWorkWithHistory(Long bookId) {
        bookRepository.deleteById(bookId);
        viewRepository.deleteAllByBookId(bookId);
        purchaseRepository.deleteAllByBookId(bookId);
    }

    @Transactional
    public String saveMember(MemberSaveRequestDto request) throws CustomException {
        Optional<Member> findMemberCheck = memberRepository.findByUserId(request.getUserId());
        Member saveMember;
        if(!findMemberCheck.isPresent()) {
            saveMember = memberRepository.save(
                    Member.builder()
                            .username(request.getUserName())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .userId(request.getUserId())
                            .createdAt(LocalDateTime.now())
                            .age(request.getAge())
                            .build()
            );
        } else {
            throw new CustomException(ServiceExceptionCode.ALREADY_JOIN);
        }
        return jwtTokenProvider.createToken(request.getUserId());
    }

    public Long saveViewHistory(Long bookId, String userId) throws CustomException {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        Member findMember = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        if(findBook.getGradeType().equals(GradeType.NotAllowed.name()) && findMember.getAge() < 19) {
            throw new CustomException(ServiceExceptionCode.NOT_ALLOW_BOOK);
        }
        return viewRepository.save(ViewHistory.builder().member(findMember).book(findBook).createdAt(LocalDateTime.now()).viewedAt(LocalDateTime.now()).build()).getId();
    }
}
