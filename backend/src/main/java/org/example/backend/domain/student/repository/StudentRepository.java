package org.example.backend.domain.student.repository;

import org.example.backend.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Student 엔티티 저장소.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    boolean existsByStudentNumber(String studentNumber);
}
