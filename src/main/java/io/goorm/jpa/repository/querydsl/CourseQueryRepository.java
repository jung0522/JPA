package io.goorm.jpa.repository.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.jpa.dto.course.CourseSearchCondition;
import io.goorm.jpa.dto.dashboard.CourseStatistics;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static io.goorm.jpa.entity.QCourse.course;
import static io.goorm.jpa.entity.QUser.user;
import static io.goorm.jpa.entity.QEnrollment.enrollment;
import static io.goorm.jpa.entity.QCurriculum.curriculum;

/**
 * Course QueryDSL Repository
 * - 동적 검색, 통계 쿼리
 * - 공통 조건 모듈 활용
 */
@Repository
@RequiredArgsConstructor
public class CourseQueryRepository {

    private final JPAQueryFactory queryFactory;

    // ===== Step 2-3: QueryDSL 통계 쿼리 =====

    /**
     * 강의 통계 - 전체 현황
     */
    public CourseStatistics getCourseStatistics() {
        return queryFactory
            .select(Projections.constructor(CourseStatistics.class,
                course.count().as("totalCourses"),
                course.currentStudents.sum().as("totalStudents"),
                course.maxStudents.sum().as("totalCapacity"),
                course.currentStudents.avg().as("avgStudents"),
                course.maxStudents.avg().as("avgCapacity")
            ))
            .from(course)
            .where(CommonQueryConditions.notDeleted(course.deleted))
            .fetchOne();
    }

    /**
     * 강의별 상세 통계
     */
    public List<CourseStatistics> getCourseDetailedStatistics() {
        return queryFactory
            .select(Projections.constructor(CourseStatistics.class,
                course.courseNo,
                course.name,
                course.instructor.fullName,
                course.maxStudents,
                course.currentStudents,
                course.currentStudents.divide(course.maxStudents).multiply(100).as("enrollmentRatio"),
                enrollment.count().as("totalEnrollments"),
                enrollment.status.when(EnrollmentStatus.APPROVED).then(1L).otherwise(0L).sum().as("approvedEnrollments"),
                enrollment.status.when(EnrollmentStatus.PENDING).then(1L).otherwise(0L).sum().as("pendingEnrollments"),
                enrollment.status.when(EnrollmentStatus.REJECTED).then(1L).otherwise(0L).sum().as("rejectedEnrollments")
            ))
            .from(course)
            .leftJoin(enrollment).on(
                course.courseNo.eq(enrollment.course.courseNo)
                .and(CommonQueryConditions.notDeleted(enrollment.deleted))
            )
            .where(CommonQueryConditions.notDeleted(course.deleted))
            .groupBy(course.courseNo, course.name, course.instructor.fullName, 
                    course.maxStudents, course.currentStudents)
            .orderBy(course.currentStudents.desc())
            .fetch();
    }

    /**
     * 강사별 강의 통계
     */
    public List<CourseStatistics> getInstructorStatistics() {
        return queryFactory
            .select(Projections.constructor(CourseStatistics.class,
                course.instructor.userNo,
                course.instructor.fullName,
                course.count().as("totalCourses"),
                course.currentStudents.sum().as("totalStudents"),
                course.maxStudents.sum().as("totalCapacity"),
                course.currentStudents.avg().as("avgStudents"),
                enrollment.count().as("totalEnrollments")
            ))
            .from(course)
            .leftJoin(enrollment).on(
                course.courseNo.eq(enrollment.course.courseNo)
                .and(CommonQueryConditions.notDeleted(enrollment.deleted))
            )
            .where(CommonQueryConditions.notDeleted(course.deleted))
            .groupBy(course.instructor.userNo, course.instructor.fullName)
            .orderBy(course.count().desc())
            .fetch();
    }

    /**
     * 월별 강의 개설 통계
     */
    public List<CourseStatistics> getMonthlyCourseStatistics() {
        return queryFactory
            .select(Projections.constructor(CourseStatistics.class,
                course.createdAt.year(),
                course.createdAt.month(),
                course.count().as("monthlyCourses"),
                course.currentStudents.sum().as("monthlyStudents")
            ))
            .from(course)
            .where(CommonQueryConditions.notDeleted(course.deleted))
            .groupBy(course.createdAt.year(), course.createdAt.month())
            .orderBy(course.createdAt.year().desc(), course.createdAt.month().desc())
            .fetch();
    }

    // ===== Step 2-3: 공통 조건 모듈 활용 =====

    /**
     * 강의 동적 검색 (공통 조건 모듈 활용)
     */
    public Page<Course> searchCourses(CourseSearchCondition condition, Pageable pageable) {
        List<Course> content = queryFactory
            .selectFrom(course)
            .join(course.instructor, user).fetchJoin()
            .where(
                CommonQueryConditions.notDeleted(course.deleted),
                courseNameContains(condition.getCourseName()),
                instructorNameContains(condition.getInstructorName()),
                descriptionContains(condition.getDescription()),
                capacityBetween(condition.getMinCapacity(), condition.getMaxCapacity()),
                currentStudentsBetween(condition.getMinCurrentStudents(), condition.getMaxCurrentStudents()),
                enrollmentRatioBetween(condition.getMinEnrollmentRatio(), condition.getMaxEnrollmentRatio()),
                createdAtBetween(condition.getStartDate(), condition.getEndDate()),
                isAvailable(condition.getIsAvailable())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(createOrderSpecifier(condition.getSortBy()))
            .fetch();

        Long total = queryFactory
            .select(course.count())
            .from(course)
            .join(course.instructor, user)
            .where(
                CommonQueryConditions.notDeleted(course.deleted),
                courseNameContains(condition.getCourseName()),
                instructorNameContains(condition.getInstructorName()),
                descriptionContains(condition.getDescription()),
                capacityBetween(condition.getMinCapacity(), condition.getMaxCapacity()),
                currentStudentsBetween(condition.getMinCurrentStudents(), condition.getMaxCurrentStudents()),
                enrollmentRatioBetween(condition.getMinEnrollmentRatio(), condition.getMaxEnrollmentRatio()),
                createdAtBetween(condition.getStartDate(), condition.getEndDate()),
                isAvailable(condition.getIsAvailable())
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    /**
     * 강의 검색 (텍스트 기반)
     */
    public Page<Course> searchCoursesByText(String searchText, Pageable pageable) {
        List<Course> content = queryFactory
            .selectFrom(course)
            .join(course.instructor, user).fetchJoin()
            .where(
                CommonQueryConditions.notDeleted(course.deleted),
                textSearch(searchText)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(course.createdAt.desc())
            .fetch();

        Long total = queryFactory
            .select(course.count())
            .from(course)
            .join(course.instructor, user)
            .where(
                CommonQueryConditions.notDeleted(course.deleted),
                textSearch(searchText)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    // ===== 동적 조건 메서드들 (공통 조건 모듈 활용) =====

    private BooleanExpression courseNameContains(String courseName) {
        return CommonQueryConditions.stringContains(course.name, courseName);
    }

    private BooleanExpression instructorNameContains(String instructorName) {
        return CommonQueryConditions.stringContains(course.instructor.fullName, instructorName);
    }

    private BooleanExpression descriptionContains(String description) {
        return CommonQueryConditions.stringContains(course.description, description);
    }

    private BooleanExpression capacityBetween(Integer minCapacity, Integer maxCapacity) {
        return CommonQueryConditions.numberBetween(course.maxStudents, minCapacity, maxCapacity);
    }

    private BooleanExpression currentStudentsBetween(Integer minStudents, Integer maxStudents) {
        return CommonQueryConditions.numberBetween(course.currentStudents, minStudents, maxStudents);
    }

    private BooleanExpression enrollmentRatioBetween(Double minRatio, Double maxRatio) {
        return CommonQueryConditions.ratioBetween(
            course.currentStudents, course.maxStudents, minRatio, maxRatio);
    }

    private BooleanExpression createdAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return CommonQueryConditions.dateBetween(course.createdAt, startDate, endDate);
    }

    private BooleanExpression isAvailable(Boolean isAvailable) {
        if (isAvailable == null) return null;
        return isAvailable 
            ? course.currentStudents.lt(course.maxStudents)
            : course.currentStudents.goe(course.maxStudents);
    }

    private BooleanExpression textSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return null;
        
        String trimmedText = searchText.trim();
        return course.name.contains(trimmedText)
            .or(course.description.contains(trimmedText))
            .or(course.instructor.fullName.contains(trimmedText));
    }

    private com.querydsl.core.types.OrderSpecifier<?> createOrderSpecifier(String sortBy) {
        if (sortBy == null) return course.createdAt.desc();
        
        return switch (sortBy) {
            case "name" -> course.name.asc();
            case "nameDesc" -> course.name.desc();
            case "capacity" -> course.maxStudents.asc();
            case "capacityDesc" -> course.maxStudents.desc();
            case "students" -> course.currentStudents.asc();
            case "studentsDesc" -> course.currentStudents.desc();
            case "enrollmentRatio" -> course.currentStudents.divide(course.maxStudents).desc();
            case "created" -> course.createdAt.asc();
            case "createdDesc" -> course.createdAt.desc();
            default -> course.createdAt.desc();
        };
    }

    // ===== 추가 통계 메서드들 =====

    /**
     * 인기 강의 TOP N (수강생 수 기준)
     */
    public List<Course> findPopularCourses(int limit) {
        return queryFactory
            .selectFrom(course)
            .join(course.instructor, user).fetchJoin()
            .where(CommonQueryConditions.notDeleted(course.deleted))
            .orderBy(course.currentStudents.desc(), course.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    /**
     * 수강 가능한 강의 (정원 미달)
     */
    public List<Course> findAvailableCourses() {
        return queryFactory
            .selectFrom(course)
            .join(course.instructor, user).fetchJoin()
            .where(
                CommonQueryConditions.notDeleted(course.deleted),
                course.currentStudents.lt(course.maxStudents)
            )
            .orderBy(course.createdAt.desc())
            .fetch();
    }

    /**
     * 강의별 커리큘럼 수 통계
     */
    public List<CourseStatistics> getCourseCurriculumStatistics() {
        return queryFactory
            .select(Projections.constructor(CourseStatistics.class,
                course.courseNo,
                course.name,
                curriculum.count().as("curriculumCount"),
                curriculum.weekNumber.max().as("maxWeekNumber")
            ))
            .from(course)
            .leftJoin(curriculum).on(
                course.courseNo.eq(curriculum.course.courseNo)
                .and(CommonQueryConditions.notDeleted(curriculum.deleted))
            )
            .where(CommonQueryConditions.notDeleted(course.deleted))
            .groupBy(course.courseNo, course.name)
            .orderBy(curriculum.count().desc())
            .fetch();
    }
}
