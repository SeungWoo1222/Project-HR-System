// 수정 페이지 모달 열기
function goToUpdateForm(vacationId) {
    console.log(vacationId);
    if (confirm("휴가 신청서를 수정하시겠습니까?")) {
        openModal('/vacation/' + vacationId + '/edit');
    }
    return;
}

// 유효성 검사
function validateForm() {
    const vacationType = document.getElementById("vacationType").value;
    const startAt = document.getElementById("startAt").value;
    const endAt = document.getElementById("endAt").value;
    const usedDays = document.getElementById("usedDays").value;
    const reason = document.getElementById("reason").value;
    const remainingLeave = document.getElementById("remainingLeave").textContent;

    // 휴가 유형 체크
    if (!vacationType) {
        document.getElementById("error-message").textContent = "휴가 유형을 선택해주세요.";
        return false;
    }

    // 시작일과 종료일 체크
    if (!startAt || !endAt) {
        document.getElementById("error-message").textContent = "휴가 시작일과 종료일을 입력해주세요.";
        return false;
    }

    // 사용 일수가 잔여 연차보다 많으면 오류 메세지 출력
    if (parseFloat(usedDays) > parseFloat(remainingLeave)) {
        document.getElementById("error-message").textContent = "사용 일수가 잔여 연차보다 많습니다.";
        return false;
    }

    // 사용 일수 체크
    if (parseFloat(usedDays) < 0) {
        document.getElementById("error-message").textContent = "유효한 연차 사용 일수를 입력해주세요.";
        return false;
    }

    // 휴가 사유 체크
    if (!reason.trim()) {
        document.getElementById("error-message").textContent = "휴가 사유를 입력해주세요.";
        return false;
    }

    // 오류 메시지 없애기
    document.getElementById("error-message").textContent = "";
    return true;
}

// 날짜 필드 수정 후 폼데이터 반환
function createFormData(form) {
    // 날짜 필드의 값 가져와 (현재 yyyy-mm-dd 형식)
    const startDate = document.getElementById("startAt").value;
    const endDate = document.getElementById("endAt").value;
    let vacationType = document.getElementById("vacationType").value;

    // LocalDateTime 형식으로 변경
    let startAt;
    let endAt;
    switch (vacationType) {
        case '오전 반차':
            startAt = `${startDate}T09:00:00`;
            endAt = `${endDate}T14:00:00`;
            vacationType = '연차';
            break;
        case '오후 반차':
            startAt = `${startDate}T13:00:00`;
            endAt = `${endDate}T18:00:00`;
            vacationType = '연차';
            break;
        default:
            startAt = `${startDate}T09:00:00`;
            endAt = `${endDate}T18:00:00`;
            break;
    }

    // 폼 데이터 생성
    const formData = new FormData(form);

    // 기존 값 덮어쓰기
    formData.set("startAt", startAt);
    formData.set("endAt", endAt);
    formData.set("vacationType", vacationType);

    return formData;
}

// AJAX POST 요청 - 휴가 신청
function submitInsertForm(event, requestor) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const actionUrl = form.action;

    const formData = createFormData(form);

    if (confirm('휴가를 신청하시겠습니까?')) {
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
                    if (requestor === 'employee') window.location.href = '/vacation/employee';
                    else {
                        window.location.href = '/vacation/list';
                    }
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('휴가 신청 중 오류가 발생하였습니다.\n재신청 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX PUT 요청 - 휴가 정보 수정
function submitUpdateForm(event) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const actionUrl = form.action;

    const formData = createFormData(form);

    if (confirm('휴가 신청 내용을 수정하시겠습니까?')) {
        fetch(actionUrl, {
            method: "PUT",
            body: formData
        })
            .then(response => {
                if (response.ok) { // 성공 시 JSON 데이터 처리
                    return response.json().then(data => ({
                        status: response.status,
                        data: data
                    }));
                } else { // 실패 시 텍스트 데이터 처리
                    return response.text().then(data => ({
                        status: response.status,
                        text: data
                    }));
                }
            })
            .then(response => {
                if (response.status === 200) {
                    alert(response.data.message);
                    window.location.href = response.data.url;
                } else {
                    const errorStatuses = [400, 403, 404, 500];
                    if (errorStatuses.includes(response.status)) {
                        alert(response.text);
                    } else {
                        alert('휴가 수정 중 오류가 발생하였습니다.\n재수정 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                        window.location.reload();
                    }
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX DELETE 요청 - 휴가 정보 삭제
function deleteVacation(vacationId) {
    if (confirm('휴가 정보를 정말 삭제하시겠습니까?')) {
        fetch('/api/vacation/' + vacationId, {
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
                    alert('휴가 신청 중 오류가 발생하였습니다.\n재신청 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX PATCH 요청 - 휴가 처리
function processVacation(vacationId, approvalStatus) {
    if (confirm('휴가(\'' + vacationId +  '\')를 '+ approvalStatus + '하시겠습니까?')) {
        fetch('/api/vacation/' + vacationId, {
            method: "PATCH",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: approvalStatus })
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
                    alert('휴가 처리 중 오류가 발생하였습니다.\n재신청 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}
