// 퇴사 사유 선택시 코드 번호 추가
function updateCodeNumber() {
    var resignationReason = document.getElementById('resignationReason').value;
    var codeNumber = document.getElementById('codeNumber');

    // 코드 번호 초기화
    codeNumber.innerHTML = '<option value="">선택하세요</option>';

    if (resignationReason === "1") {
        codeNumber.innerHTML += '<option value="11">11. 개인사정으로 인한 자진퇴사</option>';
        codeNumber.innerHTML += '<option value="12">12. 사업장 이전, 근로조건(계약조건) 변동, 임금체불 등으로 자진퇴사</option>';
    } else if (resignationReason === "2") {
        codeNumber.innerHTML += '<option value="22">22. 폐업, 도산(예정 포함), 공사 중단</option>';
        codeNumber.innerHTML += '<option value="23">23. 경영상 필요 및 회사 불황으로 인원 감축 등에 의한 퇴사 (해고•권고사직•계약파기 포함)</option>';
        codeNumber.innerHTML += '<option value="26">26. 피보험자의 귀책사유에 의한 징계해고•권고사직 또는 계약 파기</option>';
    } else if (resignationReason === "3") {
        codeNumber.innerHTML += '<option value="31">31. 정년</option>';
        codeNumber.innerHTML += '<option value="32">32. 계약기간만료, 공사 종료</option>';
    } else if (resignationReason === "4") {
        codeNumber.innerHTML += '<option value="41">41. 고용보험 비적용</option>';
        codeNumber.innerHTML += '<option value="42">42. 이중고용</option>';
    }
}

// 퇴사 처리 폼 유효성 검사
function validateForm() {
    var resignationDate = document.getElementById('resignationDate').value;
    var resignationReason = document.getElementById('resignationReason').value;
    var codeNumber = document.getElementById('codeNumber').value;
    var specificReason = document.getElementById('specificReason').value;
    var errorMessage = document.getElementById('error-message');

    // 에러 메시지를 초기화
    errorMessage.textContent = '';

    if (resignationReason === "" || resignationDate === "" || codeNumber === "" || specificReason.length < 10) {
        if (resignationDate === "") {
            errorMessage.textContent = '퇴사일자를 입력해주세요.';
        } else if (resignationReason === "") {
            errorMessage.textContent = '퇴사사유를 선택해주세요.';
        } else if (codeNumber === "") {
            errorMessage.textContent = '퇴사코드를 선택해주세요.';
        } else if (specificReason.length > 10 && specificReason.length < 50) {
            errorMessage.textContent = '구체적 사유를 10자 이상 기재해주세요.';
        }
        return false;
    }
    return true;
}

// 퇴사 처리 폼 제출 시 AJAX 요청
function submitResignationForm(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    if (!validateForm()) { // 퇴사 처리 폼 유효성 검사 함수 실행
        return;
    }

    var form = event.target;
    var formData = new FormData(form);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', form.action, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                alert(xhr.responseText); // 성공 메시지
                window.location.reload(); // 페이지 새로고침
            } else if (xhr.status === 404) {
                alert(xhr.responseText); // 사원을 찾을 수 없습니다.
            } else {
                alert('퇴사처리 중 오류가 발생했습니다. 다시 시도해주세요.');
            }
        }
    };

    var params = new URLSearchParams(formData).toString();
    xhr.send(params);
}

document.addEventListener('DOMContentLoaded', function () {
    // 삭제 버튼 클릭 시 확인 메세지 및 처리 로직
    var deleteButtons = document.getElementsByClassName('delete-btn');
    for (var i = 0; i < deleteButtons.length; i++) {
        deleteButtons[i].addEventListener('click', function () {
            var employeeId = this.getAttribute('employeeId');
            var confirmMessage = '\'' + employeeId + '\' 사원을 정말 삭제하시겠습니까?';
            // AJAX
            if (confirm(confirmMessage)) {
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '/employee/delete/' + employeeId, true);
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

                // 서버 응답 처리
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE) {
                        if (xhr.status === 200) {
                            alert('\'' + employeeId + '\' ' + xhr.responseText);
                            window.location.reload(); // 페이지 새로고침
                        } else if (xhr.status === 404) {
                            alert('\'' + employeeId + '\' ' + xhr.responseText); // 사원을 찾을 수 없습니다.
                        } else if (xhr.status === 400) {
                            alert('\'' + employeeId + '\' ' + xhr.responseText); // 퇴사 정보 없음 또는 1년이 지나지 않음
                        } else {
                            alert('삭제하는 중 오류가 발생했습니다. 다시 시도해주세요.');
                        }
                    }
                };

                xhr.send(JSON.stringify({_method: 'DELETE'}));
            }
        });
    }
});