package org.example.backend.domain.department.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.department.dto.DepartmentCreateRequest;
import org.example.backend.domain.department.dto.DepartmentResponse;
import org.example.backend.domain.department.dto.DepartmentUpdateRequest;
import org.example.backend.domain.department.entity.Department;
import org.example.backend.domain.department.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 학과 조회·관리 서비스.
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /** 전체 학과 목록 조회 (모든 사용자) */
    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** [Admin] 학과 신설 */
    @Transactional
    public DepartmentResponse create(DepartmentCreateRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .deptCode(request.getDeptCode())
                .college(request.getCollege())
                .build();
        department = departmentRepository.save(department);
        return toResponse(department);
    }

    /** [Admin] 학과 정보 수정 */
    @Transactional
    public DepartmentResponse update(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("학과를 찾을 수 없습니다."));
        if (request.getName() != null && !request.getName().isBlank()) {
            department.setName(request.getName());
        }
        if (request.getDeptCode() != null && !request.getDeptCode().isBlank()) {
            department.setDeptCode(request.getDeptCode());
        }
        if (request.getCollege() != null && !request.getCollege().isBlank()) {
            department.setCollege(request.getCollege());
        }
        return toResponse(departmentRepository.save(department));
    }

    /** [Admin] 학과 삭제 */
    @Transactional
    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("학과를 찾을 수 없습니다."));
        departmentRepository.delete(department);
    }

    private DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .deptCode(d.getDeptCode())
                .college(d.getCollege())
                .build();
    }
}
