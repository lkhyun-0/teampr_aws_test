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
    } /*else if ($("#nick-btn").prop("value") === "N") {
        alert("닉네임 중복확인을 해주세요");
        return;
    }*/ else if (fm.userid.value === "") {
        alert("아이디를 입력해주세요");
        fm.userid.focus();
        return;
    } /*else if ($("#id-btn").prop("value") === "N") {
        alert("아이디 중복확인을 해주세요");
        return;
    } */else if (fm.userpwd.value === "") {
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
            phone: fm.phone.value
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
                window.location.href = "/user/userLogin";
            })
            .catch(error => {
               alert("회원가입 실패 : " + error.message);
            });
    }
}

/*function checkUserId() {
    let userid = $("#userid").val();
    if (userid === "") {
        alert("아이디 입력란이 공란입니다.");
        return;
    }

    $.ajax({
        type: "post",
        url: "/user/userIdCheck",
        dataType: "json",
        data: { "userid": userid },
        success: function (result) {
            if (result.cnt === 0) {
                alert("사용할 수 있는 아이디입니다.");
                $("#id-btn").prop("value", "Y");
            } else {
                alert("사용할 수 없는 아이디입니다.");
                $("#userid").val("");
            }
        },
        error: function () {
            alert("전송 실패");
        }
    });
}

function checkNickname() {
    let usernick = $("#usernick").val();
    if (usernick === "") {
        alert("닉네임 입력란이 공란입니다.");
        return;
    }

    $.ajax({
        type: "post",
        url: "/user/userNickCheck",
        dataType: "json",
        data: { "usernick": usernick },
        success: function (result) {
            if (result.cnt === 0) {
                alert("사용할 수 있는 닉네임입니다.");
                $("#nick-btn").prop("value", "Y");
            } else {
                alert("사용할 수 없는 닉네임입니다.");
                $("#usernick").val("");
            }
        },
        error: function () {
            alert("전송 실패");
        }
    });
}*/
