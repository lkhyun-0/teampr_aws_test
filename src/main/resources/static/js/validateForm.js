function validateForm() {
    let title = document.getElementById("title");
    let content = document.getElementById("content");

    if (title.value.trim() === "") {
        alert("제목을 입력해주세요.");
        title.focus();
        return false;
    }
    if (content.value.trim() === "") {
        alert("내용을 입력해주세요.");
        content.focus();
        return false;
    }

    return true;
}