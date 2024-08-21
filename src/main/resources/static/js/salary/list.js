// 연봉 포맷팅 함수
function formatAnnualSalary(value) {
    return value.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// 테이블의 모든 연봉 값을 포맷팅 (3자리마다 ',' 삽입)
function formatSalaries() {
    const salaryCells = document.querySelectorAll('.annualSalary');
    salaryCells.forEach(cell => {
        const rawValue = cell.textContent.trim();
        cell.textContent = formatAnnualSalary(rawValue);
    });
}

// 급여정보 등록 페이지로 이동
function goToRegisterForm(event, employeeId) {
    event.stopPropagation();
    window.location.href='/salary/register?employeeId=' + employeeId;
}