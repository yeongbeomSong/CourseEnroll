package org.example.backend.domain.student.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.department.entity.Department;
import org.example.backend.domain.user.entity.User;

/**
 * 학생 엔티티.
 * 학생 전용 정보로, 학점 및 학년 관리가 핵심이다.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User 엔티티 참조 (OneToOne) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Department 엔티티 참조 (ManyToOne) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** 학번 */
    @Column(name = "student_number", nullable = false)
    private String studentNumber;

    /** 학년 (1~4) */
    @Column(nullable = false)
    private Integer grade;

    /** 신청 가능한 최대 학점 (예: 21) */
    @Column(name = "max_credits", nullable = false)
    private Integer maxCredits;

    /** 현재 신청 완료한 학점 (검증용) */
    @Column(name = "current_credits", nullable = false)
    private Integer currentCredits;
}
