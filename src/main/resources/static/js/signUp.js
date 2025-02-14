function signUp() {
    let signUpfm = document.forms["signUpfm"];

    if (document.getElementById("nick-btn").value === "N") {
        alert("닉네임 중복 확인을 해주세요.");
        return;
    }
    if (document.getElementById("id-btn").value === "N") {
        alert("아이디 중복 확인을 해주세요.");
        signUpfm.userId.focus();
        return;
    }
    if (signUpfm.userpwd2.value === "") {
        alert("비밀번호를 확인해주세요");
        signUpfm.userpwd2.focus();
        return;
    }
    if (signUpfm.userPwd.value !== signUpfm.userpwd2.value) {
        alert("비밀번호가 일치하지 않습니다.");
        signUpfm.userpwd2.value = "";
        signUpfm.userpwd2.focus();
        return;
    }

    let ans = confirm("회원가입 하시겠습니까?");
    if (ans) {
        let userData = {
            userName: signUpfm.userName.value,
            userNick: signUpfm.userNick.value,
            userId: signUpfm.userId.value,
            userPwd: signUpfm.userPwd.value,
            email: signUpfm.email.value,
            phone: signUpfm.phone.value
        };

        fetch("/user/dosignUp", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    throw new Error(data.error);
                }
                alert(data.message);
                window.location.href = data.redirect;
            })
            .catch(error => {
                console.error("회원가입 실패:", error);
                alert("회원가입 실패: " + error.message);
            });
    }
}

function checkUserId() {
    let userId = document.getElementById("userId").value.trim();
    let idButton = document.getElementById("id-btn");
    if (!userId) {  // 아이디 입력란 비어있을 때
        alert("아이디를 입력하세요.");
        return;
    }

    fetch(`/user/checkUserId?userId=${encodeURIComponent(userId)}`)

        .then(response => response.json())
        .then(isDuplicate => {
            console.log(isDuplicate + "중복확인 값 확인");
            if (isDuplicate) {
                alert("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
                idButton.value = "N"; // 중복된 경우 다시 확인 필요

            } else {
                alert("사용 가능한 아이디입니다!");
                idButton.value = "Y"; // 사용 가능하면 "Y"로 변경
            }
        })
        .catch(error => console.error("Error:", error));
}// 회원가입시에 아이디 중복체크 하는 스크립트

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
}// 회원가입시 닉네임 중복체크 하는 스크립트

// ✅ 아이디 또는 닉네임이 변경될 때 버튼 상태 초기화
document.getElementById("userId").addEventListener("input", function () {
    document.getElementById("id-btn").value = "N";
});
// 이벤트 감지해서 닉네임 중복체크 하고난 뒤에 변경사항이 있으면 다시 중복체크를 시도하도록 제한

document.getElementById("userNick").addEventListener("input", function () {
    document.getElementById("nick-btn").value = "N";
});

