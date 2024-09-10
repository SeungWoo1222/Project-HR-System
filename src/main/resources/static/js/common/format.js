// 날짜 문자열 'T' 제거 함수
function formatDateTime(dateTimeString) {
    return dateTimeString.replace('T', ' ');
}

// 페이지에서 모든 날짜 문자열 'T' 제거 함수
function updateDateTimeElements() {
    const dateTimeElements = document.querySelectorAll('.dateTime');
    dateTimeElements.forEach(element => {
        element.textContent = formatDateTime(element.textContent);
    });
}