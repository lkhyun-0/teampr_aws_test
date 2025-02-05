function joinCheck() {
    //alert("test");

    let fm = document.frm;

    if (fm.username.value === "") {
        alert("이름을 입력해주세요");
        fm.username.focus();
        return;
    } else if (fm.usernick.value === "") {
        alert("닉네임을 입력해주세요");
        fm.usernick.focus();
        return;
    } else if ($("#nick-btn").val() === "N") {  // 닉네임 중복 확인 안 했으면 막기
        alert("닉네임 중복 확인을 해주세요.");
        return;
    } else if (fm.userid.value === "") {
        alert("아이디를 입력해주세요");
        fm.userid.focus();
        return;
    } else if ($("#id-btn").val() === "N") {  // 아이디 중복 확인 안 했으면 막기
        alert("아이디 중복 확인을 해주세요.");
        return;
    } else if (fm.userpwd.value === "") {
        alert("비밀번호를 입력해주세요");
        fm.userpwd.focus();
        return;
    } else if (fm.userpwd2.value === "") {
        alert("비밀번호를 확인해주세요");
        fm.userpwd2.focus();
        return;
    } else if (fm.userpwd.value !== fm.userpwd2.value) {
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
            username: fm.username.value,
            usernick: fm.usernick.value,
            userid: fm.userid.value,
            userpwd: fm.userpwd.value,
            email: fm.email.value,
            phone: fm.phone.value,
            /*            auth_level: fm.auth_level.value,
                        social_login_status: fm.social_login_status.value,
                        del_status: fm.del_status.value*/

        };

        fetch("/user/userSignUp", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
            .then(response => response.json())
            .then(data => {
                alert("회원가입 성공!");
                window.location.href = "/user/userDetail";
            })
            .catch(error => {
                alert("회원가입 실패 : " + error.message);
            });
    }
}

// ✅ 아이디 또는 닉네임이 변경될 때 버튼 상태 초기화
document.getElementById("userid").addEventListener("input", function () {
    document.getElementById("id-btn").value = "N";
});

document.getElementById("usernick").addEventListener("input", function () {
    document.getElementById("nick-btn").value = "N";
});


function checkUserId() {
    let userid = document.getElementById("userid").value.trim();
    let idButton = document.getElementById("id-btn");
    if (!userid) {  // 아이디 입력란 비어있을 때
        alert("아이디를 입력하세요.");
        return;
    }

    fetch(`/user/checkUserId?userid=${encodeURIComponent(userid)}`)
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
    let usernick = document.getElementById("usernick").value.trim();
    let nickButton = document.getElementById("nick-btn");

    if (!usernick) {
        alert("닉네임을 입력하세요.");
        return;
    }

    fetch(`/user/checkNickname?usernick=${encodeURIComponent(usernick)}`)
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