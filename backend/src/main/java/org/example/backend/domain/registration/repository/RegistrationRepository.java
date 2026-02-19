package org.example.backend.domain.registration.repository;

import org.example.backend.domain.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * Registration(수강신청) 엔티티 저장소.
 */
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Registration> findByCourseId(Long courseId);

    Optional<Registration> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
