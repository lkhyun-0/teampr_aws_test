function recom(element) {

    const articlePk = element.getAttribute("data-id");

    $.ajax({
        url: '/recommend/toggle',
        type: 'POST',
        data: { articlePk: articlePk },
        dataType: 'json',
        success: function(response) {
            // 추천 수 업데이트
            alert('추천되었습니다.');
            $('#recomCount').text("추천 수: " + response.count);
        },
        error: function(xhr, status, error) {
            if (xhr.status === 401) {
                alert(xhr.responseText); // 서버에서 보낸 메시지 출력
            } else {
                alert('에러가 발생했습니다.');
            }
        }
    });
}

function deleteArticle(element) {
    const articlePk = element.getAttribute("data-id");

    if (!confirm("삭제하시겠습니까?")) {
        return;
    }

    fetch(`/free/freeDelete/${articlePk}`, {
        method: "Post"
    }).then(response => {
        if (response.ok) {
            alert("삭제되었습니다.");
            window.location.href="/free/freeList";
        } else {
            alert("삭제 중 오류 발생")
        }
    }).catch(error => console.error("Error:", error));
}