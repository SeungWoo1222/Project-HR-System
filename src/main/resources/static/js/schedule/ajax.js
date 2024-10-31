// 수정 페이지 모달 열기
function goToUpdateForm(taskId) {
    if (confirm("일정을 수정하시겠습니까?")) {
        openModal('/schedule/' + taskId + '/edit');

        // 모달이 열리고 DOM이 로드될 시간을 주기 위해 약간의 지연을 둠
        setTimeout(function () {
            toggleDateTimeFields();
        }, 100); // 지연
    }
    return;
}

function checkForUpdates() {
    const inputs = document.querySelectorAll('#editForm input, #editForm textarea, #editForm select');
    let isChanged = false;

    inputs.forEach(input => {
        let currentValue = input.value;
        let defaultValue = input.defaultValue;

        // checkbox의 경우 checked 상태로 비교
        if (input.type === "checkbox") {
            currentValue = input.checked ? 'on' : 'off';
            defaultValue = input.defaultChecked ? 'on' : 'off';
        } else if (input.tagName === "SELECT") {
            // select 태그의 경우 selected된 값과 기본 selected 옵션을 비교
            const selectedOption = input.options[input.selectedIndex].value;
            currentValue = selectedOption;
            defaultValue = input.querySelector('option[selected]') ? input.querySelector('option[selected]').value : null;
        }

        if (currentValue !== defaultValue) {
            isChanged = true;
        }
    })

    // 출장 정보의 값 비교
    const addressInput = document.getElementById('sample6_address');
    const detailedAddressInput = document.getElementById('sample6_detailAddress');
    const tripNameInput = document.getElementById('tripName');
    const contactTelInput = document.getElementById('tripTel');
    const contactEmailInput = document.getElementById('emailLocalPart');

    if (hadTripInfo && addressInput.value) {
        if ( // 수정필요함 여기는
            addressInput.value !== addressInput.defaultValue ||
            detailedAddressInput.value !== detailedAddressInput.defaultValue ||
            tripNameInput.value !== tripNameInput.defaultValue ||
            contactTelInput.value !== contactTelInput.defaultValue ||
            contactEmailInput.value !== contactEmailInput.defaultValue
        ) {
            isChanged = true;  // 출장 정보가 변경된 경우
        }
    } else if (hadTripInfo && !addressInput.value || !hadTripInfo && addressInput.value) {
        isChanged = true;
    }

    if (!isChanged) {
        alert('변경 사항이 없습니다.');
    } else {
        return true;
    }
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

    // 유효성 검사 함수
    function showError(inputId, message, isBottomBorder = false) {
        const inputElement = document.getElementById(inputId);
        errorMessage.textContent = message;

        // 빨간 테두리와 흔들림 효과 추가
        if (isBottomBorder) {
            inputElement.classList.add("input-error-bottom", "shake");
        } else {
            inputElement.classList.add("input-error", "shake");
        }

        // 5초 후 빨간 테두리 제거
        setTimeout(() => {
            inputElement.classList.remove("input-error", "input-error-bottom");
        }, 5000);

        // 애니메이션이 끝난 후 흔들림 제거
        setTimeout(() => {
            inputElement.classList.remove("shake");
        }, 300);

        return false;
    }

    if (!taskName) {
        return showError('taskName', '일정 이름을 입력해주세요.', true);
    }

    if (!memberId) {
        return showError('memberId', '사원을 선택해주세요.');
    }

    if (allDay) {
        if (!startDate) {
            return showError('startDate', '시작일을 입력해주세요.', true);
        }
        if (!endDate) {
            return showError('endDate', '종료일을 입력해주세요.', true);
        }
    } else {
        if (!startDateTime) {
            return showError('startDateTime', '시작일을 입력해주세요.', true);
        }
        if (!endDateTime) {
            return showError('endDateTime', '종료일을 입력해주세요.', true);
        }
    }

    if ((allDay && startDate > endDate) || (!allDay && startDateTime > endDateTime)) {
        errorMessage.textContent = '시작일이 종료일보다 빠르거나 같아야 합니다.';
        return false;
    }

    if (!content.trim()) {
        return showError('content', '일정 내용을 입력해주세요.');
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

    // 출장 정보가 입력된 경우에만 추가
    const addressElement = document.getElementById('sample6_address');
    const address = addressElement ? addressElement.value : null;

    const detailedAddressElement = document.getElementById('sample6_detailAddress');
    const detailedAddress = detailedAddressElement ? detailedAddressElement.value : null;

    const tripNameElement = document.getElementById('tripName');
    const tripName = tripNameElement ? tripNameElement.value : null;

    const contactTelElement = document.getElementById('tripTel');
    const contactTel = contactTelElement ? contactTelElement.value : null;

    const contactEmail = getEmail().trim() || '';

    if (address || detailedAddress || tripName || contactTel) {
        if (!validateTripInfo()) {
            return;  // 유효성 검사 실패 시 종료
        }

        // 유효성 통과 시 출장 정보를 formData에 추가
        formData.append('address', address);
        formData.append('detailedAddress', detailedAddress);
        formData.append('tripName', tripName);
        formData.append('contactTel', contactTel);
        formData.append('contactEmail', contactEmail);
        if (document.getElementById('note') && document.getElementById('note').value.trim() !== '') {
            formData.append('note', document.getElementById('note').value);
        }
    }

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
                } else if (response.status === 422) { // 유효성 검사 오류 시
                    document.getElementById('error-message').textContent = response.text;
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
        startTime = startDate ? `${startDate}T00:00:00` : null;
        endTime = endDate ? `${endDate}T00:00:00` : null;
    } else {
        startTime = document.getElementById('startDateTime').value;
        endTime = document.getElementById('endDateTime').value;
    }

    // 폼 데이터 생성
    const formData = new FormData(form);

    // 기존 값 업데이트
    if (startTime) {
        formData.set("startTime", startTime);
    }
    if (endTime) {
        formData.set("endTime", endTime);
    }

    return formData;
}

// AJAX PUT 요청 - 일정 수정
function submitUpdateForm(event) {
    event.preventDefault();

    if (!checkForUpdates()) {
        return;
    }

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const actionUrl = form.action;
    const formData = createFormData(form);

    // 출장 정보가 입력된 경우에만 추가
    const addressElement = document.getElementById('sample6_address');
    const address = addressElement ? addressElement.value : null;

    const detailedAddressElement = document.getElementById('sample6_detailAddress');
    const detailedAddress = detailedAddressElement ? detailedAddressElement.value : null;

    const tripNameElement = document.getElementById('tripName');
    const tripName = tripNameElement ? tripNameElement.value : null;

    const contactTelElement = document.getElementById('tripTel');
    const contactTel = contactTelElement ? contactTelElement.value : null;

    if (address || detailedAddress || tripName || contactTel) {
        // if (!validateTripInfo()) {
        //     return;  // 유효성 검사 실패 시 종료
        // }
        const tripId = document.getElementById('tripId');
        if (tripId) {
            formData.append('tripId', tripId.value);
        }
        const contactEmail = getEmail().trim() || '';

        // 유효성 통과 시 출장 정보를 formData에 추가
        formData.append('address', address);
        formData.append('detailedAddress', detailedAddress);
        formData.append('tripName', tripName);
        formData.append('contactTel', contactTel);
        formData.append('contactEmail', contactEmail);
        if (document.getElementById('note') && document.getElementById('note').value.trim() !== '') {
            formData.append('note', document.getElementById('note').value);
        }

        formData.forEach((value, key) => {
            console.log(key + ': ' + value);
        });
    }

    // 일정 수정
    if (confirm('일정을 수정하시겠습니까?')) {
        fetch(actionUrl, {
            method: "PUT",
            body: formData
        }).then(response => response.text().then(data => ({
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
                } else if (response.status === 422) { // 유효성 검사 오류 시
                    document.getElementById('error-message').textContent = response.text;
                } else {
                    alert('일정 수정 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX DELETE 요청 - 일정 삭제
function deleteSchedule(taskId) {
    if (confirm("일정을 삭제하시겠습니까?")) {
        fetch(`/schedule/delete/${taskId}`, {
            method: "DELETE"
        }).then(response => response.text().then(data => ({
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
                    alert('일정 삭제 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX PATCH 요청 - 일정 상태 변경
function updateScheduleStatus(taskId) {
    if (confirm("일정 상태를 변경하시겠습니까?")) {
        const selectedStatus = document.querySelector('input[name="scheduleStatus"]:checked').value;

        // 폼데이터 생성
        const formData = new FormData();
        formData.append("status", selectedStatus);
        if (selectedStatus === "완료") {
            const taskName = document.getElementById('taskName');
            console.log(taskName);
            console.log(taskName.value);
            formData.append("taskName", taskName.value);
        }

        formData.forEach((value, key) => {
            console.log(key, value);
        });

        fetch(`/schedule/status/${taskId}`, {
            method: "PUT",
            // headers: {
            //     'Content-Type': 'application/json',
            // },
            body: formData
        }).then(response => response.text().then(data => ({
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
                    alert('일정 상태 변경 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });

        // 스케줄을 완료했다면 보고서 생성 알림 생성
        // const scheduleStatus = document.querySelector('input[name="scheduleStatus"]:checked').value;;
        // console.log("scheduleStatus : ", scheduleStatus);
        // if (scheduleStatus == "완료") {
        //     const taskId = document.getElementById('taskId');
        //     const formData = ('taskId', taskId);
        //
        //     fetch(`/report/writeFromSchedule`, {
        //         method: "POST",
        //         headers: {
        //             'Content-Type': 'application/json',
        //         },
        //         body: formData
        //     }).then(response => response.text().then(data => ({
        //         status: response.status,
        //         text: data
        //     })))
        //         .then(response => {
        //             const errorStatuses = [400, 403, 404, 500];
        //             if (response.status === 200) {
        //                 alert(response.text);
        //                 window.location.reload();
        //             } else if (errorStatuses.includes(response.status)) {
        //                 alert(response.text);
        //             } else {
        //                 alert('일정 알림 생성 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
        //                 window.location.reload();
        //             }
        //         })
        //         .catch(error => {
        //             console.error('Error :', error.message);
        //             alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        //         });
        // }
    }
}

// 출장 상태 변경
function updateTripStatus(tripId) {
    if (confirm("출장 상태를 변경하시겠습니까?")) {
        // 선택된 출장 상태값 가져오기
        const selectedTripStatus = document.querySelector('input[name="tripStatus"]:checked').value;

        fetch(`/schedule/tripStatus/${tripId}`, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({status: selectedTripStatus}) // 상태값을 JSON으로 전송
        }).then(response => response.text().then(data => ({
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
                    alert('출장 상태 변경 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}