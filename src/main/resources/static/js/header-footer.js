document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ DOM 로드 완료");

    // 헤더 메뉴 업데이트
    function updateHeaderMenu() {
        fetch("/user/session", { method: "GET", credentials: "include" })
            .then(response => response.json())
            .then(data => {
                console.log("📌 로그인 상태 확인:", data);

                let signupMenu = document.getElementById("signup-menu");
                let loginMenu = document.getElementById("login-menu");
                let myPageMenu = document.getElementById("mypage-menu");
                let logoutMenu = document.getElementById("logout-menu");

                let loginLink = document.getElementById("category-login");
                let signupLink = document.getElementById("category-signup");

                if (data.loggedIn) {
                    console.log("✅ 로그인 상태 감지됨! 메뉴 변경");
                    console.log(`✅ 로그인된 사용자 Pk: ${data.userPk}`);

                    // 헤더 변경
                    if (signupMenu) signupMenu.style.display = "none";
                    if (loginMenu) loginMenu.style.display = "none";
                    if (myPageMenu) myPageMenu.style.display = "block";
                    if (logoutMenu) logoutMenu.style.display = "block";

                    // 카테고리 메뉴 변경 (로그인 상태)
                    loginLink.textContent = "마이페이지";
                    loginLink.href = "/user/myPage";

                    signupLink.textContent = "로그아웃";
                    signupLink.href = "#";
                    signupLink.id = "category-logout"; // ID 변경

                    // 로그아웃 버튼 이벤트 등록
                    signupLink.addEventListener("click", function (event) {
                        event.preventDefault();
                        handleLogout();
                    });
                } else {
                    console.log("❌ 로그아웃 상태 감지됨! 회원가입/로그인 표시");

                    // 헤더 변경
                    if (signupMenu) signupMenu.style.display = "block";
                    if (loginMenu) loginMenu.style.display = "block";
                    if (myPageMenu) myPageMenu.style.display = "none";
                    if (logoutMenu) logoutMenu.style.display = "none";

                    // 카테고리 메뉴 변경 (로그아웃 상태)
                    loginLink.textContent = "로그인";
                    loginLink.href = "/user/signIn";

                    signupLink.textContent = "회원가입";
                    signupLink.href = "/user/signUp";
                    signupLink.id = "category-signup"; // 원래 ID로 복구
                }
            })
            .catch(error => console.error("🚨 로그인 상태 확인 중 오류 발생:", error));
    }

    // 로그아웃 처리 함수
    function handleLogout() {
        fetch("/user/logout", { method: "GET", credentials: "include" })
            .then(response => response.json())
            .then(data => {
                alert(data.message); // ✅ "로그아웃되었습니다." 메시지 출력
                updateHeaderMenu(); // ✅ UI 업데이트
                window.location.href = data.redirect; // 🚀 메인 페이지로 이동
            })
            .catch(error => console.error("🚨 로그아웃 중 오류 발생:", error));
    }

    // ✅ 페이지 로드 시 로그인 상태 확인 및 UI 업데이트
    updateHeaderMenu();

    // ✅ 헤더 로그아웃 버튼 이벤트 등록
    let logoutBtn = document.getElementById("logout-btn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", function (event) {
            event.preventDefault();
            handleLogout();
        });
    }
});
