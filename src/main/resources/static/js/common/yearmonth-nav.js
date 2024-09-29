document.addEventListener('DOMContentLoaded', () => {
    updateNextButtons(); // 다음 월 (>) 버튼 비활성화 설정
});

// 다음 월 (>) 버튼 비활성화 설정
function updateNextButtons() {
    const yearMonthInput = document.getElementById("yearmonth");
    let currentYearMonth = yearMonthInput.value.split('-');
    let year = parseInt(currentYearMonth[0]);
    let month = parseInt(currentYearMonth[1]);

    const nowYear = new Date().getFullYear();
    const nowMonth = new Date().getMonth() + 1;

    const nextBtn = document.getElementById('nextBtn');

    // 다음 버튼은 현재 월까지만 표시
    if (year >= nowYear && month >= nowMonth) {
        nextBtn.style.display = 'none';
    } else {
        nextBtn.style.display = 'block';
    }
}

// 검색 월 설정
function changeMonth(offset) {
    const yearMonthInput = document.getElementById("yearmonth");
    let currentYearMonth = yearMonthInput.value.split('-');
    let year = parseInt(currentYearMonth[0]);
    let month = parseInt(currentYearMonth[1]);

    month += offset;

    if (month < 1) {
        month = 12;
        year -= 1;
    } else if (month > 12) {
        month = 1;
        year += 1;
    }

    // 다시 년월 형식으로 포멧
    yearMonthInput.value = `${year}-${month.toString().padStart(2, '0')}`;

    // 새로운 월로 검색
    document.getElementById("yearMonthForm").submit();
}