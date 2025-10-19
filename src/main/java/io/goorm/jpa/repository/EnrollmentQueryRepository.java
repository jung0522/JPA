package io.goorm.jpa.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.goorm.jpa.entity.QEnrollment.enrollment;
import static io.goorm.jpa.entity.QUser.user;
import static io.goorm.jpa.entity.QCourse.course;

/**
 * Enrollment QueryDSL Repository
 * 동적 검색, 집계 쿼리
 */
@Repository
@RequiredArgsConstructor
public class EnrollmentQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 수강신청 동적 검색 (관리자용)
     */
    public Page<Enrollment> search(Long studentNo, Long courseNo, EnrollmentStatus status, Pageable pageable) {
        List<Enrollment> content = queryFactory
                .selectFrom(enrollment)
                .join(enrollment.student, user).fetchJoin()
                .join(enrollment.course, course).fetchJoin()
                .where(
                        enrollment.deleted.eq(false),
                        studentNoEq(studentNo),
                        courseNoEq(courseNo),
                        statusEq(status)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(enrollment.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(enrollment.count())
                .from(enrollment)
                .where(
                        enrollment.deleted.eq(false),
                        studentNoEq(studentNo),
                        courseNoEq(courseNo),
                        statusEq(status)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    /**
     * 내 수강신청 목록
     */
    public Page<Enrollment> findByStudent(User student, Pageable pageable) {
        List<Enrollment> content = queryFactory
                .selectFrom(enrollment)
                .join(enrollment.course, course).fetchJoin()
                .join(course.instructor, user).fetchJoin()
                .where(
                        enrollment.student.eq(student),
                        enrollment.deleted.eq(false)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(enrollment.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(enrollment.count())
                .from(enrollment)
                .where(
                        enrollment.student.eq(student),
                        enrollment.deleted.eq(false)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    /**
     * 동적 조건: 학생 번호
     */
    private BooleanExpression studentNoEq(Long studentNo) {
        return studentNo != null ? enrollment.student.userNo.eq(studentNo) : null;
    }

    /**
     * 동적 조건: 강의 번호
     */
    private BooleanExpression courseNoEq(Long courseNo) {
        return courseNo != null ? enrollment.course.courseNo.eq(courseNo) : null;
    }

    /**
     * 동적 조건: 상태
     */
    private BooleanExpression statusEq(EnrollmentStatus status) {
        return status != null ? enrollment.status.eq(status) : null;
    }
}
