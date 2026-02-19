package org.example.backend.domain.course.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.department.entity.Department;
import org.example.backend.domain.professor.entity.Professor;

/**
 * 강의 엔티티.
 * 수강신청의 대상이 되는 강의 정보를 담는다.
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 담당 Professor 엔티티 참조 (ManyToOne) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    /** 개설 Department 엔티티 참조 (ManyToOne) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** 과목 코드 (예: CS101, Unique) */
    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;

    /** 강의명 */
    @Column(nullable = false)
    private String title;

    /** 이수 구분 (MAJOR_REQUIRED, MAJOR_SELECT, GENERAL) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    /** 학점 (1~3) */
    @Column(nullable = false)
    private Integer credit;

    /** 수강 정원 */
    @Column(nullable = false)
    private Integer capacity;

    /** 현재 신청 인원 (동시성 제어 대상) */
    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment;

    /** 대상 학년 */
    @Column(name = "target_grade", nullable = false)
    private Integer targetGrade;

    /** 강의 시간 (예: Mon 09:00-11:00) */
    @Column(nullable = false)
    private String schedule;
}
