document.addEventListener("DOMContentLoaded", function () {
    const radioButtons = document.querySelectorAll(".radio-group input[type='radio']");

    radioButtons.forEach(radio => {
        radio.addEventListener("change", function () {
            // 모든 라벨 초기화
            document.querySelectorAll(".radio-label").forEach(label => {
                label.classList.remove("active");
            });

            // 선택된 라벨에 active 스타일 적용
            const selectedLabel = document.querySelector("label[for='" + this.id + "']");
            if (selectedLabel) {
                selectedLabel.classList.add("active");
            }
        });
    });
});
// 수정하기 모달창 안에 있는 흡연 음주 버튼 (active클래스)

function updateUserInfo() {
    const userPk = document.getElementById("userPk").value;

    // 📌 users 테이블 업데이트 정보
    const userData = {
        userPk: userPk,
        phone: document.getElementById("modal-phone").value,
        email: document.getElementById("modal-email").value,

    };

    // 📌 detail 테이블 업데이트 정보
    const detailData = {
        userPk: userPk,
        height: document.getElementById("height").value,
        weight: document.getElementById("weight").value,
        smoke: Number(document.querySelector('input[name="smoke"]:checked').value),  //  숫자로 변환
        drink: Number(document.querySelector('input[name="drink"]:checked').value)  //  숫자로 변환
    };

    // 📌 통합 데이터 (UserDetailUpdateDto와 매칭)
    const updateData = {
        usersDto: userData,
        detailDto: detailData
    };

    //console.log("전송할 데이터:", updateData); // 디버깅용

    fetch("/detail/updateInfo", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updateData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("회원 정보가 성공적으로 수정되었습니다.");
                location.reload();      // 새로고침
            } else {
                alert("수정 실패: " + data.message);
            }
        })
        .catch(error => console.error("[ERROR] 회원정보 업데이트 실패:", error));
}

// "수정하기" 버튼 클릭 시 업데이트 실행
//document.querySelector(".save-btn").addEventListener("click", updateUserInfo);

// 비번 변경 !
function modifyUserPwd() {
    const userPk = document.getElementById("modal-userPk").value;

    if (!userPk) {
        alert("회원 정보가 없습니다. 다시 로그인해 주세요.");
        return;
    }

    let newPwd = prompt("새 비밀번호를 입력하세요:");

    if (newPwd === null || newPwd.trim() === "") {
        alert("비밀번호 변경이 취소되었습니다.");
        return;
    }

    fetch('/user/modifyUserPwd', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userPk: userPk, newPassword: newPwd }) // ✅ userPk를 함께 전송
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("비밀번호가 변경되었습니다.");
            } else {
                alert("비밀번호 변경 실패: " + data.message);
            }
        })
        .catch(error => console.error("에러 발생:", error));
}

// 프로필사진 업로드 !
function openFileInput() {
    document.getElementById("profile-upload").click();
}
document.getElementById("profile-upload").addEventListener("change", function (event) {
    let file = event.target.files[0];

    if (file) {
        let formData = new FormData();
        formData.append("profileImage", file);

        fetch("/detail/uploadProfileImage", {
            method: "POST",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 새 프로필 이미지 적용
                    let profileImage = document.getElementById("profile-image");
                    profileImage.src = data.imagePath;
                    profileImage.classList.remove("hidden");

                    // 기본 프로필 아이콘 숨기기
                    document.getElementById("profile-icon").classList.add("hidden");

                    console.log("프로필 이미지 변경됨:", data.imagePath);
                } else {
                    alert("업로드 실패: " + data.message);
                }
            })
            .catch(error => console.error("에러 발생:", error));
    }
});










// 그래프 스크립트
document.addEventListener("DOMContentLoaded", function () {
    const userPk = document.getElementById('userPk').value; // HTML에서 값 가져오기
    if (userPk) {
        loadGraphData(userPk);
    } else {
        console.error("로그인한 사용자의 정보를 찾을 수 없습니다.");
    }
});

function loadGraphData(userPk) {
    fetch(`/graph/${userPk}`)
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) {
                document.getElementById('progressChart').style.display = 'none';
                document.getElementById('noDataMessage').style.display = 'block';
                return;
            }

            document.getElementById('progressChart').style.display = 'block';
            document.getElementById('noDataMessage').style.display = 'none';

            const labels = data.map(entry => new Date(entry.regDate).toLocaleDateString());
            const weights = data.map(entry => entry.weight);
            const bloodPressures = data.map(entry => entry.bloodPress);
            const bloodSugars = data.map(entry => entry.bloodSugar);
            console.log("날짜 원본 데이터>>>>>>>>>>>>>>>>>>>>>>>", labels);
            const ctx = document.getElementById('progressChart').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: '몸무게 (kg)',
                            data: weights,
                            borderColor: 'rgba(54, 162, 235, 1)',
                            backgroundColor: 'rgba(54, 162, 235, 0.2)',
                            borderWidth: 2,
                            pointRadius: 5,
                            pointHoverRadius: 8,
                            tension: 0.4
                        },
                        {
                            label: '혈압',
                            data: bloodPressures,
                            borderColor: 'rgba(255, 99, 132, 1)',
                            backgroundColor: 'rgba(255, 99, 132, 0.2)',
                            borderWidth: 2,
                            pointRadius: 5,
                            pointHoverRadius: 8,
                            tension: 0.4
                        },
                        {
                            label: '혈당',
                            data: bloodSugars,
                            borderColor: 'rgba(75, 192, 192, 1)',
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            borderWidth: 2,
                            pointRadius: 5,
                            pointHoverRadius: 8,
                            tension: 0.4
                        }
                    ]
                },
                options: {
                    responsive: true,
                    scales: {
                        x: { title: { display: true, text: '날짜' } },
                        y: { title: { display: true, text: '수치' } }
                    }
                }
            });
        })
        .catch(error => console.error('Error fetching graph data:', error));
}

$(document).ready(function () {
    // 모든 탭 내용 숨기고 자유 게시판만 표시
    $('.tabcontent .tab-pane').removeClass('active').hide();
    $('#free-list').addClass('active').show(); // 자유 게시판 기본 표시
    $('.tab-bar li:first-child a').addClass('active'); // 첫 번째 탭 활성화

    // 탭 클릭 이벤트 처리
    $('.tab-bar a').on('click', function (e) {
        e.preventDefault(); // 기본 동작 방지

        let targetTab = $(this).attr('href'); // 클릭한 탭의 ID 가져오기

        // 모든 탭 숨기고 해당 탭만 표시
        $('.tabcontent .tab-pane').removeClass('active').hide();
        $(targetTab).addClass('active').fadeIn(200); // 부드럽게 표시

        // 탭 활성화 스타일 적용
        $('.tab-bar a').removeClass('active');
        $(this).addClass('active');
    });

    // 디버깅: 데이터 확인
    console.log("자유 게시판 데이터 개수:", $('#free-list tbody tr').length);
    console.log("Q&A 게시판 데이터 개수:", $('#qna-list tbody tr').length);

    // 자유 게시판 테이블이 숨겨져 있는지 확인 후 표시
    if ($('#free-list tbody tr').length > 0) {
        $('#free-list').addClass('active').show();
    } else {
        console.warn("🚨 자유 게시판에 데이터가 있음에도 보이지 않음! CSS 확인 필요");
    }
});






// 회원정보 열기 닫기 (수정하기 위한 모달 창)
// 모달 열기/닫기 기능
document.addEventListener("DOMContentLoaded", function () {

    // 버튼과 모달 요소 가져오기
    const openModalBtn = document.getElementById("openModal");
    const modal = document.getElementById("modal");

    const closeModalBtn = document.querySelector(".close");

    // 모달 열기
    openModalBtn.addEventListener("click", function () {
        modal.style.display = "block";
    });

    // 모달 닫기 (X 버튼)
    closeModalBtn.addEventListener("click", function () {
        modal.style.display = "none";
    });

    // 모달 닫기 (배경 클릭)
    window.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
});

// 수정하기 모달에서 탈퇴버튼 클릭시 알럿창 탈퇴하시겠습니까? 확인 or 취소 확인 하면 > 회원 탈퇴시키기
function deleteUser() {
    if (!confirm("정말로 탈퇴하시겠습니까?")) {
        return;
    }

    fetch("/user/deleteUser", {
        method: "POST",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    alert(err.error || "서버 오류 발생");
                    return null;
                });
            }
            return response.json();
        })
        .then(data => {
            if (!data) return;

            alert(data.message);
            if (data.redirect) {
                window.location.href = data.redirect;  // 로그인 페이지로 이동
            }
        })
        .catch(error => {
            console.error("회원 탈퇴 요청 실패:", error);
            alert("서버 오류가 발생했습니다. 다시 시도해주세요.");
        });
}