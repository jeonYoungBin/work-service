package com.work_service.work.repository;

import com.work_service.work.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaDataRepository extends JpaRepository<Book, Long> {

}

