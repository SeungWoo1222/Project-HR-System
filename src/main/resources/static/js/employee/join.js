// AJAX POST 요청 - 방문객 회원가입
function submitJoinForm(event) {
    event.preventDefault();

    // 유효성 검사
    const name = document.getElementById("name").value.trim();
    const department = document.getElementById("department").value;
    const position = document.getElementById("position").value;

    if (name === "" || !validateName(name)) {
        return showError("name", "유효한 이름을 입력해주세요. (한글 또는 영어만 허용)", true);
    }
    if (!department) {
        return showError("department", "부서를 선택해주세요.");
    }
    if (!position) {
        return showError("position", "직위를 선택해주세요.");
    }

    document.getElementById('error-message').textContent = '';

    // form 제출 처리
    const form = event.currentTarget;
    const formData = new FormData(form);
    const actionUrl = form.action;

    // 데이터를 서버로 전송
    if (confirm("회원가입 하시겠습니까?")) {
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
                    alert(response.text); // 성공 메시지 알림
                    window.location.href = '/auth/login';
                } else if (response.status === 404) {
                    alert(response.text); // 404 오류 메세지 알림
                } else if (response.status === 400) {
                    alert(response.text); // 400 오류 메시지 알림
                } else if (response.status === 500) {
                    alert(response.text); // 500 오류 메시지 알림
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// 유효성 검사 함수
function showError(inputId, message, isBottomBorder = false) {
    const inputElement = document.getElementById(inputId);
    let errorMessage = document.getElementById("error-message");
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

// 유효성 검사 - 이름
function validateName(name) {
    // 한글, 영어만 허용 (공백 및 숫자, 특수문자 등 금지)
    const namePattern = /^[가-힣a-zA-Z]+$/;
    return namePattern.test(name);
}