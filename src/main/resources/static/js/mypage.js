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
    const userPk = document.getElementById("userPk").value; // 로그인된 사용자 PK 가져오기

    // 사용자가 입력한 값 가져오기
    const updatedData = {
        userPk: userPk,
        phone: document.getElementById("modal-phone").value,
        email: document.getElementById("modal-email").value,
        password: document.getElementById("password").value,
        height: document.getElementById("height").value,
        weight: document.getElementById("weight").value,
        smoke: document.querySelector('input[name="smoke"]:checked').value, // 흡연 선택값
        drink: document.querySelector('input[name="drink"]:checked').value  // 음주 선택값
    };

    console.log("수정하기 위해 받은 데이터 :", updatedData); // 디버깅용 콘솔 로그

    // 서버에 업데이트 요청 보내기
    fetch("/detail/updateInfo", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("회원 정보가 성공적으로 수정되었습니다!");
                location.reload(); // 성공 시 페이지 새로고침
            } else {
                alert("수정 실패: " + data.message);
            }
        })
        .catch(error => console.error("[ERROR] 회원정보 업데이트 실패:", error));
}

// "수정하기" 버튼 클릭 시 업데이트 실행
document.querySelector(".save-btn").addEventListener("click", updateUserInfo);


// 그래프 스크립트
document.addEventListener("DOMContentLoaded", function () {
    const userPk = document.getElementById('userPk').value; // HTML에서 값 가져오기
    if (userPk) {
        loadGraphData(userPk);
    } else {
        console.error("로그인한 사용자의 userPk를 찾을 수 없습니다.");
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

// 게시글 보여줄 때 탭 !
$(function () {
    // 모든 탭 콘텐츠 숨기기
    $('.tabcontent > div').removeClass('active');

    // 탭 클릭 이벤트 처리
    $('.tab-bar a').click(function (e) {
        e.preventDefault(); // 기본 동작 방지

        // 모든 콘텐츠 숨기고 선택된 콘텐츠만 표시
        $('.tabcontent > div').removeClass('active');
        $($(this).attr('href')).addClass('active');

        // 활성화 클래스 처리
        $('.tab-bar a').removeClass('active');
        $(this).addClass('active');
    });

    // 첫 번째 탭 자동 활성화
    $('.tab-bar a').first().click();
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