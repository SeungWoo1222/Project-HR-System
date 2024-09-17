// 진승우씨가 채워주시면 됩니다

// 수정 페이지 모달 열기
function goToUpdateForm(taskId) {
    if (confirm("일정을 수정하시겠습니까?")) {
        openModal('/schedule/' + taskId + '/edit');

        // 모달이 열리고 DOM이 로드될 시간을 주기 위해 약간의 지연을 둠
        setTimeout(function() {
            toggleDateTimeFields();
        }, 100); // 지연
    }
    return;
}

// 사원이 선택되면 서버로 API 요청해서 사원 정보를 받아와서 업데이트
function fetchEmployeeInfo(employeeId) {
    fetch(`/api/employee/` + employeeId)
        .then(response => {
            if (!response.ok) {
                alert('사원 정보를 불러오는데 실패했습니다.');
            }
            return response.json();
        })
        .then(employee => {
            // 사원 정보를 input 필드에 업데이트
            document.getElementById("name").textContent = employee.name;
            document.getElementById("department").textContent = employee.department;
            document.getElementById("position").textContent = employee.position;
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}

// 유효성 검사
function validateForm() {
    const taskName = document.getElementById('taskName').value.trim();
    const memberId = document.getElementById('memberId').value;
    const startDateTime = document.getElementById('startDateTime').value;
    const endDateTime = document.getElementById('endDateTime').value;
    const content = document.getElementById('content').value;
    const allDay = document.getElementById('allDay').checked;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    // 오류 메시지 초기화
    const errorMessage = document.getElementById('error-message');
    errorMessage.textContent = '';

    if (!taskName) {
        errorMessage.textContent = '일정 이름을 입력해주세요.';
        return false;
    }

    if (!memberId) {
        errorMessage.textContent = '사원을 선택해주세요.';
        return false;
    }

    if (!content.trim()) {
        errorMessage.textContent = '일정 내용을 입력해주세요.';
        return false;
    }

    if (allDay) {
        if (!startDate) {
            errorMessage.textContent = '시작일을 입력해주세요.';
            return false;
        }
        if (!endDate) {
            errorMessage.textContent = '종료일을 입력해주세요.';
            return false;
        }
    } else {
        if (!startDateTime) {
            errorMessage.textContent = '시작일을 입력해주세요.';
            return false;
        }
        if (!endDateTime) {
            errorMessage.textContent = '종료일을 입력해주세요.';
            return false;
        }
    }

    if ((allDay && startDate > endDate) || (!allDay && startDateTime > endDateTime)) {
        errorMessage.textContent = '시작일이 종료일보다 빠르거나 같아야 합니다.';
        return false;
    }

    errorMessage.textContent = '';
    return true;
}

// AJAX POST 요청 - 일정 등록
function submitInsertForm(event) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const actionUrl = form.action;

    const formData = createFormData(form);

    // 일정 등록
    if (confirm('새로운 일정을 등록하시겠습니까?')) {
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
                    alert('일정 등록 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// formData 생성
function createFormData(form) {
    // 하루종일 체크박스 값 가져오기
    const allDayCheckbox = document.getElementById('allDay');

    // 하루종일 체크 여부에 따라 startTime과 endTime 값 설정
    let startTime;
    let endTime;
    if (allDayCheckbox.checked) {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        startTime = `${startDate}T00:00:00`;
        endTime = `${endDate}T00:00:00`;
    } else {
        startTime = document.getElementById('startDateTime').value;
        endTime = document.getElementById('endDateTime').value;
    }

    // 폼 데이터 생성
    const formData = new FormData(form);

    // 기존 값 업데이트
    formData.set("startTime", startTime);
    formData.set("endTime", endTime);

    return formData;
}

// AJAX PUT 요청 - 일정 수정
function submitUpdateForm(event) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const actionUrl = form.action;

    const formData = createFormData(form);

    // 일정 수정
    if (confirm('일정을 수정하시겠습니까?')) {
        fetch(actionUrl, {
            method: "PUT",
            body: formData
        })
        // 미완성 - 구현 필요
    }
}

// AJAX DELETE 요청 - 일정 삭제
function deleteSchedule(taskId) {

}

// AJAX PATCH 요청 - 일정 상태 변경
// 메소드 필요