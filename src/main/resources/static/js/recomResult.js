// 식단 텍스트를 파싱하는 함수
function parseMealOption(text) {
    const lines = text.split("\n").map(line => line.trim());
    let currentSection = null;

    const data = {
        breakfast: [],
        breakfastNutrition: [],
        breakfastTip: [],
        lunch: [],
        lunchNutrition: [],
        lunchTip: [],
        dinner: [],
        dinnerNutrition: [],
        dinnerTip: []
    };

    for (let line of lines) {
        // "### 식단 1:" 등의 헤더는 건너뛰기
        if (line.startsWith("### ")) {
            continue;
        }
        if (line.startsWith("**아침**")) {
            currentSection = "breakfast";
            continue;
        } else if (line.startsWith("**아침 영양 정보**")) {
            currentSection = "breakfastNutrition";
            continue;
        } else if (line.startsWith("**아침 영양 팁**")) {
            currentSection = "breakfastTip";
            continue;
        } else if (line.startsWith("**점심**")) {
            currentSection = "lunch";
            continue;
        } else if (line.startsWith("**점심 영양 정보**")) {
            currentSection = "lunchNutrition";
            continue;
        } else if (line.startsWith("**점심 영양 팁**")) {
            currentSection = "lunchTip";
            continue;
        } else if (line.startsWith("**저녁**")) {
            currentSection = "dinner";
            continue;
        } else if (line.startsWith("**저녁 영양 정보**")) {
            currentSection = "dinnerNutrition";
            continue;
        } else if (line.startsWith("**저녁 영양 팁**")) {
            currentSection = "dinnerTip";
            continue;
        }

        if (line.startsWith("-")) {
            const item = line.replace(/^-\s*/, "");
            if (currentSection && data[currentSection]) {
                data[currentSection].push(item);
            }
        }
    }
    return data;
}

// 파싱된 데이터를 HTML의 해당 위치에 렌더링하는 함수
function renderDietToHTML(dietData, tabIndex) {
    // 아침
    $(`#tab${tabIndex}-breakfast`).empty().append(
        dietData.breakfast.map(item => `<li>${item}</li>`)
    );
    $(`#tab${tabIndex}-breakfast-nutrition`).empty().append(
        dietData.breakfastNutrition.map(item => `<li>${item}</li>`)
    );
    $(`#tab${tabIndex}-breakfast-tip`).text(dietData.breakfastTip.join(" "));

    // 점심
    $(`#tab${tabIndex}-lunch`).empty().append(
        dietData.lunch.map(item => `<li>${item}</li>`)
    );
    $(`#tab${tabIndex}-lunch-nutrition`).empty().append(
        dietData.lunchNutrition.map(item => `<li>${item}</li>`)
    );
    $(`#tab${tabIndex}-lunch-tip`).text(dietData.lunchTip.join(" "));

    // 저녁
    $(`#tab${tabIndex}-dinner`).empty().append(
        dietData.dinner.map(item => `<li>${item}</li>`)
    );
    $(`#tab${tabIndex}-dinner-nutrition`).empty().append(
        dietData.dinnerNutrition.map(item => `<li>${item}</li>`)
    );
    $(`#tab${tabIndex}-dinner-tip`).text(dietData.dinnerTip.join(" "));
}

// 탭 활성화 함수
function activateTab(tabId) {
    $(".tab").removeClass("active");
    $(".tab-content").removeClass("active").hide();
    $(`.tab[data-tab="${tabId}"]`).addClass("active");
    $(`#${tabId}`).addClass("active").show();
}

// AJAX 요청으로 새로운 식단을 가져오는 함수
function fetchNewDiet(tabIndex) {
    const goal = localStorage.getItem("goal");
    if (!goal) {
        alert("추천받은 식단이 없습니다. 다시 추천을 받아주세요.");
        window.location.href = "/food/recom";
        return;
    }
    $("#loading").show();
    $.ajax({
        url: "http://localhost:8000/recommend",
        type: "GET",
        data: { goal: goal },
        success: function(response) {
            $("#loading").hide();
            const mealOption = response.meal_options;
            if (mealOption) {
                const dietData = parseMealOption(mealOption);
                renderDietToHTML(dietData, tabIndex);
                // 새 탭 활성화
                activateTab("tab" + tabIndex);
            } else {
                alert("추천된 식단이 없습니다.");
            }
        },
        error: function() {
            $("#loading").hide();
            alert("추천을 가져오는 데 실패했습니다.");
        }
    });
}

$(document).ready(function(){
    $("#loading").show();

    const goal = localStorage.getItem("goal");
    if (!goal) {
        alert("추천받은 식단이 없습니다. 다시 추천을 받아주세요.");
        window.location.href = "/food/recom";
        return;
    }
    $("#goal-title").text(`${goal}를 위한 AI 추천 식단`);

    // 초기 식단(식단1) 요청 및 렌더링
    $.ajax({
        url: "http://localhost:8000/recommend",
        type: "GET",
        data: { goal: goal },
        success: function(response) {
            $("#loading").hide();
            const mealOption = response.meal_options;
            if (mealOption) {
                const dietData = parseMealOption(mealOption);
                renderDietToHTML(dietData, 1);
                activateTab("tab1");
            } else {
                alert("추천된 식단이 없습니다.");
            }
        },
        error: function() {
            $("#loading").hide();
            alert("추천을 가져오는 데 실패했습니다.");
        }
    });

    // 탭 클릭 이벤트 (각 탭을 클릭하면 해당 탭 내용이 보이도록)
    $(".tab").click(function(){
        const tabId = $(this).attr("data-tab");
        activateTab(tabId);
    });

    // '식단 추가' 버튼 클릭 이벤트
    $("#add-diet").click(function(){
        if(confirm("식단을 추가하시겠습니까?")) {
            let nextTabIndex;
            if ($(`.tab[data-tab="tab2"]`).is(":hidden")) {
                nextTabIndex = 2;
                $(`.tab[data-tab="tab2"]`).show();
            } else if ($(`.tab[data-tab="tab3"]`).is(":hidden")) {
                nextTabIndex = 3;
                $(`.tab[data-tab="tab3"]`).show();
            } else {
                alert("더 이상 추가할 수 없습니다.");
                return;
            }
            fetchNewDiet(nextTabIndex);
        }
    });
});