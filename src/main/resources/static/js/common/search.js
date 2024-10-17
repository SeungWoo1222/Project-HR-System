// 날짜 검색 유효성 검사
function validateSearchDate() {
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    // 시작일이나 종료일이 입력되지 않았을 경우
    if (!startDate) {
        showError("startDate");
        return false;
    }
    if (!endDate) {
        showError("endDate");
        return false;
    }

    // 시작일이 종료일 이후일 경우
    if (startDate > endDate) {
        showError("startDate");
        showError("endDate");
        return false;
    }

    return true;
}

// 유효성 검사 함수
function showError(inputId) {
    const inputElement = document.getElementById(inputId);

    // 빨간 테두리와 흔들림 효과 추가
    inputElement.classList.add("input-error", "shake");

    // 5초 후 빨간 테두리 제거
    setTimeout(() => {
        inputElement.classList.remove("input-error", "input-error-bottom");
    }, 5000);

    // 애니메이션이 끝난 후 흔들림 제거
    setTimeout(() => {
        inputElement.classList.remove("shake");
    }, 300);
}