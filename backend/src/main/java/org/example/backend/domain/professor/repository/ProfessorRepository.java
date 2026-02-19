package org.example.backend.domain.professor.repository;

import org.example.backend.domain.professor.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Professor 엔티티 저장소.
 */
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByUserId(Long userId);

    boolean existsByProfessorCode(String professorCode);
}
