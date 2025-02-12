// 풀캘린더
document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');

    window.calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        timeZone: 'Asia/Seoul',
        headerToolbar: {
            left: 'prev addEventButton',
            center: 'title',
            right: 'today next'
        },
        selectable: false,
        contentHeight: 'auto', // 일정이 많아도 높이 유지
        dayMaxEvents: 3,
        eventOrder: function(eventA, eventB) {
            if (eventA.extendedProps.category === "hospital" && eventB.extendedProps.category === "medicine") {
                return -1; // 병원이 위쪽으로 정렬
            } else if (eventA.extendedProps.category === "medicine" && eventB.extendedProps.category === "hospital") {
                return 1; // 약 일정이 아래쪽으로 정렬
            }
            return 0; // 나머지는 기존 순서 유지
        },
        customButtons: {
            addEventButton: {
                text: '일정 추가',
                click: function () {
                    $('html, body').animate({
                        scrollTop: $('.plan').offset().top
                    }, 500);
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
        eventClick: function (info){

        },
        dateClick: function (info) {
            let events = window.calendar.getEvents();
            let selectDate = new Date(info.date);

            console.log("📅 현재 캘린더에 등록된 이벤트 목록:", events);

            // 선택한 날짜에 해당하는 병원 일정 찾기
            let hospitalEvent = events.find(event => {
                let eventDate = new Date(event.start).toISOString().split("T")[0]; // YYYY-MM-DD 추출
                let selectedDateStr = selectDate.toISOString().split("T")[0]; // YYYY-MM-DD 추출

                console.log("selectedDateStr: " + selectedDateStr);
                console.log("eventDate: " + eventDate);

                console.log("데이터 타입 비교:", typeof selectedDateStr, typeof eventDate);

                return eventDate === selectedDateStr
            });

            console.log("🔍 찾은 병원 일정:", hospitalEvent);

            // 선택한 날짜에 해당하는 약 일정 찾기
            let medicineEvent = events.find(event => {
                let startDate = new Date(event.start);
                let endDate = new Date(event.end);
                return startDate <= selectDate && selectDate < endDate
            });

            if (hospitalEvent || medicineEvent) {
                // 둘 다 일정이 있으면 병원 & 약 일정 데이터를 가져옴
                if (hospitalEvent) {
                    console.log("hospitalEvent.id:" + hospitalEvent.id)
                    fetchHospitalDetails(hospitalEvent.id, info.dateStr);
                }
                if (medicineEvent) {
                    fetchMedicineDetails(medicineEvent.id, info.dateStr);
                }
                openPopup(hospitalEvent, medicineEvent);
            } else {
                alert("해당 날짜에는 일정이 없습니다.");
            }
        }

    });

    window.calendar.render();

    // 캘린더 데이터 로드
    loadAllPlans(calendar);

});

function loadAllPlans(calendar) {

    Promise.all([
        $.ajax({ url: "/plan/getAllHospitalPlansAjax", type: "GET" }),
        $.ajax({ url: "/plan/getAllMedicinePlansAjax", type: "GET" })
    ])
        .then(([hospitalData, medicineData]) => {

            calendar.removeAllEvents();

            // 병원 일정 추가
            hospitalData.forEach(event => {


                let newEvent = {
                    id: event.id,
                    title: event.title,
                    start: event.start,
                    allDay: true,
                    backgroundColor: "#79b9fa", // 병원 일정 색상
                    category: "hospital"
                };
                calendar.addEvent(newEvent);
            });

            // 약 일정 추가
            medicineData.forEach(event => {
                let endDate = new Date(event.end);
                endDate.setDate(endDate.getDate() + 1);

                let newEvent = {
                    id: event.id,
                    title: event.title,
                    start: event.start,
                    end: endDate.toISOString().split('T')[0],
                    allDay: true,
                    backgroundColor: "#6dd984", // 약 일정 색상
                    category: "medicine"
                };
                calendar.addEvent(newEvent);
            });

            window.calendar.render();
        })
        .catch(error => {
            alert("일정을 불러오는 데 실패했습니다.");
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

                var marker = new google.maps.Marker({
                    position: userLatLng,
                    map: map,
                    title: "현재 위치"
                });

                var infowindow = new google.maps.InfoWindow({
                    content: marker.title
                });

                marker.addListener("click", function() {
                    infowindow.open(map, marker);
                });
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
        zoom: 17
    });

    addMarker(new google.maps.LatLng(hospital.latitude, hospital.longitude), hospital.name, popupMap)

    const infoWindow = new google.maps.InfoWindow({
        content: `<div><strong>${hospital.name}</strong><br>${hospital.address}</div>`,
    });
    infoWindow.open(popupMap);
}

// 마커 추가 함수
function addMarker(position, title, targetMap, iconUrl) {
    new google.maps.Marker({
        position: position,
        map: targetMap,
        title: title,
        icon:{
            url: "/images/hospital.png",
            scaledSize: new google.maps.Size(30, 30),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(20, 40)
        }
    });
}

// 최근 일정가져오기 버튼 함수
$(".hospital-recent").on("click", function () {
    const hospitalName = $(this).text().trim();

    $.ajax({
        url: "/plan/getHospitalRecent",
        type: "GET",
        data: {hospitalName: hospitalName},
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
    addMarker(position, hospital.name, map)

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
        selectTime: $("#hospital-time").val(),
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

            loadAllPlans(calendar);
            window.calendar.render();

            $('.plan').animate({
                scrollTop: 0
            }, 500);

            $("#select-date").val("");
            $("#hospital-time").val("");
            $("#hospital-info").val("");
            $("#hospital-lat").val("");
            $("#hospital-lng").val("");
            $("#hospital-address").val("");
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("로그인이 필요합니다.");
            } else if (xhr.status === 409) {
                alert("이미 해당 날짜에 병원 일정이 등록되었습니다.");
            } else {
                alert("등록을 실패하였습니다.");
            }
        }
    });

    return false;
}

// AJAX로 병원일정 상세 정보 가져오기
function fetchHospitalDetails(hospitalPk, selectDate) {
    $.ajax({
        url: `/plan/getHospitalDetail/${hospitalPk}?selectDate=${selectDate}`, // 서버에서 일정 상세 정보 제공하는 API
        type: "GET",
        success: function (data) {
            console.log(data);
            updatePopupHospital(data); //팝업 내용 업데이트
        },
        error: function () {
            alert("일정 상세 정보를 불러오는 데 실패했습니다.");
        }
    });

}

// 병원팝업 업데이트
function updatePopupHospital(eventData) {
    console.log(eventData);

    $(".hospital-title").text(`🏥 ${eventData.selectDate} 병원 일정`);
    $(".hospital-table tr:nth-child(1) td").text(eventData.selectTime); // 시간
    $(".hospital-table tr:nth-child(2) td").text(eventData.hospitalName); // 병원 이름
    $(".hospital-table tr:nth-child(3) td").text(eventData.address); // 병원 위치
    $(".delete-btn.hospital").attr("onclick", `deleteHospital(${eventData.hospitalPk})`); // 삭제 버튼에 ID 연결

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

// 일정 삭제 함수
function deleteHospital(hospitalPk) {
    if (!confirm("정말 삭제하시겠습니까?")) {
        return;
    }

    $.ajax({
        url: `/plan/deleteHospital/${hospitalPk}`,
        type: "DELETE",
        success: function (response) {
            alert("일정이 삭제되었습니다.");

            // 캘린더에서 해당 이벤트 제거
            let calendar = window.calendar;
            let event = calendar.getEventById(hospitalPk);

            if (event) {
                event.remove();
            }

            closeModal(); // 팝업 닫기
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("로그인이 필요합니다.");
            } else if (xhr.status === 404) {
                alert("해당 일정을 찾을 수 없습니다.");
            } else {
                alert("삭제를 실패하였습니다.");
            }
        }
    });
}

// // 약 시간 추가 함수
// $(".add-time").click(function () {
//     let timeContainer = $(this).closest("form").find(".time-container");
//
//     if (timeContainer.find(".time-field").length >= 2) { // 최대 2개까지만 추가 가능
//         alert("최대 2개의 시간을 추가할 수 있습니다.");
//         return;
//     }
//
//     let newTimeInput = `
//         <div class="time-list">
//             <input type="time" class="time-field" name="select-time" step="900">
//             <button type="button" class="remove-time">🗑</button>
//         </div>
//         `;
//
//     timeContainer.append(newTimeInput);
//
//     // 새로 추가된 삭제 버튼에 이벤트 연결
//     timeContainer.find(".remove-time").off("click").on("click", function () {
//         $(this).closest(".time-list").remove();
//     });
// });

// 약 유형 선택함수
$(".medicine-type").click(function () {
    $(".medicine-type").removeClass("selected");
    $(this).addClass("selected");
    $("#medicineType").val(Number($(this).attr("data-type")));
});

// 폼 제출 시, 사용자가 선택하지 않았다면 경고 메시지
$("form[name='form2']").submit(function (e) {
    if (!$("#medicineType").val()) {
        alert("약 유형을 선택해주세요!");
        e.preventDefault();
    }
});

// 약 일정 저장함수
function medicineSave(event) {
    event.preventDefault();

    const form = document.forms["form2"];

    if (!form.checkValidity()) {
        return false;
    }

    const medicineData = {
        startDate: $("#start-date").val(),
        endDate: $("#end-date").val(),
        selectTime: $("#medicine-time").val(),
        medicineName: $("#medicine-name").val(),
        medicineType: $("#medicineType").val()
    };

    $.ajax({
        url: "/plan/saveMedicine",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(medicineData),
        success: function (medicine) {
            alert("일정이 등록되었습니다.");

            let calendar = window.calendar;

            calendar.removeAllEvents();

            calendar.addEvent({
                id: medicine.id,
                title: medicine.title,
                start: medicine.start,
                end: medicine.end,
            });

            loadAllPlans(calendar);
            window.calendar.render();

            $("#start-date").val("");
            $("#end-date").val("");
            $("#medicine-time").val("");
            $("#medicine-name").val("");
            $("#medicineType").val("");
            $(".medicine-type").removeClass("selected");
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

$(".medicine-recent").on("click", function () {
    const medicineName = $(this).text().trim();

    $.ajax({
        url: "/plan/getMedicineRecent",
        type: "GET",
        data: {medicineName: medicineName},
        success: function (data) {
            if (!data) {
                alert("약 정보를 가져올 수 없습니다.");
                return;
            }

            // 병원 정보 입력란 자동 채우기
            $("#medicine-name").val(data.medicineName);
            $("#medicineType").val(data.medicineType);

            // 기존 선택된 약 유형 해제
            $(".medicine-type").removeClass("selected");

            // 가져온 medicineType 값과 일치하는 요소 선택
            let selectedType = $(".medicine-type[data-type='" + data.medicineType + "']");

            if (selectedType.length) {
                selectedType.addClass("selected");
            }
        },
        error: function () {
            alert("약 정보를 불러오는 데 실패했습니다.");
        }
    });
});

function fetchMedicineDetails(medicinePk, selectDate) {
    $.ajax({
        url: `/plan/getMedicineDetail?selectDate=${selectDate}`,
        type: "GET",
        success: function (data) {
            updatePopupMedicine(data, selectDate); // 약 일정 팝업 업데이트
        },
        error: function () {
            alert("약 일정 상세 정보를 불러오는 데 실패했습니다.");
        }
    });
}

function updatePopupMedicine(eventData, selectDate) {
    $(".medicine-title").html(`<img class="medicine" src="/images/medicine.jpg" alt=""> ${selectDate} 약 일정`);

    let medicineTable = $(".medicine-table");
    medicineTable.find("tr:gt(0)").remove(); // 기존 데이터 삭제 (첫 번째 행 제외)


    eventData.forEach(medicine => {
        let medicineTypeImg = medicine.medicineType == 1
            ? "/images/medicine.jpg"
            : "/images/syringe.jpg";

        let row = `
            <tr data-medicine-id="${medicine.medicinePk}">
                <td><input type="checkbox" class="delete-checkbox"></td>
                <td>${medicine.selectTime}</td>
                <td>${medicine.medicineName}</td>
                <td><img src="${medicineTypeImg}" alt="약 유형"></td>
            </tr>
        `;
        medicineTable.append(row);
    });

    $(".delete-btn.medicine").attr("onclick", `deleteMedicine(${eventData.medicinePk})`);
}

function deleteMedicine(medicinePk) {
    // 체크된 약 일정들의 medicinePk 값을 배열로 수집
    let selectedMedicineIds = $(".medicine-table .delete-checkbox:checked").map(function () {
        return $(this).closest("tr").data("medicine-id"); // 각 tr에 저장된 ID 값 가져오기
    }).get();

    if (selectedMedicineIds.length === 0) {
        alert("삭제할 약 일정을 선택하세요.");
        return;
    }

    if (!confirm("선택한 약 일정을 삭제하시겠습니까?")) {
        return;
    }

    // AJAX 요청으로 여러 개의 약 일정 삭제 요청
    $.ajax({
        url: "/plan/deleteMedicine",
        type: "DELETE",
        contentType: "application/json",
        data: JSON.stringify({ medicinePkList: selectedMedicineIds }),
        success: function (response) {
            alert("선택한 약 일정이 삭제되었습니다.");

            // 캘린더에서 해당 이벤트 제거
            let calendar = window.calendar;
            selectedMedicineIds.forEach(medicinePk => {
                let event = calendar.getEventById(medicinePk);
                if (event) {
                    event.remove();
                }
            });

            closeModal(); // 팝업 닫기
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("로그인이 필요합니다.");
            } else if (xhr.status === 404) {
                alert("일정을 찾을 수 없습니다.");
            } else {
                alert("삭제를 실패하였습니다.");
                console.error("삭제 실패:", xhr.responseText);
            }
        }
    });
}

function openPopup(hasHospital, hasMedicine) {
    // 모든 탭 초기화
    $(".tab").removeClass("active");
    $(".detail-content").removeClass("active");
    $(".tab").removeClass("hidden");

    if (hasHospital && hasMedicine) {
        // 병원 & 약 일정 둘 다 있을 경우
        $(".tab[data-tab='tab1']").addClass("active");
        $(".tab[data-tab='tab2']").removeClass("hidden");
        $("#tab1").addClass("active");
        $("#tab2").removeClass("hidden");
    } else if (hasHospital) {
        // 병원 일정만 있는 경우
        $(".tab[data-tab='tab1']").addClass("active");
        $(".tab[data-tab='tab2']").addClass("hidden");
        $("#tab1").addClass("active");
        $("#tab2").addClass("hidden");
    } else if (hasMedicine) {
        // 약 일정만 있는 경우
        $(".tab[data-tab='tab1']").addClass("hidden");
        $(".tab[data-tab='tab2']").addClass("active");
        $("#tab1").addClass("hidden");
        $("#tab2").addClass("active");
    }

    // 팝업 표시
    $('.detail-popup').css({
        'opacity': '1',
        'visibility': 'visible'
    });
}


// 모달 닫기 함수
window.closeModal = function () {
    $('.detail-popup').css({
        'opacity': '0',
        'visibility': 'hidden'
    });
};

window.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
        closeModal();
    }
});

$(".tab").click(function () {
    // 모든 탭 버튼 비활성화 & 모든 콘텐츠 숨김
    $(".tab").removeClass("active");
    $(".detail-content").removeClass("active");

    // 클릭한 버튼 활성화 & 해당 콘텐츠 표시
    $(this).addClass("active");
    $("#" + $(this).data("tab")).addClass("active");
});
