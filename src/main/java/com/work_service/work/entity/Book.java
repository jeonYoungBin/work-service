package com.work_service.work.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    @Column(name = "is_free")
    private boolean isFree; // 무료 여부
    @Column(name = "is_event_active")
    private boolean isEventActive; // 이벤트 활성화 여부
    @Column(name = "grade_type")
    private String gradeType;
    private LocalDateTime createdAt;
}
