// 풀캘린더
document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');

    window.calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        selectable: false,
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
        dateClick: function (info) {
            let events = calendar.getEvents();

            let selectDate = new Date(info.date);
            let formattedSelectDate = selectDate.getFullYear() + '-' +
                String(selectDate.getMonth() + 1).padStart(2, '0') + '-' +
                String(selectDate.getDate()).padStart(2, '0');

            // 선택한 날짜의 일정 찾기
            let selectedEvent = events.find(event => {
                let eventDate = new Date(event.start);
                let formattedEventDate = eventDate.getFullYear() + '-' +
                    String(eventDate.getMonth() + 1).padStart(2, '0') + '-' +
                    String(eventDate.getDate()).padStart(2, '0');

                return formattedSelectDate === formattedEventDate;
            });

            if (selectedEvent) {
                let hospitalPk = selectedEvent.id;
                fetchEventDetails(hospitalPk, formattedSelectDate); // 상세 정보 불러오기
            } else {
                alert("해당 날짜에는 일정이 없습니다.");
            }
        },
    });

    window.calendar.render();

    // 캘린더 데이터 로드
    loadHospitalPlans(calendar);
});

// 일정 불러오기 함수 (calendar 객체를 인자로 받도록 수정)
function loadHospitalPlans(calendar) {
    $.ajax({
        url: "/plan/getAllPlansAjax",
        type: "GET",
        success: function (data) {
            calendar.removeAllEvents();

            data.forEach(event => {
                calendar.addEvent({
                    id: event.id,
                    title: event.title,
                    start: event.start,
                    allDay: true
                });
            });

            window.calendar.render();
        },
        error: function () {
            alert("일정 목록을 불러오는 데 실패했습니다.");
        }
    });
}


// 지도
let map;

// 지도 초기화 및 현재 위치 표시
function initMap() {
    const defaultCenter = new google.maps.LatLng(37.5665, 126.9780);
    map = new google.maps.Map(document.getElementById("map"), {
        center: defaultCenter,
        zoom: 18
    });

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            position => {
                const userLatLng = new google.maps.LatLng(
                    position.coords.latitude,
                    position.coords.longitude
                );
                map.setCenter(userLatLng);
                addMarker(userLatLng, "내 위치", map); // 현재 위치 마커만 추가
            },
            error => {
                console.error("위치 정보를 가져올 수 없습니다.", error);
            },
            {enableHighAccuracy: true, timeout: 10000, maximumAge: 0}
        );
    } else {
        alert("위치 정보를 사용할 수 없습니다.");
    }
}

// 팝업 전용 지도 초기화 함수
function initPopupMap(hospital) {
    const popupMapEl = document.getElementById("popup-map");
    const popupMap = new google.maps.Map(popupMapEl, {
        center: new google.maps.LatLng(hospital.latitude, hospital.longitude),
        zoom: 15
    });

    addMarker(new google.maps.LatLng(hospital.latitude, hospital.longitude), hospital.name, popupMap)

    const infoWindow = new google.maps.InfoWindow({
        content: `<div><strong>${hospital.name}</strong><br>${hospital.address}</div>`,
    });
    infoWindow.open(popupMap);
}

// 마커 추가 함수
function addMarker(position, title, targetMap) {
    new google.maps.Marker({
        position: position,
        map: targetMap,
        title: title
    });
}

// 최근 일정가져오기 버튼 함수
$(".favorite-list button").on("click", function () {
    const hospitalName = $(this).text().trim();

    $.ajax({
        url: "/plan/getHospitalRecent",
        type: "GET",
        data: { hospitalName: hospitalName },
        success: function (data) {
            if (!data) {
                alert("병원 정보를 가져올 수 없습니다.");
                return;
            }

            // 병원 정보 입력란 자동 채우기
            $("#hospital-info").val(data.hospitalName);
            $("#hospital-lat").val(data.latitude);
            $("#hospital-lng").val(data.longitude);
            $("#hospital-address").val(data.address);

            // 지도에 병원 위치 표시
            displayHospitalOnMap(data);
        },
        error: function () {
            alert("병원 정보를 불러오는 데 실패했습니다.");
        }
    });
});

// 병원 검색함수
$("#search-button").on("click", function () {
    const hospitalName = $("#hospital-info").val().trim();
    var $list = $('#hospital-list');

    if (!hospitalName) {
        alert("병원 이름을 입력하세요.");
        return;
    }

    $.ajax({
        url: "/hospital/search",
        type: "GET",
        data: {name: hospitalName},
        success: function (data) {
            if (data.length === 0) {
                $list.hide();
                alert("검색 결과 없습니다.");
                return;
            }

            showHospitalList(data); // 검색된 병원 리스트 표시
        },
        error: function () {
            alert("병원을 찾을 수 없습니다.");
        }
    });
});

// 검색 결과 리스트를 생성 및 표시하는 함수
function showHospitalList(hospitals) {
    var $list = $('#hospital-list');
    $list.empty(); // 기존 리스트 비우기

    if (hospitals.length === 0) {
        $list.hide();
        return;
    }

    // 각 병원 정보를 리스트 아이템(li)로 생성
    hospitals.forEach(function (hospital) {
        // hospital 객체의 'name' 프로퍼티 사용 (서버에서 반환하는 데이터 구조에 맞게 수정)
        var $li = $('<li></li>').text(hospital.name);
        // 아이템 클릭 시 입력란에 병원명 설정 후 리스트 숨김 처리
        $li.on('click', function () {
            $("#hospital-info").val(hospital.name);
            $("#hospital-lat").val(hospital.latitude);
            $("#hospital-lng").val(hospital.longitude);
            $("#hospital-address").val(hospital.address);
            $list.hide();
            displayHospitalOnMap(hospital);
        });
        $list.append($li);
    });

    // 리스트 표시
    $list.show();
}

// 병원의 위치를 지도에 표시하는 함수
function displayHospitalOnMap(hospital) {
    const position = new google.maps.LatLng(hospital.latitude, hospital.longitude); // 병원 좌표
    const mapOptions = {
        center: position,
        zoom: 15,
    };

    // 지도 중심 이동
    map.setCenter(position);

    // 마커 추가
    new google.maps.Marker({
        position: position,
        map: map,
        title: hospital.name, // 마커에 병원 이름 표시
    });

    // 병원 정보 윈도우 표시
    const infoWindow = new google.maps.InfoWindow({
        content: `<div><strong>${hospital.name}</strong><br>${hospital.address}</div>`,
    });
    infoWindow.open(map);
}

// 병원 정보담는 함수
$("#hospital-info").on("blur", function () {
    if ($("#hospital-lat").val() === "") {  // 병원 좌표가 저장되지 않았다면
        console.log("검색되지 않은 병원: 이름만 저장");
        $("#hospital-lat").val(null);
        $("#hospital-lng").val(null);
        $("#hospital-address").val(null);
    }
});

// 병원일정 저장 함수
function hospitalSave(event) {
    event.preventDefault();

    const form = document.forms["form1"];

    if (!form.checkValidity()) {
        return false;
    }

    const hospitalData = {
        selectDate: $("#select-date").val(),
        selectTime: $("#select-time").val(),
        hospitalName: $("#hospital-info").val(),
        latitude: $("#hospital-lat").val() || null,
        longitude: $("#hospital-lng").val() || null,
        address: $("#hospital-address").val() || null
    };

    $.ajax({
        url: "/plan/saveHospital",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(hospitalData),
        success: function (hopspital) {
            alert("일정이 등록되었습니다.");

            let calendar = window.calendar;

            calendar.removeAllEvents();

            calendar.addEvent({
                id: hopspital.id,
                title: hopspital.title,
                start: hopspital.start,
                allDay: true
            });

            loadHospitalPlans(calendar);
            window.calendar.render();
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("로그인이 필요합니다.");
            } else {
                alert("등록을 실패하였습니다.");
            }
        }
    });

    return false;
}

// AJAX로 병원일정 상세 정보 가져오기
function fetchEventDetails(hospitalPk, selectDate) {
    $.ajax({
        url: `/plan/getHospitalDetail/${hospitalPk}?selectDate=${selectDate}`, // 서버에서 일정 상세 정보 제공하는 API
        type: "GET",
        success: function (data) {
            console.log(data);
            updatePopupContent(data); //팝업 내용 업데이트
        },
        error: function () {
            alert("일정 상세 정보를 불러오는 데 실패했습니다.");
        }
    });

}

// 병원팝업 업데이트
function updatePopupContent(eventData) {
    $(".content-title").text(`🏥 ${eventData.selectDate} 병원 일정`);
    $(".hospital-table tr:nth-child(1) td").text(eventData.selectTime); // 시간
    $(".hospital-table tr:nth-child(2) td").text(eventData.hospitalName); // 병원 이름
    $(".hospital-table tr:nth-child(3) td").text(eventData.address); // 병원 위치
    $(".delete-btn").attr("onclick", `deleteEvent(${eventData.id})`); // 삭제 버튼에 ID 연결

    $('.detail-popup').css({
        'opacity': '1',
        'visibility': 'visible'
    });

    // 병원 위치가 있으면 지도에 표시
    if (eventData.latitude && eventData.longitude) {
        $("#popup-map").remove();
        $(".hospital-table tr:nth-child(4) td").append('<div id="popup-map" style="width: 100%; height: 200px;"></div>');

        initPopupMap(eventData);

        $("#popup-map").show();
    } else {
        $("#popup-map").hide(); // 위치 정보 없으면 지도 숨기기
    }
}

// 약 시간 추가 함수
$(".add-time").click(function () {
    let timeContainer = $(this).closest("form").find(".time-container");

    if (timeContainer.find(".time-field").length >= 2) { // 최대 2개까지만 추가 가능
        alert("최대 2개의 시간을 추가할 수 있습니다.");
        return;
    }

    let newTimeInput = `
        <div class="time-list">
            <input type="time" class="time-field" name="select-time" step="900">
            <button type="button" class="remove-time">🗑</button>
        </div>
        `;

    timeContainer.append(newTimeInput);

    // 새로 추가된 삭제 버튼에 이벤트 연결
    timeContainer.find(".remove-time").off("click").on("click", function () {
        $(this).closest(".time-list").remove();
    });
});


// 모달 닫기 함수
window.closeModal = function () {
    $('.detail-popup').css({
        'opacity': '0',
        'visibility': 'hidden'
    });
};

$(".tab").click(function () {
    // 모든 탭 버튼 비활성화 & 모든 콘텐츠 숨김
    $(".tab").removeClass("active");
    $(".detail-content").removeClass("active");

    // 클릭한 버튼 활성화 & 해당 콘텐츠 표시
    $(this).addClass("active");
    $("#" + $(this).data("tab")).addClass("active");
});

// 약 유형 선택함수
$(".medicine-type").click(function () {
    $(".medicine-type").removeClass("selected");
    $(this).addClass("selected");
    $("#medicineType").val($(this).data("type"));
});

// 폼 제출 시, 사용자가 선택하지 않았다면 경고 메시지
$("form[name='form2']").submit(function (e) {
    if (!$("#medicineType").val()) {
        alert("약 유형을 선택해주세요!");
        e.preventDefault();
    }
});