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