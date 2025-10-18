/**
 * 인증 관련 공통 함수
 * localStorage를 사용한 토큰 관리
 */

/**
 * 로그인 페이지로 이동
 */
function goLogin() {
    location.href = '/auth/login';
}

/**
 * 로그아웃
 */
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        localStorage.clear();
        location.href = '/';
    }
}

/**
 * 저장된 토큰 가져오기
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * 현재 사용자 정보 가져오기
 */
function getCurrentUser() {
    return {
        username: localStorage.getItem('username'),
        role: localStorage.getItem('role')
    };
}

/**
 * 로그인 여부 확인
 */
function isLoggedIn() {
    return !!getToken();
}

/**
 * 로그인 필수 체크 (로그인 안되어 있으면 로그인 페이지로 이동)
 */
function requireLogin() {
    if (!isLoggedIn()) {
        alert('로그인이 필요합니다.');
        goLogin();
        return false;
    }
    return true;
}

/**
 * 권한 체크
 */
function hasRole(role) {
    const currentRole = localStorage.getItem('role');
    if (Array.isArray(role)) {
        return role.includes(currentRole);
    }
    return currentRole === role;
}

/**
 * ADMIN 권한 체크
 */
function isAdmin() {
    return hasRole('ADMIN');
}

/**
 * INSTRUCTOR 권한 체크
 */
function isInstructor() {
    return hasRole('INSTRUCTOR');
}

/**
 * STUDENT 권한 체크
 */
function isStudent() {
    return hasRole('STUDENT');
}
