function joinCheck(){
	alert("test");

    //유효성 검사하기
    let fm = document.frm;

    if(fm.username.value==="") {
        alert("이름을 입력해주세요");
        fm.username.focus();
        return;
    }else if(fm.usernick.value==="") {
        alert("닉네임을 입력해주세요");
        fm.usernick.focus();
        return;
    }else if ($("#nick-btn").val() === "N") { // 닉네임 중복확인 여부 확인
        alert("닉네임 중복확인을 해주세요");
        return;
    }else if(fm.userid.value==="") {
        alert("아이디를 입력해주세요");
        fm.userid.focus();
        return;
    }else if ($("#id-btn").val() === "N") { // 아이디 중복확인 여부 확인
        alert("아이디 중복확인을 해주세요");
        return;
    }else if (fm.userpwd.value===""){
        alert("비밀번호를 입력해주세요");
        fm.userpwd.focus();
        return;
    }else if(fm.userpwd2.value==="") {
        alert("비밀번호를 확인해주세요");
        fm.userpwd2.focus();
        return;
    }else if(fm.userpwd.value !== fm.userpwd2.value) {
        alert("비밀번호가 일치하지 않습니다.");
        fm.userpwd2.value="";	// 값 초기화
        fm.userpwd2.focus();	// 포커스
        return;
    }else if(fm.phone.value==="") {
        alert("전화번호를 입력해주세요");
        fm.phone.focus();
        return;
    }else if(fm.email.value==="") {
        alert("이메일을 입력해주세요");
        fm.email.focus();
        return;
    }



// check 함수 끝

    let ans = confirm("회원가입 하시겠습니까?");

    if (ans === true){
        fm.action="/user/userJoinAction";
        fm.method="post";
        fm.submit();
    }

}


function checkUserId() {
    let userid = $("#userid").val();
    if (userid === "") {
        alert("아이디 입력란이 공란입니다.");
        return;
    } // 입력란에 아무것도 입력하지 않은 경우 다시 입력하도록 제한

    $.ajax({
        type: "post",
        url: "/user/userIdCheck",
        dataType: "json",
        data: {"userid": userid},
        success: function(result) {
            if (result.cnt === 0) {	// 사용가능한 경우
                alert("사용할 수 있는 아이디입니다.");
                $("#id-btn").val("Y");

            } else {
                alert("사용할 수 없는 아이디입니다.");
                $("#userid").val(""); // 입력한 아이디 지우기

            }
        },
        error: function() {
            alert("전송 실패");
        }
    });
}
//아이디 중복체크 완료

function checkNickname() {
    let usernick = $("#usernick").val();
    if (usernick === "") {
        alert("닉네임 입력란이 공란입니다.");
        return;
    } // 입력란에 아무것도 입력하지 않은 경우 다시 입력하도록 제한
    $.ajax({
        type: "post",
        url: "/user/userNickCheck",
        dataType: "json",
        data: {"usernick": usernick},
        success: function(result) {
            if (result.cnt === 0) {
                alert("사용할 수 있는 닉네임입니다.");
                $("#nick-btn").val("Y");

            } else {
                alert("사용할 수 없는 닉네임입니다.");
                $("#usernick").val(""); // 입력한 아이디 지우기

            }
        },
        error: function() {
            alert("전송 실패");
        }
    });
}
