package org.example.backend.domain.department.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 학과 엔티티.
 * 시스템의 기준이 되는 학과 정보를 담는다.
 */
@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 학과명 (예: 소프트웨어학과) */
    @Column(nullable = false)
    private String name;

    /** 학과 코드 (예: SW01) */
    @Column(name = "dept_code", nullable = false)
    private String deptCode;

    /** 단과대학명 (예: IT대학) */
    @Column(nullable = false)
    private String college;
}
