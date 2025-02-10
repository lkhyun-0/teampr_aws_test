document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');

    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth', // 월별 보기
        headerToolbar: {
            left: 'prev',
            center: 'title',
            right: 'next' // 다양한 보기 옵션
        },
        views: {
            dayGridMonth: {
                titleFormat: {
                    year: 'numeric',
                    month: '2-digit'
                }
            }
        },
        datesSet: function (info) {
            setTimeout(() => {
                var titleEl = document.querySelector('.fc-toolbar-title');
                if (titleEl) {
                    // 기존 내용을 완전히 초기화
                    titleEl.innerHTML = '';

                    // 현재 달력 중앙 날짜 가져오기
                    var currentDate = calendar.getDate(); // 현재 날짜 객체 반환
                    var year = currentDate.getFullYear(); // 현재 연도
                    var month = String(currentDate.getMonth() + 1).padStart(2, '0'); // 현재 월 (2자리)

                    // 새로운 연도와 월로 요소 생성
                    titleEl.innerHTML = `
                        <div class="calendar-title">
                            <div class="calendar-year">${year}</div>
                            <div class="calendar-month">${month}</div>
                        </div>
                    `;
                }
            }, 0);
        },
        events: [
            // 예시 일정 데이터
            {
                title: '병원 방문',
                start: '2025-02-10',
                color: '#87CEEB'
            },
            {
                title: '병원 방문',
                start: '2025-02-15',
                color: '#87CEEB'
            },
            {
                title: '약 복용',
                start: '2025-02-15',
                end: '2025-02-20'
            }
        ],
        selectable: true,
        select: function (info) {
            // 상세일정 모달팝업
            let events = calendar.getEvents();
            let selectDate = new Date(info.startStr); // 사용자가 클릭한 날짜 (Date 객체)

            // 선택한 날짜에 일정이 있는지 확인
            let hasEvent = events.some(event => {
                let startDate = new Date(event.startStr); // 시작 날짜
                let endDate = event.endStr ? new Date(event.endStr) : startDate; // 종료 날짜 (없으면 시작 날짜)

                // 🔥 FullCalendar 특성 반영: 종료 날짜가 존재하면 하루 빼기 (표시 범위 조정)
                if (event.endStr) {
                    endDate.setDate(endDate.getDate() - 1);
                }

                // ✅ 정확한 범위 비교: startDate와 일치하거나, (startDate ≤ selectDate ≤ endDate)
                return selectDate.getTime() === startDate.getTime() ||
                    (selectDate >= startDate && selectDate <= endDate);
            });

            if (hasEvent) {
                // 일정이 있으면 모달 표시
                $('.detail-popup').css({
                    'opacity': '1',
                    'visibility': 'visible'
                });
            } else {
                // 일정이 없으면 알림 (선택사항)
                alert("해당 날짜에는 일정이 없습니다.");
            }
        },
    });

    calendar.render();

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            $('.detail-popup').css({
                'opacity': '0',
                'visibility': 'hidden'
            });
        }
    });
});