// 수정 페이지 모달 열기
function goToUpdateOvertimeForm(overtimeId) {
    if (confirm("초과근무 정보를 수정하시겠습니까?")) openModal('/overtime/' + overtimeId);
}

// AJAX PUT 요청 - 초과근무 수정
function submitUpdateOvertimeForm(event) {
    event.preventDefault();

    // 유효성 검사
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;

    const timeRegex = /^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$/;

    // 오류 메시지 초기화
    const errorMessage = document.getElementById('error-message');
    errorMessage.textContent = '';

    if (!startTime.match(timeRegex)) {
        errorMessage.textContent = "시작 시간은 'HH:mm:ss' 형식이어야 합니다.";
        return;
    }

    if (!endTime.match(timeRegex)) {
        errorMessage.textContent = "종료 시간은 'HH:mm:ss' 형식이어야 합니다.";
        return;
    }

    errorMessage.textContent = '';

    const form = event.target;
    const formData = new FormData(form);
    const actionUrl = form.action;

    if (confirm('초과근무 정보를 수정하시겠습니까?')) {
        fetch(actionUrl, {
            method: 'PUT',
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
                    alert('초과근무 정보 수정 중 오류가 발생하였습니다.\n재수정 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX DELETE 요청 - 휴가 정보 삭제
function deleteOvertime(overtimeId) {
    if (confirm('초과근무 정보를 정말 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.\n이 작업을 계속하시려면 확인을 눌러주세요.')) {
        fetch('/api/overtime/' + overtimeId, {
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
                    alert('초과근무 삭제 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}