$(document).ready(function () {
    let activeSlot = null; // 현재 활성화된 슬롯
    let selectedFoodType = ""; // 현재 선택된 식사 타입 (아침, 점심, 저녁)
    let userPk = [[${session.userPk}]];

    const tips = [
        "오늘도 건강한 한 끼를 기록해볼까요? 📝 작은 기록이 모여 큰 변화를 만듭니다! 어제보다 더 나은 선택으로 건강을 쌓아가요.",
        "음식은 단순한 연료가 아니에요. 내 몸을 위한 소중한 영양소입니다! 💪 오늘은 어떤 영양소를 채워볼까요?",
        "식단을 기록하는 순간, 당신의 건강 목표는 한 걸음 더 가까워집니다! 🚀 꾸준한 기록이 건강한 몸을 만드는 첫걸음이에요.",
        "하루 세 끼, 아무렇게나 먹을 수도 있지만, 조금 더 신경 쓰면 내 몸이 달라집니다! ✨ 오늘은 어떤 음식을 선택했나요?",
        "건강한 식습관의 비밀? 🤫 바로 꾸준한 기록! 매일의 작은 습관이 쌓이면, 어느새 목표에 도달할 거예요.",
        "‘조금만 더 건강하게’라는 작은 노력들이 쌓여 내 몸을 바꿉니다! 🏆 오늘도 균형 잡힌 식사를 기록하며 건강을 챙겨볼까요?",
        "오늘도 건강을 위한 한 걸음! 🍎 하루 세 끼 중 한 끼라도 더 신경 써서 먹으면, 내 몸은 그만큼 좋아질 거예요.",
        "음식은 내 몸을 만드는 재료입니다! 🎨 어떤 음식을 먹느냐에 따라 내 몸도 달라지겠죠? 좋은 재료로 오늘 하루를 채워보세요!",
        "식단 기록, 귀찮다고요? 하지만 내 몸은 모든 걸 기억하고 있어요. 😏 나를 위한 건강한 습관, 지금 시작해볼까요?",
        "오늘 먹은 음식이 내일의 에너지가 됩니다! 🚴‍♂️ 건강한 선택을 기록하는 순간, 더 좋은 내일을 만드는 거예요."
    ];

    // 랜덤으로 문구 선택
    const randomTip = tips[Math.floor(Math.random() * tips.length)];

    // 선택한 문구를 페이지에 적용
    $(".description").text(randomTip);

    // 팝업 열기 + 식사 타입 설정
    $('.plus-button').click(function () {
        $('.popup-overlay').fadeIn();
        $('.popup1').fadeIn();

        // 클릭된 카드에서 식사 타입(아침, 점심, 저녁) 가져오기
        let parentCard = $(this).closest(".card");
        selectedFoodType = parentCard.find(".title").text().trim(); // "아침", "점심", "저녁"
    });

    // 팝업 닫기 (오버레이 또는 닫기 버튼 클릭)
    $('.popup-overlay, .popup-close-btn').click(function () {
        $('.popup-overlay').fadeOut();
        $('.popup1').fadeOut();
    });

    // 팝업 내부 클릭 시 닫히지 않도록 방지
    $('.popup1').on('click', function (event) {
        event.stopPropagation();
    });

    // 검색 버튼 클릭 시 API 호출
    $(".search-button").click(function () {
        let query = $(".search-bar").val().trim();
        if (query === "") return;

        $.ajax({
            url: "/food/search",
            type: "GET",
            data: { query: query },
            success: function (response) {
                let tableBody = $(".search-results tbody");
                tableBody.empty(); // 기존 검색 결과 초기화

                response.forEach(food => {
                    let row = `
                        <tr data-menu="${food.menu}" data-kcal="${food.kcal}"
                            data-protein="${food.protein}" data-carb="${food.carbohydrate}"
                            data-fat="${food.fat}">
                            <td>${food.menu}</td>
                            <td>${food.kcal} kcal</td>
                            <td>단백질: ${food.protein}g | 탄수화물: ${food.carbohydrate}g | 지방: ${food.fat}g
                            <br>1인분 기준 : ${food.servingSize}</td>
                            <td>
                                <input type="number" class="food-amount" value="100" min="1" step="10"> g
                            </td>
                            <td><button class="add-food">추가</button></td>
                        </tr>
                    `;
                    tableBody.append(row);
                });
                $(".search-results").fadeIn();
            },
            error: function () {
                alert("음식 검색에 실패했습니다.");
            }
        });
    });

    // Enter 키 입력 시 실행
    $(".search-bar").keypress(function (event) {
        if (event.key === "Enter") {  // 또는 event.keyCode === 13 (옛날 방식)
            event.preventDefault(); // 폼 제출 방지
            $(".search-button").click(); // 검색 버튼 클릭 이벤트 실행
        }
    });

    // 검색 결과 클릭 시 선택 메뉴에 추가
    $(".search-results").on("click", ".add-food", function () {
        let row = $(this).closest("tr");
        let foodName = row.data("menu");
        let kcalPer100g = row.data("kcal");
        let proteinPer100g = row.data("protein");
        let carbohydratePer100g = row.data("carb");
        let fatPer100g = row.data("fat");

        let userAmount = row.find(".food-amount").val(); // 사용자가 입력한 g 단위
        let ratio = userAmount / 100; // 100g 대비 비율 계산

        let kcal = (kcalPer100g * ratio).toFixed(1);
        let protein = (proteinPer100g * ratio).toFixed(1);
        let carbohydrate = (carbohydratePer100g * ratio).toFixed(1);
        let fat = (fatPer100g * ratio).toFixed(1);

        const selectedText = `
        <span>${foodName} (${userAmount}g)</span>
        <span>${kcal} kcal</span>
        <span class="small-text">단백질: ${protein}g | 탄수화물: ${carbohydrate}g | 지방: ${fat}g</span>
    `;

        if (activeSlot) {
            // 현재 선택된 슬롯이 있다면 해당 슬롯을 변경
            activeSlot.html(selectedText);
            activeSlot.removeClass("selected"); // 변경 후 선택 해제
            activeSlot = null;
        } else {
            // 선택된 슬롯이 없으면 빈 슬롯에 추가
            const emptySlot = $(".selected-menu li:empty").first();
            if (emptySlot.length > 0) {
                emptySlot.html(selectedText);
            }
        }
    });

    // 선택된 슬롯 클릭 시 활성화
    $(".selected-menu li").click(function () {
        $(".selected-menu li").removeClass("selected");
        $(this).addClass("selected");
        activeSlot = $(this);
    });

    // 기록하기 버튼 클릭 이벤트
    $(".record-button").click(function () {
        let selectedFoods = [];
        let foodTypeCode = selectedFoodType === "아침" ? "B" : selectedFoodType === "점심" ? "L" : "D"; // B: 아침, L: 점심, D: 저녁

        $(".selected-menu li").each(function () {
            if ($(this).html().trim() !== "") {
                let foodData = {
                    menu: $(this).find("span:nth-child(1)").text().split(" (")[0], // 음식명
                    amount: parseFloat($(this).find("span:nth-child(1)").text().match(/\((\d+)g\)/)[1]), // 입력한 g 수량
                    kcal: parseFloat($(this).find("span:nth-child(2)").text().replace(" kcal", "")),
                    protein: parseFloat($(this).find(".small-text").text().match(/단백질: ([0-9.]+)g/)[1]),
                    carbohydrate: parseFloat($(this).find(".small-text").text().match(/탄수화물: ([0-9.]+)g/)[1]),
                    fat: parseFloat($(this).find(".small-text").text().match(/지방: ([0-9.]+)g/)[1]),
                };
                selectedFoods.push(foodData);
            }
        });

        if (selectedFoods.length === 0) {
            alert("선택된 음식이 없습니다.");
            return;
        }

        let today = new Date();
        today.setHours(today.getHours() + 9); // UTC+9 변환
        let requestData = {
            selectDate: today.toISOString().split("T")[0], // 변환된 날짜
            foodType: foodTypeCode,
            userPk: userPk,
            foodList: selectedFoods
        };

        $.ajax({
            url: "/food/record",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestData),
            success: function (response) {
                alert("식단이 성공적으로 저장되었습니다.");
                $(".selected-menu li").empty(); // UI 초기화
            },
            error: function () {
                alert("식단 저장에 실패했습니다.");
            }
        });
    });

});