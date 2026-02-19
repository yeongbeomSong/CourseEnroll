package org.example.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.professor.entity.Professor;
import org.example.backend.domain.professor.repository.ProfessorRepository;
import org.example.backend.domain.student.entity.Student;
import org.example.backend.domain.student.repository.StudentRepository;
import org.example.backend.domain.user.dto.UserDetailResponse;
import org.example.backend.domain.user.dto.UserResponse;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 조회·관리 비즈니스 로직 (마이페이지, Admin 목록/상세/삭제).
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    /**
     * 내 정보 조회 (마이페이지). 학생/교수에 따라 학번·사번 등 포함.
     */
    @Transactional(readOnly = true)
    public UserResponse getMe(User user) {
        return toUserResponse(user);
    }

    /**
     * 관리자: 전체 유저 목록 (필터: 학번/사번, 이름, 역할).
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersForAdmin(String keyword, String name, org.example.backend.domain.user.entity.Role role, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                Predicate byUsername = cb.like(root.get("username"), "%" + keyword + "%");
                predicates.add(byUsername);
            }
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec, pageable).map(this::toUserResponse);
    }

    /**
     * 관리자: 특정 유저 상세 조회.
     */
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetailForAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return toUserDetailResponse(user);
    }

    /**
     * 관리자: 유저 삭제 (User 삭제 시 Student/Professor는 cascade 또는 수동 삭제).
     */
    @Transactional
    public void deleteUserByAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        studentRepository.findByUserId(user.getId()).ifPresent(studentRepository::delete);
        professorRepository.findByUserId(user.getId()).ifPresent(professorRepository::delete);
        userRepository.delete(user);
    }

    private UserResponse toUserResponse(User user) {
        String deptName = null;
        String deptCode = null;
        String studentNumber = null;
        Integer grade = null;
        String professorCode = null;
        String position = null;

        if (user.getRole() == org.example.backend.domain.user.entity.Role.ROLE_STUDENT) {
            Student s = studentRepository.findByUserId(user.getId()).orElse(null);
            if (s != null) {
                deptName = s.getDepartment().getName();
                deptCode = s.getDepartment().getDeptCode();
                studentNumber = s.getStudentNumber();
                grade = s.getGrade();
            }
        } else if (user.getRole() == org.example.backend.domain.user.entity.Role.ROLE_PROFESSOR) {
            Professor p = professorRepository.findByUserId(user.getId()).orElse(null);
            if (p != null) {
                deptName = p.getDepartment().getName();
                deptCode = p.getDepartment().getDeptCode();
                professorCode = p.getProfessorCode();
                position = p.getPosition();
            }
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole())
                .studentNumber(studentNumber)
                .grade(grade)
                .professorCode(professorCode)
                .position(position)
                .departmentName(deptName)
                .departmentCode(deptCode)
                .build();
    }

    private UserDetailResponse toUserDetailResponse(User user) {
        UserDetailResponse.UserDetailResponseBuilder b = UserDetailResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole());

        if (user.getRole() == org.example.backend.domain.user.entity.Role.ROLE_STUDENT) {
            Student s = studentRepository.findByUserId(user.getId()).orElse(null);
            if (s != null) {
                b.departmentId(s.getDepartment().getId())
                        .departmentName(s.getDepartment().getName())
                        .departmentCode(s.getDepartment().getDeptCode())
                        .studentNumber(s.getStudentNumber())
                        .grade(s.getGrade())
                        .maxCredits(s.getMaxCredits())
                        .currentCredits(s.getCurrentCredits());
            }
        } else if (user.getRole() == org.example.backend.domain.user.entity.Role.ROLE_PROFESSOR) {
            Professor p = professorRepository.findByUserId(user.getId()).orElse(null);
            if (p != null) {
                b.departmentId(p.getDepartment().getId())
                        .departmentName(p.getDepartment().getName())
                        .departmentCode(p.getDepartment().getDeptCode())
                        .professorCode(p.getProfessorCode())
                        .position(p.getPosition());
            }
        }
        return b.build();
    }
}
