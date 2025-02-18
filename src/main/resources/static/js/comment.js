// 댓글 등록 이벤트
$('#comment-submit').on('click', function(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const content = $('.comment-input').val(); // 입력된 댓글 내용

    // 비로그인 사용자 차단
    if (window.userPk === null || window.userPk === 'null') {
        alert("로그인이 필요합니다.");
        return;
    }

    if (content.trim() === '') {
        alert("댓글을 입력하세요.");
        return;
    }

    $.ajax({
        url: '/comment/addComment',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            userPk: window.userPk,
            articlePk: window.articlePk,
            content: content
        }),
        success: function(response) {
            alert("댓글이 추가되었습니다.")
            appendComment(response); // 새 댓글 추가
            $('.comment-input').val(''); // 입력창 비우기
        },
        error: function(xhr, status, error) {
            if (xhr.status === 500) {
                alert("서버 내부 오류로 인해 댓글 저장에 실패했습니다.");
            } else {
                alert("오류 발생: " + xhr.responseText);
            }
        }
    });
});

// 댓글 삭제 함수
function deleteComment(element) {
    const commentPk = element.getAttribute("data-id");

    if (!confirm("삭제하시겠습니까?")) {
        return;
    }

    $.ajax({
        url:'/comment/deleteComment',
        type: 'POST',
        data: {commentPk : commentPk},
        success: function (response) {
            alert(response); // "댓글이 삭제되었습니다."
            $(`button[data-id="${commentPk}"]`).closest('.comment-item').remove();
        },
        error: function (xhr) {
            alert(xhr.responseText);
        }
    })
}

// 댓글을 화면에 추가하는 함수
function appendComment(comment) {
    $('.comment-list').append(`
        <div class="comment-item">
            <div class="comment-left">
                <div class="comment-author">${comment.userNick}</div>
                <div class="comment-text">${comment.content}</div>
            </div>
            <div class="comment-right">
                <div class="comment-date">${comment.regDate}</div>
                <button class="comment-delete" data-id="${comment.commentPk}">×</button>
            </div>
        </div>
    `);
}
