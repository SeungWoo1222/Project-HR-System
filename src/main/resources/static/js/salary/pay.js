document.addEventListener('DOMContentLoaded', () => {
    formatSalaries(); // 테이블의 모든 연봉 값을 포맷팅 (3자리마다 ',' 삽입)
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
    if (year >= nowYear  &&  month >= nowMonth) {
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

// 각 행 클릭 시 체크박스 선택/해제
function toggleCheckbox(row) {
    if (!row) {
        console.error('Row is undefined or null:', row);
        return;
    }

    const checkbox = row.querySelector('.pay-checkbox');
    if (checkbox) {
        checkbox.checked = !checkbox.checked;
    } else {
        console.error('Checkbox not found in row:', row);
    }
}

//
function submitSelectedSalaries() {
    const selectedSalaries = [];
    document.querySelectorAll('.pay-checkbox:checked').forEach(checkbox => {
        selectedSalaries.push(checkbox.getAttribute('data-salary-id'));
    });

    console.log(selectedSalaries);

    if (selectedSalaries.length === 0) {
        alert("급여를 지급할 사원을 선택해 주세요.");
        return;
    }

    // yearmonth 값을 가져옴
    const yearmonth = document.querySelector('input[name="yearmonth"]').value;

    // URLSearchParams를 사용하여 salaryIds와 yearmonth를 추가
    const params = new URLSearchParams();
    params.append('salaryIds', selectedSalaries.join(','));
    params.append('yearmonth', yearmonth);

    const url = '/salary/payment/confirm?' + params.toString();

    console.log(url);
    openModal(url);  // openModal 함수 호출
}

// 사원 정보 상세보기 페이지 모달로 열기
function openEmployeeDetailModal(event, button) {
    event.stopPropagation();
    const employeeId = button.getAttribute('employeeId');
    openModal('/employee/' + employeeId);
}