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
                addMarker(userLatLng, "내 위치"); // ✅ 현재 위치 마커만 추가
            },
            error => {
                console.error("위치 정보를 가져올 수 없습니다.", error);
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    } else {
        alert("위치 정보를 사용할 수 없습니다.");
    }
}

// 마커 추가 함수
function addMarker(position, title) {
    new google.maps.Marker({
        position: position,
        map: map,
        title: title
    });
}

$("#search-button").on("click", function () {
    const hospitalName = $("#hospital-info").val().trim();
    if (!hospitalName) {
        alert("병원 이름을 입력하세요.");
        return;
    }

    $.ajax({
        url: "/hospital/search",
        type: "GET",
        data: { name: hospitalName },
        success: function (data) {
            if (data.length === 0) { // ✅ 검색 결과가 빈 배열인지 확인
                alert("해당 검색어로 병원을 찾을 수 없습니다."); // 경고창 띄우기
                return; // 빈 결과이므로 이후 코드 실행 중단
            }

            console.log("검색 결과:", data);
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
    hospitals.forEach(function(hospital) {
        // hospital 객체의 'name' 프로퍼티 사용 (서버에서 반환하는 데이터 구조에 맞게 수정)
        var $li = $('<li></li>').text(hospital.name);
        // 아이템 클릭 시 입력란에 병원명 설정 후 리스트 숨김 처리
        $li.on('click', function() {
            $('#hospital-info').val(hospital.name);
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