package com.work_service.work;

import com.work_service.work.domain.GradeType;
import com.work_service.work.entity.Book;
import com.work_service.work.entity.Member;
import com.work_service.work.repository.BookJpaDataRepository;
import com.work_service.work.repository.MemberJpaDataRepository;
import com.work_service.work.repository.PurchaseJpaDataRepository;
import com.work_service.work.repository.ViewJpaDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final BookJpaDataRepository bookRepository;

    /**
     * 확인용 초기 데이터 추가
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        /*book init*/
//        Book book1 = Book.builder().title("BOOK1").createdAt(LocalDateTime.now()).gradeType(GradeType.ALL.name()).build();
//        bookRepository.save(book1);
//        Book book2 = Book.builder().title("BOOK2").createdAt(LocalDateTime.now()).gradeType(GradeType.ALL.name()).build();
//        bookRepository.save(book2);
//        Book book3 = Book.builder().title("BOOK3").createdAt(LocalDateTime.now()).gradeType(GradeType.YouthNotAllowed.name()).build();
//        bookRepository.save(book3);
//        Book book4 = Book.builder().title("BOOK4").createdAt(LocalDateTime.now()).gradeType(GradeType.ALL.name()).build();
//        bookRepository.save(book4);
//        Book book5 = Book.builder().title("BOOK5").createdAt(LocalDateTime.now()).gradeType(GradeType.ALL.name()).build();
//        bookRepository.save(book5);

        log.info("test data init");
    }

}
