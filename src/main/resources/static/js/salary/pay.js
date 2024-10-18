document.addEventListener('DOMContentLoaded', () => {
    formatSalaries(); // 테이블의 모든 연봉 값을 포맷팅 (3자리마다 ',' 삽입)
});

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

    openModal(url);  // openModal 함수 호출
}

// 사원 정보 상세보기 페이지 모달로 열기
function openEmployeeDetailModal(event, button) {
    event.stopPropagation();
    const employeeId = button.getAttribute('employeeId');
    openModal('/employee/' + employeeId);
}

// AJAX 요청 - 급여 지급 및 급여명세서 등록
function submitPayForm(event) {
    event.preventDefault();

    // 사용자 확인
    const userConfirmed = confirm("급여를 지급하시겠습니까? 이 작업은 되돌릴 수 없습니다.");
    if (!userConfirmed) {
        return;
    }

    const form = event.currentTarget;
    const formData = new FormData(form);

    const actionUrl = form.action;
    fetch(actionUrl, {
        method: 'POST',
        body: formData
    })
        .then(response => response.text().then(data => ({
            status: response.status,
            text: data
        })))
        .then(response => {
            if (response.status === 200) {
                alert(response.text);
                window.location.href = '/salary/payment/list';
            } else {
                alert('급여 지급 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                window.location.reload();
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}