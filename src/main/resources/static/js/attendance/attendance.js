// 출근 퇴근 조퇴 모달 열기
function openAttendanceModal(url) {
    var modal = document.getElementById("myModal");
    var modalBody = document.getElementById("modal-body");

    // 컨텐츠 로드
    fetch(url)
        .then(response => {
            if (response.status === 400) {
                response.text().then(errorMessage => {
                    alert(errorMessage);
                });
            }
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
        // .then(response => response.text())
        .then(html => {
            modalBody.innerHTML = html;
            modal.style.display = "flex";

            // 확대 애니메이션을 위해 'show' 클래스 추가
            setTimeout(() => {
                modal.classList.add("show");
            }, 10);

            // 페이지 로드 시 즉시 시간을 업데이트하고, 1초마다 갱신
            setInterval(updateTime, 1000);
            updateTime();  // 페이지가 로드되자마자 시간을 표시
        });

}

// 실시간 업데이트
function updateTime() {
    const currentTimeElement = document.getElementById("currentTime");
    const now = new Date();

    // 12시간 형식으로 시간 변환
    let hours = now.getHours();
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    const ampm = hours >= 12 ? 'PM' : 'AM';

    // 12시간 형식 적용 (0시는 12시로 처리)
    hours = hours % 12;
    hours = hours ? hours : 12; // 0시를 12시로 표시

    // 시간을 AM/PM과 함께 표시
    currentTimeElement.innerHTML = `${ampm} ${String(hours).padStart(2, '0')}:${minutes}:${seconds}`;
}

// AJAX 요청 - 출근, 퇴근
function checkInAndOut(method, message) {
    if (confirm(message + ' 체크를 하시겠습니까?')) {
        fetch('/api/attendance', {
            method: method
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
                    alert(message + ' 체크 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX 요청 - 조퇴
function earlyLeave(event) {
    event.preventDefault(); // 폼 제출 기본 동작 막기

    const notes = document.getElementById('notes').value.trim(); // 조퇴 사유 입력값 가져오기

    // 유효성 검사
    if (notes === '') {
        alert('조퇴 사유를 입력해주세요.');
        return;
    }

    if (confirm('조퇴 하시겠습니까?\n조퇴 전 반드시 관리자에게 보고해주세요.\n미보고 시 무단 조퇴 처리될 수 있습니다.')) {
        fetch('/api/attendance/earlyLeave', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ notes: notes })
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
                    alert('조퇴 처리 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// 수정 페이지 모달 열기
function goToUpdateForm(attendanceId) {
    if (confirm("근태 정보를 수정하시겠습니까?")) openModal('/attendance/' + attendanceId + '/edit');
}

// AJAX PUT 요청 - 근태 수정
function submitUpdateForm(event) {
    event.preventDefault();

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

    // 유효성 검사
    const checkIn = document.getElementById('checkIn').value;
    const checkOut = document.getElementById('checkOut').value;
    const status = document.getElementById('status').value;

    const timeRegex = /^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$/;

    // 오류 메시지 초기화
    const errorMessage = document.getElementById('error-message');
    errorMessage.textContent = '';

    if (!checkIn.match(timeRegex)) {
        return showError('checkIn', '출근 시간은 \'HH:mm:ss\' 형식이어야 합니다.', true);
    }

    if (!checkOut.match(timeRegex)) {
        return showError('checkOut', '퇴근 시간은 \'HH:mm:ss\' 형식이어야 합니다.', true);
    }

    if (!status) {
        return showError('status', '근태 상태를 선택해주세요.');
    }

    errorMessage.textContent = '';

    const form = event.target;
    const formData = new FormData(form);
    const actionUrl = form.action;

    if (confirm('근태 정보를 수정하시겠습니까?')) {
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
                    alert('근태 정보 수정 중 오류가 발생하였습니다.\n재수정 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

