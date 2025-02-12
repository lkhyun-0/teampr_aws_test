function doSignIn() {
    let signInfm = document.getElementById("signInfm");
    let userId = signInfm.userId.value.trim();
    let userPwd = signInfm.userPwd.value.trim();

    let loginData = { userId: userId, userPwd: userPwd };

    console.log("📌 로그인 요청 데이터:", loginData);

    fetch("/user/doSignIn", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            console.log("📌 응답 상태 코드:", response.status);
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.error || "서버 오류 발생");
                });
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
            console.error("로그인 요청 실패:", error);
            alert("서버 오류가 발생했습니다. 다시 시도해주세요.");
        });
}

// ✅ 3. 카카오 로그인 실행
function kakaoLogin() {
    Kakao.Auth.login({
        scope: "profile_nickname, account_email, phone_number",
        success: function (authObj) {
            console.log("✅ 카카오 로그인 성공!", authObj);

            // ✅ 4. 사용자 정보 요청 (프로필 & 이메일 가져오기)
            Kakao.API.request({
                url: "/v2/user/me",
                success: function (userInfo) {
                    console.log("📌 카카오 사용자 정보:", userInfo);

                    // 🔹 서버로 전달할 데이터 정리
                    let kakaoUser = {
                        userId: userInfo.id.toString(),
                        userNick: userInfo.properties.nickname,
                        email: userInfo.kakao_account.email || "no-email",
                        phone: userInfo.kakao_account.phone_number || "no-phone"
                    };

                    console.log("📌 서버로 보낼 카카오 유저 데이터:", kakaoUser);

                    // ✅ 5. 서버에 카카오 로그인 데이터 전송
                    fetch("/user/kakaoSignIn", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(kakaoUser)
                    })
                        .then(response => response.json())
                        .then(data => {
                            console.log("📌 서버 응답:", data);
                            if (data.success) {
                                alert("카카오 로그인 성공!");
                                window.location.href = data.redirect;
                            } else {
                                alert("로그인 실패: " + data.error);
                            }
                        })
                        .catch(error => {
                            console.error("🚨 카카오 로그인 처리 실패:", error);
                        });
                },
                fail: function (error) {
                    console.error("🚨 사용자 정보 요청 실패:", error);
                }
            });
        },
        fail: function (err) {
            console.error("🚨 카카오 로그인 실패:", err);
        }
    });
}
