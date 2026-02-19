package org.example.backend.domain.course.entity;

/**
 * 강의 이수 구분.
 * 전공필수, 전공선택, 교양 등을 구분한다.
 */
public enum Category {
    /** 전공필수 */
    MAJOR_REQUIRED,
    /** 전공선택 */
    MAJOR_SELECT,
    /** 교양 */
    GENERAL
}
