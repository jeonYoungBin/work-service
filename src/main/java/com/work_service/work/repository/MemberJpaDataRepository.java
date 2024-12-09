package com.work_service.work.repository;

import com.work_service.work.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaDataRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
}
