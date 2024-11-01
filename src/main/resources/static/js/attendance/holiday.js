document.addEventListener('DOMContentLoaded', () => {
    updateNextButtons(); // 다음 월 (>) 버튼 비활성화 설정
});

// 다음 월 (>) 버튼 비활성화 설정
function updateNextButtons() {
    const yearInput = document.getElementById("year");
    let year = parseInt(yearInput.value);

    const nowYear = new Date().getFullYear();

    const nextBtn = document.getElementById('nextBtn');

    // 다음 버튼은 현재 년까지만 표시
    if (year >= nowYear) {
        nextBtn.style.display = 'none';
    } else {
        nextBtn.style.display = 'block';
    }
}

// 검색 월 설정
function changeYear(offset) {
    const yearInput = document.getElementById("year");
    let year = parseInt(yearInput.value);

    year += offset;

    // 다시 연도 형식으로 포멧
    yearInput.value = `${year}`;

    // 새로운 연도로 검색
    document.getElementById("yearForm").submit();
}

// AJAX POST 요청 - 연도 단위 공휴일 생성
function submitYearForm(event) {
    event.preventDefault();

    const year = document.getElementById("yearInput").value;

    // 정규식 - 숫자 4자리만 허용
    const yearPattern = /^\d{4}$/;

    const errorMessage = document.getElementById("error-message");
    errorMessage.textContent = ""; // 오류 메시지 초기화

    // 유효성 검사
    if (!yearPattern.test(year)) {
        errorMessage.textContent = "유효한 4자리 연도를 입력해주세요.";
        return;
    }
    errorMessage.textContent = ""; // 오류 메시지 초기화

    const actionUrl = event.target.action;

    if (confirm(year + '년도 공휴일을 생성하시겠습니까?')) {
        fetch(actionUrl + year, {
            method: "POST"
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    window.location.reload();
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('공휴일 생성 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// 공휴일 유효성 검사
function validateForm() {
    const dateName = document.getElementById("dateName").value;
    const locDate = document.getElementById("locDate").value;

    const errorMessage = document.getElementById("error-message");

    errorMessage.textContent = "";

    if (!dateName) {
        document.getElementById("error-message").textContent = "공휴일 이름을 입력해주세요.";
        return false;
    }

    if (!locDate) {
        document.getElementById("error-message").textContent = "공휴일 날짜를 선택해주세요.";
        return false;
    }

    // 오류 메시지 없애기
    errorMessage.textContent = "";
    return true;
}

// AJAX POST 요청 - 공휴일 생성
function submitInsertForm(event) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const formData = new FormData(form);
    const actionUrl = form.action;

    if (confirm("새로운 공휴일을 추가하시겠습니까?")) {
        fetch(actionUrl, {
            method: "POST",
            body: formData
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    window.location.reload();
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('공휴일 추가 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}


// AJAX PUT 요청 - 공휴일 수정
function submitUpdateForm(event) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const formData = new FormData(form);
    const actionUrl = form.action;

    if (confirm("공휴일을 수정하시겠습니까?")) {
        fetch(actionUrl, {
            method: "PUT",
            body: formData
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    window.location.reload();
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('공휴일 수정 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX DELETE 요청 - 공휴일 삭제
function deleteHoliday(dateName, holidayId) {
    if (confirm(dateName + "('" + holidayId + "')을 정말 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.\n이 작업을 계속하시려면 확인을 눌러주세요.")) {
        fetch('/api/holiday/' + holidayId, {
            method: "DELETE"
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    window.location.reload();
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('공휴일 삭제 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}