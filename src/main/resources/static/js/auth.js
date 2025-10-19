/**
 * 인증 관련 공통 함수
 * 세션 기반 인증 (쿠키 자동 전송)
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
async function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        try {
            await fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            });
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            location.href = '/';
        }
    }
}

/**
 * 현재 사용자 정보 가져오기 (서버에서 조회)
 */
async function getCurrentUser() {
    try {
        const response = await fetch('/api/auth/me', {
            credentials: 'include'
        });
        if (response.ok) {
            const result = await response.json();
            return result.data;
        }
    } catch (error) {
        console.error('Get current user error:', error);
    }
    return null;
}

/**
 * 로그인 필수 체크
 * 세션 기반이므로 서버가 자동으로 인증 체크
 * 페이지 접근 시 Spring Security가 자동으로 리다이렉트
 */
function requireLogin() {
    // 세션 기반이므로 클라이언트에서 체크 불필요
    // 서버에서 자동으로 인증 확인
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
