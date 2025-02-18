document.addEventListener("DOMContentLoaded", function () {
    // 흡연 & 음주 버튼 클릭 시 hidden input 값 변경
    document.querySelectorAll(".radio-group").forEach(group => {
        const buttons = group.querySelectorAll(".radio-btn");
        const hiddenInput = group.querySelector("input[type='hidden']"); // 해당 그룹의 hidden input 가져오기

        buttons.forEach(button => {
            button.addEventListener("click", function () {
                // 같은 그룹의 모든 버튼에서 active 제거
                buttons.forEach(btn => btn.classList.remove("active"));

                // 클릭한 버튼에 active 추가
                this.classList.add("active");

                // 🚀 선택된 버튼의 값(hidden input) 설정 (data-value 사용)
                hiddenInput.value = this.getAttribute("data-value");
                //console.log(`✅ ${hiddenInput.name} 값 변경:`, hiddenInput.value);
            });
        });
    });

    // 세션에 담겨 있는 userPk 가져오기
    fetch("/user/session", {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(data => {
            if (data.userPk) {

                // 🚀 폼 제출 이벤트
                document.querySelector(".form").addEventListener("submit", function (event) {
                    event.preventDefault(); // 기본 제출 방지

                    const smokingInput = document.getElementById("smoking");
                    const drinkingInput = document.getElementById("drinking");
                    const genderInput = document.getElementById("gender");

                    const userData = {
                        age: parseInt(document.getElementById("age").value),
                        weight: document.getElementById("weight").value.trim(),
                        height: document.getElementById("height").value.trim(),
                        gender: genderInput.value.trim(), //  성별정보인 gender 추가
                        sickType: document.getElementById("disease-type").value,
                        sickDetail: document.getElementById("disease-level").value,
                        exerciseCnt: document.getElementById("exercise-frequency").value,
                        smoke: parseInt(smokingInput.value) || 0,
                        drink: parseInt(drinkingInput.value) || 0,
                        targetCount: 0,
                        userPk: data.userPk // ✅ 세션에서 가져온 userPk 사용
                    };

                    console.log("📢 서버로 보낼 데이터:", userData);

                    fetch("/user/doInsertDetail", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(userData)
                    })
                        .then(response => response.json())
                        .then(data => {

                            if (data.status === "success") {
                                alert(data.message);
                                window.location.href = "/user/mainPage";
                            } else {
                                alert("저장 실패: " + data.message);
                            }
                        })
                        .catch(error => {
                            console.error("상세정보 입력페이지 - 서버 요청 오류:", error);
                        });
                });
            } else {
                console.error("상세정보 입력페이지 - userPk 없음: 로그인 필요");
            }
        })
        .catch(error => {
            console.error("상세정보 입력페이지 - API 호출 오류:", error);
        });
});