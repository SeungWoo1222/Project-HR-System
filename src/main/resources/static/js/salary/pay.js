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
            console.log('서버 응답 데이터 :', response.text);
            if (response.status === 200) {
                alert(response.text);

                // GET 요청을 위한 URL 쿼리 파라미터 생성
                const params = new URLSearchParams();
                formData.forEach((value, key) => {
                    params.append(key, value);
                });

                var modal = document.getElementById("myModal");
                var modalBody = document.getElementById("modal-body");

                // 컨텐츠 로드
                fetch('/salary/payment/complete?' + params.toString(), {
                    method: 'GET'
                })
                    .then(response => {
                        if (response.status === 404) {
                            return fetch('/error/modal/404').then(res => res.text());
                        }
                        if (response.status === 401) {
                            return fetch('/error/modal/401').then(res => res.text());
                        }
                        if (response.status === 403) {
                            return fetch('/error/modal/403').then(res => res.text());
                        }
                        if (response.status === 500) {
                            return fetch('/error/modal/500').then(res => res.text());
                        }
                        if (!response.ok) { // 다른 HTTP 오류 처리
                            throw new Error('서버 오류 발생: ' + response.status);
                        }
                        return response.text();
                    })
                    .then(html => {
                        modalBody.innerHTML = html;
                        modal.style.display = "block";
                    });
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