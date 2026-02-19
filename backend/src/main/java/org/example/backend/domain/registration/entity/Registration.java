package org.example.backend.domain.registration.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.course.entity.Course;
import org.example.backend.domain.student.entity.Student;

import java.time.LocalDateTime;

/**
 * 수강신청 내역 엔티티.
 * 학생과 강의의 관계를 맺어주는 핵심 테이블이다.
 */
@Entity
@Table(name = "registrations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Student 엔티티 참조 (ManyToOne) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** Course 엔티티 참조 (ManyToOne) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** 신청 시각 (선착순 확인용) */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
