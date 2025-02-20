let selectedGoal = "";

function selectGoal(goal, element) {
    selectedGoal = goal;
    $(".card").removeClass("selected");
    $(element).addClass("selected");
}

$(".card").on("click", function () {
    selectGoal($(this).find(".card-text").text(), this);
});

$("#next-button").on("click", function () {
    if (!selectedGoal) {
        alert("먼저 목표를 선택하세요!");
        return;
    }

    $("#loading-screen").fadeIn();

    $.ajax({
        url: "http://localhost:8000/recommend",
        type: "GET",
        data: { goal: selectedGoal },
        success: function (response) {
            $("#loading-screen").fadeOut();

            // 추천 결과를 로컬 스토리지에 저장
            localStorage.setItem("goal", response.goal);
            localStorage.setItem("diet_recommendation", response.diet_recommendation);

            // 결과 페이지로 이동
            window.location.href = "/food/recomResult";
        },
        error: function () {
            alert("추천을 가져오는 데 실패했습니다.");
            $("#loading-screen").fadeOut();
        }
    });
});