// 사원 정보 수정 페이지 이동 메소드
function editEmployee(button) {
    var employeeId = button.getAttribute('employeeId');
    window.location.href = '/employee/' + employeeId + '/edit';
}
// 퇴사 사원 정보 수정 페이지 이동 메소드
function editResignedEmployee(button) {
    var employeeId = button.getAttribute('employeeId');
    window.location.href = '/resignation/' + employeeId + '/edit';
}

// 계정 잠금과 잠금 해제하는 메소드들
function unlockAccount(button) {
    var employeeId = button.getAttribute('employeeId');
    var name = button.getAttribute('name');
    var confirmMessage = '\'' + name + '\' 사원을 정말 잠금 해제하시겠습니까?';
    setAccountLock(confirmMessage, employeeId, name);
}
function lockAccount(button) {
    var employeeId = button.getAttribute('employeeId');
    var name = button.getAttribute('name');
    var confirmMessage = '\'' + name + '\'의 계정을 정말 잠금 처리하시겠습니까?';
    setAccountLock(confirmMessage, employeeId, name);
}
function setAccountLock(confirmMessage, employeeId, name) {
    if (confirm(confirmMessage)) {
        fetch('/api/employee/' + employeeId + '/accountLock', {
            method: 'PATCH'
        })
            .then(response => {
                return response.text().then(text => ({
                    status: response.status,
                    text: text
                }));
            })
            .then(response => {
                console.log('서버 응답 데이터 :', response.text);
                if (response.status === 200) {
                    alert('\'' + name + '\' ' + response.text); // 성공 메시지 알림
                    window.location.reload();
                } else if (response.status === 500) {
                    alert(response.text); // 500 오류 메시지 알림
                } else {
                    alert('사원 잠금 처리 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// 재직상태 변경하는 메소드
function updateStatus(event) {
    const form = event.target.closest('form');
    const formData = new FormData(form);
    const statusToBeUpdated = formData.get("status");

    var confirmMessage = "재직 상태를 '" + statusToBeUpdated + "'으로 변경하시겠습니까?";
    const actionUrl = form.action;
    if (confirm(confirmMessage)) {
        fetch(actionUrl, {
            method: 'PATCH',
            body: formData
        })
            .then(response => {
                return response.text().then(text => ({
                    status: response.status,
                    text: text
                }));
            })
            .then(response => {
                console.log('서버 응답 데이터 :', response.text);
                if (response.status === 200) {
                    alert(response.text); // 성공 메시지 알림
                    window.location.href = '/employee/list';
                } else if (response.status === 500) {
                    alert(response.text); // 500 오류 메시지 알림
                } else {
                    alert('재직 상태 수정 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// 사원 승진 처리하는 메소드
function promoteEmployee(button) {
    const employeeId = button.getAttribute('employeeId');
    const name = button.getAttribute('name');
    const position = button.getAttribute('position');

    var confirmMessage = "'" + name + "' 사원의 현재 직급은 '" + position + "'입니다.\n" + "해당 사원의 직급을 승진 처리하시겠습니까?";
    if (confirm(confirmMessage)) {
        fetch('/api/admin/employee/' + employeeId + '/promote', {
            method: 'PATCH',
        })
            .then(response => {
                return response.text().then(text => ({
                    status: response.status,
                    text: text
                }));
            })
            .then(response => {
                console.log('서버 응답 데이터 :', response.text);
                if (response.status === 200) {
                    alert(response.text); // 성공 메시지 알림
                    window.location.href = '/employee/list';
                } else if (response.status === 500) {
                    alert(response.text); // 500 오류 메시지 알림
                } else {
                    alert('사원의 승진 처리 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}