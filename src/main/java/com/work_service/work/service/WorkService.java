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
import com.work_service.work.jwtUtills.JwtTokenUtil;
import com.work_service.work.repository.BookJpaDataRepository;
import com.work_service.work.repository.PurchaseJpaDataRepository;
import com.work_service.work.repository.MemberJpaDataRepository;
import com.work_service.work.repository.ViewJpaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private static final int LIMIT_AGE = 19;

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#bookId+':'+#page+':'+#size")
    public List<ViewHistory> findViewHistory(Long bookId, int page, int size) {
        return viewRepository.findAllByBookId(bookId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#findTop10PopularBooks")
    public List<BookResponse> findTop10PopularBooks() {
        return viewRepository.findByTop10BooksByViews()
                .stream()
                .map(findViewBooks -> new BookResponse(findViewBooks.getBookId(),findViewBooks.getTitle(),findViewBooks.getViewCount()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long savePurchaseHistory(Long bookId, String userId) throws CustomException {
        System.out.println(userId);
        Member member = userRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        Book book =  bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));

        if(book.getGradeType().equals(GradeType.YouthNotAllowed.name()) && member.getAge() < LIMIT_AGE)
            throw new CustomException(ServiceExceptionCode.NOT_ALLOW_BOOK);

        return purchaseRepository.save(PurchaseHistory.builder().member(member).book(book).build()).getId();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#findTop10PurchasedPopularBooks")
    public List<PurchasedBookResponse> findTop10PurchasedPopularBooks() {
        return purchaseRepository.findTop10BooksByPurchases()
                .stream()
                .map( purchasePopularBook -> new PurchasedBookResponse(purchasePopularBook.getBookId(), purchasePopularBook.getTitle(), purchasePopularBook.getPurchaseCount())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteBookWithHistory(Long bookId) {
        bookRepository.deleteById(bookId);
        viewRepository.deleteAllByBookId(bookId);
        purchaseRepository.deleteAllByBookId(bookId);
    }

    @Transactional
    public String saveMember(String userId, String password, String userName, Integer age) throws CustomException {
        Optional<Member> findMemberCheck = memberRepository.findByUserId(userId);
        if(!findMemberCheck.isPresent()) {
            memberRepository.save(
                    Member.builder()
                            .username(userName)
                            .password(passwordEncoder.encode(password))
                            .userId(userId)
                            .age(age)
                            .build()
            );
        } else {
            throw new CustomException(ServiceExceptionCode.ALREADY_JOIN);
        }
        return jwtTokenProvider.createToken(userId);
    }

    @Transactional
    public Long saveViewHistory(Long bookId, String userId) throws CustomException {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        Member findMember = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        //이미 조회가 되있으면 exception
        if(viewRepository.findByBookIdAndMemberId(findBook.getId(), findMember.getId()).isPresent())
            throw new CustomException(ServiceExceptionCode.ALREADY_VIEW);

        if(findBook.getGradeType().equals(GradeType.YouthNotAllowed.name()) && findMember.getAge() < LIMIT_AGE) {
            throw new CustomException(ServiceExceptionCode.NOT_ALLOW_BOOK);
        }
        return viewRepository.save(ViewHistory.builder().member(findMember).book(findBook).build()).getId();
    }

    @Transactional(readOnly = true)
    public String login(String userId, String password) throws CustomException {
        Member member = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        if(!passwordEncoder.matches(password,member.getPassword())) {
            throw new CustomException(ServiceExceptionCode.NOT_PASSWORD_MATCH);
        }
        return jwtTokenProvider.createToken(userId);
    }

    @Transactional
    public Long saveBook(String title, boolean free, boolean eventActive, String gradeType) {
        return bookRepository.save(Book.builder().title(title).isFree(free).isEventActive(eventActive).gradeType(gradeType).build()).getId();
    }

    @Transactional
    public boolean updateBookEvent(boolean eventActive, Long bookId) throws CustomException {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND));
        book.updateEventActive(eventActive);
        return bookRepository.save(book).isEventActive();
    }
}
