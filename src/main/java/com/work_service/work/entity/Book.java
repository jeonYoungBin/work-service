package com.work_service.work.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    @Column(name = "is_free")
    private Boolean isFree; // 무료 여부
    @Column(name = "is_event_active")
    private Boolean isEventActive; // 이벤트 활성화 여부
    @Column(name = "grade_type")
    private String gradeType;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void updateEventActive(Boolean isEventActive) {
        this.isEventActive = isEventActive;
    }

    public void updateIsFree(Boolean isFree) {
        this.isFree = isFree;
    }
}
