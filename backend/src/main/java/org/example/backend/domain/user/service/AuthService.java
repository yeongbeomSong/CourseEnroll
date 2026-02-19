package org.example.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.department.entity.Department;
import org.example.backend.domain.department.repository.DepartmentRepository;
import org.example.backend.domain.professor.entity.Professor;
import org.example.backend.domain.professor.repository.ProfessorRepository;
import org.example.backend.domain.student.entity.Student;
import org.example.backend.domain.student.repository.StudentRepository;
import org.example.backend.domain.user.dto.LoginRequest;
import org.example.backend.domain.user.dto.LoginResponse;
import org.example.backend.domain.user.dto.SignupRequest;
import org.example.backend.domain.user.entity.Role;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인·회원가입 등 인증 비즈니스 로직.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인: 비밀번호 검증 후 JWT 발급.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), user.getRole());
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * 회원가입 (학생 또는 교수). 관리자(ROLE_ADMIN)는 별도 시딩 등으로 생성.
     */
    @Transactional
    public LoginResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID입니다.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학과입니다."));

        Role role = request.getRole();
        if (role != Role.ROLE_STUDENT && role != Role.ROLE_PROFESSOR) {
            throw new IllegalArgumentException("회원가입은 학생 또는 교수만 가능합니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(role)
                .build();
        user = userRepository.save(user);

        if (role == Role.ROLE_STUDENT) {
            validateStudentSignup(request);
            if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
                throw new IllegalArgumentException("이미 사용 중인 학번입니다.");
            }
            Student student = Student.builder()
                    .user(user)
                    .department(department)
                    .studentNumber(request.getStudentNumber() != null ? request.getStudentNumber() : request.getUsername())
                    .grade(request.getGrade() != null ? request.getGrade() : 1)
                    .maxCredits(request.getMaxCredits() != null ? request.getMaxCredits() : 21)
                    .currentCredits(0)
                    .build();
            studentRepository.save(student);
        } else {
            validateProfessorSignup(request);
            if (professorRepository.existsByProfessorCode(request.getProfessorCode())) {
                throw new IllegalArgumentException("이미 사용 중인 사번입니다.");
            }
            Professor professor = Professor.builder()
                    .user(user)
                    .department(department)
                    .professorCode(request.getProfessorCode())
                    .position(request.getPosition() != null ? request.getPosition() : "교수")
                    .build();
            professorRepository.save(professor);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), user.getRole());
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    private void validateStudentSignup(SignupRequest request) {
        if (request.getStudentNumber() == null || request.getStudentNumber().isBlank()) {
            request.setStudentNumber(request.getUsername());
        }
        if (request.getGrade() == null) request.setGrade(1);
        if (request.getMaxCredits() == null) request.setMaxCredits(21);
    }

    private void validateProfessorSignup(SignupRequest request) {
        if (request.getProfessorCode() == null || request.getProfessorCode().isBlank()) {
            request.setProfessorCode(request.getUsername());
        }
        if (request.getPosition() == null || request.getPosition().isBlank()) {
            request.setPosition("교수");
        }
    }
}
