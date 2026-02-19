package org.example.backend.domain.professor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.department.entity.Department;
import org.example.backend.domain.user.entity.User;

/**
 * 교수 엔티티.
 * 강의 개설의 주체인 교수 정보를 담는다.
 */
@Entity
@Table(name = "professors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professor {

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

    /** 사번 */
    @Column(name = "professor_code", nullable = false)
    private String professorCode;

    /** 직위 (정교수, 부교수 등) */
    @Column(nullable = false)
    private String position;
}
