
// 일반 로그인 동작 스크립트
function doSignIn() {
    let signInfm = document.getElementById("signInfm");  // ✅ `id`로 폼 가져오기

    // 1️⃣ 유효성 검사
    let userId = signInfm.userId.value.trim();
    let userPwd = signInfm.userPwd.value.trim();

    if (userId === "") {
        alert("아이디를 입력해주세요");
        signInfm.userId.focus();
        return;
    }
    if (userPwd === "") {
        alert("비밀번호를 입력해주세요");
        signInfm.userPwd.focus();
        return;
    }

    let loginData = { userId: userId, userPwd: userPwd };

    console.log("📌 로그인 요청 데이터:", loginData); // ✅ 클라이언트에서 확인

    fetch("/user/doSignIn", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData)  // ✅ JSON 형식으로 변환하여 전송
    })
        .then(response => response.json())
        .then(data => {
            console.log("📌 서버 응답 데이터:", data);  // ✅ 서버 응답 확인
            if (data.success) {
                alert(data.message);
                window.location.href = data.redirect;
            } else {
                alert(data.error);
            }
        })
        .catch(error => {
            console.error("🚨 로그인 요청 실패:", error);
            alert("서버 오류가 발생했습니다. 다시 시도해주세요.");
        });
}


// 카카오 로그인 동작 스크립트
function kakaoLogin() {
    document.getElementById("kakao-login-btn").addEventListener("click", function () {
        let clientId = "08c634745a5865601618fca8418a8d9e"; // 카카오에서 발급받은 REST API 키
        let redirectUri = "http://localhost:8081/user/kakao/callback"; // 백엔드 콜백 URL

        let kakaoAuthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code`;

        window.location.href = kakaoAuthUrl; // 카카오 로그인 페이지로 이동
    });
}
