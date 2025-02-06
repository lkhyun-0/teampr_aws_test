function joinCheck() {
    //alert("test");

    let fm = document.frm;

    if (fm.userName.value === "") {
        alert("이름을 입력해주세요");
        fm.userName.focus();
        return;
    } else if (fm.userNick.value === "") {
        alert("닉네임을 입력해주세요");
        fm.userNick.focus();
        return;
    } else if ($("#nick-btn").val() === "N") {  // 닉네임 중복 확인 안 했으면 막기
        alert("닉네임 중복 확인을 해주세요.");
        return;
    } else if (fm.userId.value === "") {
        alert("아이디를 입력해주세요");
        fm.userId.focus();
        return;
    } else if ($("#id-btn").val() === "N") {  // 아이디 중복 확인 안 했으면 막기
        alert("아이디 중복 확인을 해주세요.");
        fm.userId.focus();
        return;
    } else if (fm.userPwd.value === "") {
        alert("비밀번호를 입력해주세요");
        fm.userPwd.focus();
        return;
    } else if (fm.userpwd2.value === "") {
        alert("비밀번호를 확인해주세요");
        fm.userpwd2.focus();
        return;
    } else if (fm.userPwd.value !== fm.userpwd2.value) {
        alert("비밀번호가 일치하지 않습니다.");
        fm.userpwd2.value = "";
        fm.userpwd2.focus();
        return;
    } else if (fm.phone.value === "") {
        alert("전화번호를 입력해주세요");
        fm.phone.focus();
        return;
    } else if (fm.email.value === "") {
        alert("이메일을 입력해주세요");
        fm.email.focus();
        return;
    }

    let ans = confirm("회원가입 하시겠습니까?");
    if (ans) {
        let userData = {
            userName: fm.userName.value,  // ✅ 서버 DTO와 동일한 필드명 사용
            userNick: fm.userNick.value,
            userId: fm.userId.value,
            userPwd: fm.userPwd.value,
            email: fm.email.value,
            phone: fm.phone.value,
        };

        fetch("/user/userSignUp", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error("서버 오류: " + text);
                    });
                }
                return response.json();  // ✅ JSON 변환
            })
            .then(data => {
                console.log("서버 응답 데이터:", data);  // ✅ 응답 데이터 확인
                if (data.error) {
                    throw new Error(data.error);  // 오류 메시지 처리
                }
                alert(data.message);
                window.location.href = data.redirect;  // ✅ 서버에서 받은 redirect 경로로 이동
            })
            .catch(error => {
                alert("회원가입 실패: " + error.message);
            });
    }
}

// ✅ 아이디 또는 닉네임이 변경될 때 버튼 상태 초기화
document.getElementById("userId").addEventListener("input", function () {
    document.getElementById("id-btn").value = "N";
});

document.getElementById("userNick").addEventListener("input", function () {
    document.getElementById("nick-btn").value = "N";
});


function checkUserId() {
    let userId = document.getElementById("userId").value.trim();
    let idButton = document.getElementById("id-btn");
    if (!userId) {  // 아이디 입력란 비어있을 때
        alert("아이디를 입력하세요.");
        return;
    }

    fetch(`/user/checkUserId?userid=${encodeURIComponent(userId)}`)
        .then(response => response.json())
        .then(isDuplicate => {
            if (isDuplicate) {
                alert("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
                idButton.value = "N"; // 중복된 경우 다시 확인 필요
            } else {
                alert("사용 가능한 아이디입니다!");
                idButton.value = "Y"; // 사용 가능하면 "Y"로 변경
            }
        })
        .catch(error => console.error("Error:", error));
}

function checkNickname() {
    let userNick = document.getElementById("userNick").value.trim();
    let nickButton = document.getElementById("nick-btn");

    if (!userNick) {
        alert("닉네임을 입력하세요.");
        return;
    }

    fetch(`/user/checkNickname?userNick=${encodeURIComponent(userNick)}`)
        .then(response => response.json())
        .then(isDuplicate => {
            if (isDuplicate) {
                alert("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요.");
                nickButton.value = "N"; // 중복된 경우 다시 확인 필요
            } else {
                alert("사용 가능한 닉네임입니다!");
                nickButton.value = "Y"; // 사용 가능하면 "Y"로 변경
            }
        })
        .catch(error => console.error("Error:", error));
}