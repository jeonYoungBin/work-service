package com.work_service.work.repository;

import com.work_service.work.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaDataRepository extends JpaRepository<Member, Long> {
}
