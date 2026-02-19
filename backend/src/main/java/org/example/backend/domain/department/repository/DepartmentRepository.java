package org.example.backend.domain.department.repository;

import org.example.backend.domain.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Department 엔티티 저장소.
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
