// 전역에서 접근 가능한 userPk 변수
let globalUserPk = null;


document.addEventListener("DOMContentLoaded", function () {
    // HTML 요소에서 userPk 값 가져오기 (data-user-pk 속성 활용)
    const userDataEl = document.getElementById("userData");
    const userPk = userDataEl ? userDataEl.dataset.userPk : null;
    console.log("UserPk:", userPk);

    // 기능별 초기화 함수 호출
    setupPopupHandlers();
    initializeCalendar();
    setupExerciseRecording(userPk);
    setupGraphSaving(userPk);
    setupTargetSettings(userPk);
});

/* =========================================================
   1. 팝업 핸들러 설정 (jQuery 사용)
   ========================================================= */
function setupPopupHandlers() {
    // 목표 작성 팝업
    $('.target-write-btn').click(function () {
        console.log("target-write-btn clicked");
        $('.target-popup').css('display', 'flex');
    });
    $('.target-popup-close-btn').click(function () {
        $('.target-popup').css('display', 'none');
    });
    $(".target-popup").on("click", function () {
        $(this).hide();
    });
    $(".target-popup-content").on("click", function (event) {
        event.stopPropagation();
    });

    // 운동 종목 선택 팝업
    $('.exercise-type-popup-btn').click(function () {
        $('.exercise-type-popup').css('display', 'flex');
    });
    $('.exercise-type-popup-close-btn').click(function () {
        $('.exercise-type-popup').css('display', 'none');
    });
    $(".exercise-type-popup").on("click", function () {
        $(this).hide();
    });
    $(".exercise-type-popup-content").on("click", function (event) {
        event.stopPropagation();
    });
}

/* =========================================================
   2. FullCalendar 초기화
   ========================================================= */
function initializeCalendar() {
    const calendarEl = document.getElementById('calendar');
    if (!calendarEl) return;
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridWeek',
        height: 'auto',
        dayMaxEvents: 3,
        contentHeight: 'auto',
        headerToolbar: {
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        views: {
            dayGridMonth: { titleFormat: { year: 'numeric', month: '2-digit' } },
            dayGridWeek: { titleFormat: { year: 'numeric', month: '2-digit' } }
        },
        dayCellDidMount: function (info) {
            let cell = info.el;
            let existingDateEl = cell.querySelector('.fc-daygrid-day-number');
            if (existingDateEl) existingDateEl.remove();
            let dateEl = document.createElement('div');
            dateEl.classList.add('fc-daygrid-day-number');
            dateEl.innerText = info.date.getDate();
            dateEl.style.position = 'absolute';
            dateEl.style.top = '8px';
            dateEl.style.right = '8px';
            dateEl.style.fontSize = '14px';
            dateEl.style.fontWeight = 'bold';
            dateEl.style.zIndex = '10';
            // 요일에 따른 색상 설정 (일: 빨강, 토: 파랑)
            let dayOfWeek = info.date.getDay();
            dateEl.style.color = dayOfWeek === 0 ? 'red' : dayOfWeek === 6 ? 'blue' : 'black';
            cell.appendChild(dateEl);
        },
        dayHeaderFormat: { weekday: 'short' },
        datesSet: function (info) {
            const rawTitle = info.view.title;
            const parts = rawTitle.split('/');
            let year = parts.length === 2 ? parts[1].trim() : 'Unknown Year';
            let month = parts.length === 2 ? parts[0].trim() : 'Unknown Month';
            const titleEl = document.querySelector('.fc-toolbar-title');
            if (titleEl) {
                titleEl.innerHTML = `
          <div class="calendar-title">
            <div class="calendar-year">${year}</div>
            <div class="calendar-month">${month}</div>
          </div>
        `;
            }
            // 날짜 요소 재생성 (딜레이 후)
            setTimeout(() => {
                document.querySelectorAll('.fc-daygrid-day').forEach(cell => {
                    let existingDateEl = cell.querySelector('.fc-daygrid-day-number');
                    if (existingDateEl) existingDateEl.remove();
                    let dateEl = document.createElement('div');
                    dateEl.classList.add('fc-daygrid-day-number');
                    let date = new Date(cell.getAttribute('data-date')).getDate();
                    dateEl.innerText = date;
                    dateEl.style.position = 'absolute';
                    dateEl.style.top = '8px';
                    dateEl.style.right = '8px';
                    dateEl.style.fontSize = '14px';
                    dateEl.style.fontWeight = 'bold';
                    dateEl.style.zIndex = '10';
                    dateEl.style.color = 'black';
                    cell.appendChild(dateEl);
                });
            }, 50);
        },
        // 기본적으로 서버의 이벤트를 로드 (JSON 포맷 필요)
        events: '/exercise/getExerciseEvents',
        eventClick: function (info) {
            showEventPopup(info.event);
            eventSave(info.event);
        }
    });

    // 추가적으로 운동 이벤트를 직접 fetch해 추가 (선택사항)
    fetch('/exercise/getExerciseEvents')
        .then(response => response.json())
        .then(exerciseList => {
            exerciseList.forEach(exercise => {
                calendar.addEvent({
                    title: exercise.exerciseName,
                    start: exercise.regDate,
                    extendedProps: {
                        hour: exercise.hour,
                        minute: exercise.minute,
                        kcal: exercise.kcal,
                        exercisePk: exercise.exercisePk
                    },
                    color: "#9EDE9EFF"
                });
            });
        })
        .catch(error => console.error("Error loading exercises:", error));

    calendar.render();
}

/* =========================================================
   3. 운동 기록하기 기능
   ========================================================= */
function setupExerciseRecording(userPk) {
    const exercisePopup = document.querySelector(".exercise-type-popup");
    const exerciseTypeBtn = document.querySelector(".exercise-type-popup-btn");
    const exerciseTypeSaveBtn = document.querySelector(".exercise-type-save-btn");
    const exerciseTypeDisplay = document.querySelector("#selected-exercise");
    const exerciseTimeHour = document.querySelector("#hour");
    const exerciseTimeMinute = document.querySelector("#minute");
    const saveExerciseBtn = document.querySelector(".exercise-save");
    const exerciseSearchInput = document.querySelector("#exercise-search");
    const exerciseResetBtn = document.querySelector("#reset-exercise-btn");

    let selectedExercise = "";
    let selectedMet = 0;
    let exerciseList = [];

    // 오늘의 운동 기록 여부 확인
    fetch(`/exercise/has-today-exercise?userPk=${userPk}`)
        .then(response => response.json())
        .then(hasData => {
            console.log("Today's exercise data exists:", hasData);
        })
        .catch(error => console.error("Error checking today's exercise data:", error));

    // 운동 목록 API 호출
    fetch('/exercise/apiList')
        .then(response => response.json())
        .then(data => {
            exerciseList = data;
            renderExerciseList("");
        })
        .catch(error => console.error("Error fetching exercises:", error));

    function renderExerciseList(searchTerm) {
        const exerciseContainer = document.querySelector(".exercise-type-popup__body");
        if (!exerciseContainer) return;
        exerciseContainer.innerHTML = "";
        const filteredExercises = exerciseList.filter(exercise =>
            exercise.exerciseName.includes(searchTerm)
        );
        filteredExercises.forEach(exercise => {
            const exerciseDiv = document.createElement("div");
            exerciseDiv.classList.add("exercise-item");
            exerciseDiv.innerHTML = `
        <div>${exercise.exerciseName}</div>
        <input type="hidden" value="${exercise.metValue}">
      `;
            exerciseDiv.dataset.exerciseName = exercise.exerciseName;
            exerciseDiv.dataset.metValue = exercise.metValue;
            exerciseContainer.appendChild(exerciseDiv);
        });
    }

    if (exerciseSearchInput) {
        exerciseSearchInput.addEventListener("input", function () {
            renderExerciseList(this.value.trim());
        });
    }

    const exerciseTypePopupBody = document.querySelector(".exercise-type-popup__body");
    if (exerciseTypePopupBody) {
        exerciseTypePopupBody.addEventListener("click", function (event) {
            const selectedDiv = event.target.closest(".exercise-item");
            if (!selectedDiv) return;
            selectedExercise = selectedDiv.dataset.exerciseName;
            selectedMet = selectedDiv.dataset.metValue;
            document.querySelectorAll(".exercise-item").forEach(div => div.classList.remove("selected"));
            selectedDiv.classList.add("selected");
        });
    }

    if (exerciseTypeSaveBtn) {
        exerciseTypeSaveBtn.addEventListener("click", function () {
            if (!selectedExercise) {
                alert("운동을 선택해주세요!");
                return;
            }
            exerciseTypeBtn.style.display = "none";
            exerciseTypeDisplay.style.display = "block";
            exerciseTypeDisplay.textContent = selectedExercise;
            // 선택한 값을 hidden input에 저장 (서버 전송용)
            const selectedExerciseNameInput = document.querySelector("#selected-exercise-name");
            const selectedExerciseMetInput = document.querySelector("#selected-exercise-met");
            if (selectedExerciseNameInput) selectedExerciseNameInput.value = selectedExercise;
            if (selectedExerciseMetInput) selectedExerciseMetInput.value = selectedMet;
            if (exercisePopup) exercisePopup.style.display = "none";
            if (exerciseResetBtn) exerciseResetBtn.style.display = "flex";
        });
    }

    const resetExerciseBtn = document.getElementById("reset-exercise-btn");
    if (resetExerciseBtn) {
        resetExerciseBtn.addEventListener("click", function () {
            selectedExercise = null;
            selectedMet = null;
            document.querySelectorAll(".exercise-item").forEach(div => div.classList.remove("selected"));
            if (exerciseTypeBtn) exerciseTypeBtn.style.display = "block";
            if (exerciseTypeDisplay) {
                exerciseTypeDisplay.style.display = "none";
                exerciseTypeDisplay.textContent = "";
            }
            const selectedExerciseNameInput = document.querySelector("#selected-exercise-name");
            const selectedExerciseMetInput = document.querySelector("#selected-exercise-met");
            if (selectedExerciseNameInput) selectedExerciseNameInput.value = "";
            if (selectedExerciseMetInput) selectedExerciseMetInput.value = "";
            resetExerciseBtn.style.display = "none";
            console.log("운동 종목 선택이 초기화되었습니다.");
        });
    }

    if (saveExerciseBtn) {
        saveExerciseBtn.addEventListener("click", function (event) {
            event.preventDefault();
            if (!selectedExercise) {
                alert("운동을 선택해주세요!");
                return;
            }
            let hour = parseInt(exerciseTimeHour.value, 10);
            let minute = parseInt(exerciseTimeMinute.value, 10);
            let today = new Date()
                .toLocaleDateString("ko-KR")
                .replace(/. /g, "-")
                .replace(".", "");
            const exerciseData = {
                exerciseName: selectedExercise,
                metValue: selectedMet,
                hour: hour,
                minute: minute,
                regDate: today
            };
            fetch('/exercise/saveExercise', {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(exerciseData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then(result => {
                    alert(result.message);
                    // 주간 목표 달성 수 업데이트
                    fetch(`/target/count?userPk=${userPk}`)
                        .then(response => response.json())
                        .then(count => {
                            const weeklyGoalCountEl = document.getElementById("weekly-goal-count");
                            if (weeklyGoalCountEl) weeklyGoalCountEl.textContent = `${count}회`;
                        })
                        .catch(error => console.error("Error fetching weekly goal count:", error));
                    if (exerciseTypeDisplay) {
                        exerciseTypeDisplay.textContent = "";
                        exerciseTypeDisplay.style.display = "none";
                    }
                    if (exerciseTypeBtn) exerciseTypeBtn.style.display = "inline-block";
                    if (exerciseTimeHour) exerciseTimeHour.value = "00";
                    if (exerciseTimeMinute) exerciseTimeMinute.value = "00";
                    localStorage.setItem("scrollToCalendar", "true");
                    setTimeout(() => {
                        window.location.reload();
                    }, 100);
                })
                .catch(error => console.error("Error saving exercise:", error));
        });
    }
}

/* =========================================================
   4. 그래프 저장 기능
   ========================================================= */
function setupGraphSaving(userPk) {
    const saveForm = document.getElementById("graph-form");
    if (!saveForm) return;
    let hasTodayData = false;
    fetch(`/graph/has-today-graph?userPk=${userPk}`)
        .then(response => response.json())
        .then(hasData => {
            hasTodayData = hasData;
            console.log("Has Today Graph Data:", hasData);
        })
        .catch(error => console.error("Error checking today's graph data:", error));

    saveForm.addEventListener("submit", function (event) {
        event.preventDefault();
        const bloodSugarInput = document.getElementById("blood_sugar");
        const bloodPressInput = document.getElementById("blood_press");
        const weightInput = document.getElementById("weight");
        const bloodSugar = bloodSugarInput.value.trim();
        const bloodPress = bloodPressInput.value.trim();
        const weight = weightInput.value.trim();

        if (!bloodSugar && !bloodPress && !weight) {
            alert("수치를 한 개 이상 입력해주세요.");
            if (!bloodSugar) bloodSugarInput.focus();
            return;
        }

        const data = {
            userPk: userPk,
            bloodSugar: bloodSugar || null,
            bloodPress: bloodPress || null,
            weight: weight || null
        };
        const url = hasTodayData ? '/graph/updateGraph' : '/graph/saveGraph';

        fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(response => {
                console.log("서버 응답 코드:", response.status);
                return response.json();
            })
            .then(result => {
                alert(hasTodayData ? "오늘의 수치가 업데이트되었습니다." : "오늘의 수치가 저장되었습니다.");
                bloodSugarInput.value = "";
                bloodPressInput.value = "";
                weightInput.value = "";
                window.location.reload();
            })
            .catch(error => console.error("Error saving graph:", error));
    });
}

/* =========================================================
   5. 목표(타깃) 설정 기능
   ========================================================= */
function setupTargetSettings(userPk) {
    const targetWriteBtn = document.querySelector(".target-write-btn");

    // 운동 기록 횟수 업데이트
    fetch(`/exercise/count?userPk=${userPk}`)
        .then(response => response.json())
        .then(count => {
            const exerciseCountEl = document.getElementById("exercise-count");
            if (exerciseCountEl) exerciseCountEl.textContent = `${count}회`;
        })
        .catch(error => console.error("Error fetching exercise count:", error));

    // 주간 목표 달성 수 업데이트
    fetch(`/target/count?userPk=${userPk}`)
        .then(response => response.json())
        .then(count => {
            const weeklyGoalCountEl = document.getElementById("weekly-goal-count");
            if (weeklyGoalCountEl) weeklyGoalCountEl.textContent = `${count}회`;
        })
        .catch(error => console.error("Error fetching weekly goal count:", error));

    // 이번 주 목표 데이터 가져오기 및 UI 업데이트
    fetch(`/target/current-week?userPk=${userPk}`)
        .then(response => response.json())
        .then(data => {
            if (!data) return;
            if (targetWriteBtn) targetWriteBtn.disabled = true;
            document.querySelector(".target-1 .current").textContent = `${data.exerciseCount.toLocaleString()}회`;
            document.querySelector(".target-1 .goal").textContent = `${data.exerciseTarget.toLocaleString()}회`;
            document.querySelector(".target-2 .current").textContent = `${data.valueCount.toLocaleString()}회`;
            document.querySelector(".target-2 .goal").textContent = `${data.valueTarget.toLocaleString()}회`;
            document.querySelector(".target-3 .current").textContent = `${data.kcalSum.toLocaleString()}kcal`;
            document.querySelector(".target-3 .goal").textContent = `${data.kcalTarget.toLocaleString()}kcal`;
            updateProgress(".target-1 .target-progress", data.exerciseCount, data.exerciseTarget);
            updateProgress(".target-2 .target-progress", data.valueCount, data.valueTarget);
            updateProgress(".target-3 .target-progress", data.kcalSum, data.kcalTarget);
            toggleSuccessStamp(".target-1 .success-stamp", data.exerciseCount, data.exerciseTarget);
            toggleSuccessStamp(".target-2 .success-stamp", data.valueCount, data.valueTarget);
            toggleSuccessStamp(".target-3 .success-stamp", data.kcalSum, data.kcalTarget);
            checkAllGoalsCompleted(userPk, data);
        })
        .catch(error => console.error("Error loading target data:", error));

    // 목표 저장 폼 제출 처리
    const targetForm = document.getElementById("target-form");
    if (targetForm) {
        targetForm.addEventListener("submit", function targetSaveCheck(event) {
            event.preventDefault();
            const exerciseTargetInput = document.querySelector("[name='exercise_target']");
            const valueTargetInput = document.querySelector("[name='value_target']");
            const kcalTargetInput = document.querySelector("[name='kcal_target']");
            const exerciseTarget = exerciseTargetInput.value.trim();
            const valueTarget = valueTargetInput.value.trim();
            const kcalTarget = kcalTargetInput.value.trim();
            if (!exerciseTarget && !valueTarget && !kcalTarget) {
                alert("목표를 한 개 이상 입력해주세요.");
                if (!exerciseTarget) {
                    exerciseTargetInput.focus();
                } else if (!valueTarget) {
                    valueTargetInput.focus();
                } else {
                    kcalTargetInput.focus();
                }
                return;
            }
            const data = {
                userPk: userPk,
                exerciseTarget: exerciseTarget || null,
                valueTarget: valueTarget || null,
                kcalTarget: kcalTarget || null
            };
            fetch('/target/saveTarget', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(result => {
                    $("#loading-screen").fadeIn();
                    setTimeout(() => {
                        $("#loading-screen").fadeOut();
                        alert(result.message);
                        exerciseTargetInput.value = "";
                        valueTargetInput.value = "";
                        kcalTargetInput.value = "";
                        window.location.reload();
                    }, 5000);
                })
                .catch(error => console.error("Error saving target:", error));
        });
    }
}

/* =========================================================
   6. 헬퍼 함수들
   ========================================================= */
function showEventPopup(event) {
    const modal = document.getElementById("event-modal");
    const modalTitle = document.getElementById("modal-title");
    const modalContent = document.getElementById("modal-content");
    const eventDate = event.start.toISOString().split('T')[0];
    const { minute, hour, kcal } = event.extendedProps;
    modalTitle.innerText = `${eventDate}`;
    modalContent.innerHTML = `
    <p><strong>운동명:</strong> <span>${event.title}</span></p>
    <p><strong>운동 시간:</strong> <span>${hour}시간 ${minute}분</span></p>
    <p><strong>소모 칼로리:</strong> <span>${kcal} kcal</span></p>
  `;
    modal.style.display = "flex";
}

function eventSave(event) {
    const exercisePk = event.extendedProps.exercisePk;
    if (exercisePk === 0) {
        console.error("Invalid exercise ID");
        return;
    }
    $(".exercise-delete-btn").off("click").on("click", function () {
        if (!confirm("정말 삭제하시겠습니까?")) {
            console.log("삭제 취소됨");
            return;
        }
        fetch(`/exercise/deleteExercise?exercisePk=${exercisePk}`)
            .then(response => response.json())
            .then(result => {
                $("#loading-screen p").text("운동 기록 삭제 중입니다...");
                $("#loading-screen").fadeIn();
                setTimeout(() => {
                    fetch(`/target/current-week?userPk=${globalUserPk}`)
                        .then(response => response.json())
                        .then(data => {
                            if (!data) return;
                            document.querySelector(".target-1 .current").textContent = `${data.exerciseCount.toLocaleString()}회`;
                            document.querySelector(".target-1 .goal").textContent = `${data.exerciseTarget.toLocaleString()}회`;
                            document.querySelector(".target-2 .current").textContent = `${data.valueCount.toLocaleString()}회`;
                            document.querySelector(".target-2 .goal").textContent = `${data.valueTarget.toLocaleString()}회`;
                            document.querySelector(".target-3 .current").textContent = `${data.kcalSum.toLocaleString()}kcal`;
                            document.querySelector(".target-3 .goal").textContent = `${data.kcalTarget.toLocaleString()}kcal`;
                            updateProgress(".target-1 .target-progress", data.exerciseCount, data.exerciseTarget);
                            updateProgress(".target-2 .target-progress", data.valueCount, data.valueTarget);
                            updateProgress(".target-3 .target-progress", data.kcalSum, data.kcalTarget);
                            toggleSuccessStamp(".target-1 .success-stamp", data.exerciseCount, data.exerciseTarget);
                            toggleSuccessStamp(".target-2 .success-stamp", data.valueCount, data.valueTarget);
                            toggleSuccessStamp(".target-3 .success-stamp", data.kcalSum, data.kcalTarget);
                            checkAllGoalsCompleted(globalUserPk, data);
                        })
                        .catch(error => console.error("Error loading target data:", error));
                    $("#loading-screen").fadeOut();
                    $("#loading-screen p").text("이번 주에 기록한 내용을 반영 중 입니다...");
                    alert(result.message);
                    window.location.reload();
                }, 2000);
            })
            .catch(error => console.error("Delete failed:", error));
    });
}

function updateProgress(selector, current, goal) {
    const progressBar = document.querySelector(selector);
    if (!progressBar) return;
    const percentage = goal === 0 ? 0 : (current / goal) * 100;
    progressBar.style.width = `${percentage}%`;
}

function toggleSuccessStamp(selector, current, target) {
    const stamp = document.querySelector(selector);
    if (stamp) {
        stamp.style.display = target === 0 ? "none" : (current >= target ? "flex" : "none");
    }
}

function checkAllGoalsCompleted(userPk, data) {
    const exerciseCompleted = data.exerciseCount >= data.exerciseTarget && data.exerciseTarget > 0;
    const valueCompleted = data.valueCount >= data.valueTarget && data.valueTarget > 0;
    const kcalCompleted = data.kcalSum >= data.kcalTarget && data.kcalTarget > 0;
    const allGoalsCompleted = exerciseCompleted && valueCompleted && kcalCompleted;
    fetch(`/target/count?userPk=${userPk}`)
        .then(response => response.json())
        .then(count => {
            const currentTargetCount = count;
            if (allGoalsCompleted && currentTargetCount === 0) {
                updateTargetCount(userPk, "increase");
            } else if (!allGoalsCompleted && currentTargetCount > 0) {
                updateTargetCount(userPk, "decrease");
            }
        })
        .catch(error => console.error("Error fetching weekly goal count:", error));
}

function updateTargetCount(userPk, action) {
    console.log(`updateTargetCount 실행됨! Action: ${action}`);
    fetch(`/target/update-target-count?userPk=${userPk}&action=${action}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userPk: userPk })
    })
        .then(response => response.json())
        .then(result => {
            console.log(`Target count ${action} 성공:`, result);
            fetch(`/target/count?userPk=${userPk}`)
                .then(response => response.json())
                .then(count => {
                    const weeklyGoalCountEl = document.getElementById("weekly-goal-count");
                    if (weeklyGoalCountEl) {
                        weeklyGoalCountEl.textContent = `${count}회`;
                    }
                    console.log(`UI 업데이트 완료: ${count}회`);
                })
                .catch(error => console.error("Error fetching weekly goal count:", error));
        })
        .catch(error => console.error(`Target count ${action} 실패:`, error));
}

/* =========================================================
   7. 페이지 로드 후 스크롤 이동 (window.onload)
   ========================================================= */
window.onload = function () {
    if (localStorage.getItem("scrollToCalendar") === "true") {
        localStorage.removeItem("scrollToCalendar");
        setTimeout(() => {
            var calendarEl = document.getElementById('calendar');
            if (calendarEl) {
                console.log("캘린더로 스크롤 이동 실행");
                calendarEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
                calendarEl.focus();
            }
        }, 500);
    }
};
