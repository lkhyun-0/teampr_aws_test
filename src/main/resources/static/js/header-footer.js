document.addEventListener("DOMContentLoaded", function () {
    function updateHeaderMenu() {
        fetch("/user/session", { method: "GET", credentials: "include" })
            .then(response => response.json())
            .then(data => {
                let signupMenu = document.getElementById("signup-menu");
                let loginMenu = document.getElementById("login-menu");
                let myPageMenu = document.getElementById("mypage-menu");
                let logoutMenu = document.getElementById("logout-menu");

                let categoryLogin = document.getElementById("category-login");  // 로그인 링크
                let categorySignup = document.getElementById("category-signup"); // 회원가입 링크

                if (data.loggedIn) {
                    console.log("✅ 로그인 상태 감지됨! 메뉴 변경");

                    if (signupMenu) {
                        signupMenu.style.display = "none";

                    }
                    if (loginMenu) {
                        loginMenu.style.display = "none";

                    }
                    if (myPageMenu) {
                        myPageMenu.style.display = "block";
                        console.log("✅ myPageMenu 표시");
                    }
                    if (logoutMenu) {
                        logoutMenu.style.display = "block";
                        console.log("✅ logoutMenu 표시");
                    }

                    // 로그인/회원가입 -> 마이페이지/로그아웃으로 변경
                    if (categoryLogin) {
                        categoryLogin.href = "/user/myPage";
                        categoryLogin.innerText = "마이페이지";
                    }
                    if (categorySignup) {
                        categorySignup.href = "/user/logout";
                        categorySignup.innerText = "로그아웃";
                    }
                } else {
                    console.log("❌ 로그아웃 상태 감지됨!");

                    if (signupMenu) {
                        signupMenu.style.display = "block";
                        console.log("✅ signupMenu 표시");
                    }
                    if (loginMenu) {
                        loginMenu.style.display = "block";
                        console.log("✅ loginMenu 표시");
                    }
                    if (myPageMenu) {
                        myPageMenu.style.display = "none";
                    }
                    if (logoutMenu) {
                        logoutMenu.style.display = "none";
                    }

                    // 마이페이지/로그아웃 -> 로그인/회원가입으로 변경
                    if (categoryLogin) {
                        categoryLogin.href = "/user/signIn";
                        categoryLogin.innerText = "로그인";
                    }
                    if (categorySignup) {
                        categorySignup.href = "/user/signUp";
                        categorySignup.innerText = "회원가입";
                    }
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
