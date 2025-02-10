function doSignIn() {
    let signInfm = document.getElementById("signInfm");

    let userId = signInfm.userId.value.trim();
    let userPwd = signInfm.userPwd.value.trim();

    if (userId === "" || userPwd === "") {
        alert("아이디와 비밀번호를 입력해주세요.");
        return;
    }

    let loginData = { userId: userId, userPwd: userPwd };

    console.log("📌 로그인 요청 데이터:", loginData);

    fetch("/user/doSignIn", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            if (!response.ok) {
                console.error("🚨 서버 응답 오류:", response.status);
                return response.text(); // ✅ JSON이 아니면 그대로 출력
            }
            return response.json();
        })
        .then(data => {
            console.log("📌 서버 응답 데이터:", data);
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

// 카카오 로그인 동작 스크립트 수정
function kakaoLogin() {
    fetch("/login/kakao/auth-url")
        .then(response => response.json()) // ✅ JSON으로 변환
        .then(data => {
            window.location.href = data.kakaoAuthUrl; // ✅ URL로 이동
        })
        .catch(error => console.error("카카오 로그인 URL 요청 실패:", error));

}

