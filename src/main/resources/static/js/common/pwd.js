// AJAX POST 요청 - 비밀번호 확인
function submitPassword(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('error-message');

    // 유효성 검사
    if (!password) {
        errorMessage.textContent = '비밀번호를 입력해주세요.';
        return;
    }
    errorMessage.textContent = '';

    var form = event.target;
    var formData = new FormData(form);
    const actionUrl = form.action;

    // 데이터를 서버로 전송
    fetch(actionUrl, {
        method: 'POST',
        body: formData
    })
        .then(response => response.text().then(data => ({
            status: response.status,
            text: data
        })))
        .then(response => {
            const errorStatuses = [400, 401, 404];
            if (response.status === 200) {
                window.location.href = response.text;
            } else if (errorStatuses.includes(response.status)) {
                errorMessage.innerHTML = response.text.replace(/\n/g, "<br>");
            } else {
                errorMessage.textContent = "비밀번호 확인 중 오류가 발생했습니다.\n관리자에게 문의해주세요.";
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            errorMessage.textContent = '오류가 발생하였습니다.\n관리자에게 문의해주세요.';
        });
}

function validatePassword(password) {
    // 정규 표현식: 8~20자, 대문자, 소문자, 숫자 및 특수문자를 각각 하나 이상 포함
    const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
    return pattern.test(password);
}

// 새로운 비밀번호 강도 확인 로직
function checkPasswordStrength() {
    const newPasswordInput = document.getElementById('new-password');
    const newPassword = newPasswordInput.value;

    const passwordStrength = document.getElementById('show-strength');
    const passwordStrengthInput = document.getElementById('strength');

    // zxcvbn을 사용하여 비밀번호 강도 측정
    const result = zxcvbn(newPassword);

    // 강도 점수에 따른 메시지 표시
    const strength = ['매우 약함', '약함', '보통', '강함', '매우 강함'];
    passwordStrength.textContent = `${strength[result.score]}`;
    let passwordColor;
    switch (result.score) {
        case 0:
            passwordColor = 'red';
            break;
        case 1:
            passwordColor = 'orange';
            break;
        case 2:
            passwordColor = 'yellow';
            break;
        case 3:
            passwordColor = 'green';
            break;
        case 4:
            passwordColor = 'skyblue';
            break;
    }
    passwordStrength.classList.add(passwordColor);

    // 강도 점수 입력
    passwordStrengthInput.value = result.score;
}

// 새로운 비밀번호 확인 함수
function checkNewPassword() {
    const newPasswordInput = document.getElementById('new-password');
    const newPassword = newPasswordInput.value;
    const errorMessage = document.getElementById('pwd-error-message');

    if (!validatePassword(newPassword)) {
        errorMessage.textContent = '새로운 비밀번호가 양식에 맞지 않습니다.';
        newPasswordInput.focus();
    } else {
        errorMessage.textContent = '';
    }
    checkPasswordStrength();
}

// 확인 비밀번호 확인 함수
function checkConfirmPassword() {
    const confirmPasswordInput = document.getElementById('confirm-password');
    const confirmPassword = confirmPasswordInput.value;
    const newPassword = document.getElementById('new-password').value;
    const errorMessage = document.getElementById('pwd-error-message');

    if (confirmPassword !== newPassword) {
        errorMessage.textContent = '새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.';
        confirmPasswordInput.focus();
    } else {
        errorMessage.textContent = '';
    }
}

// AJAX PUT 요청 - 비밀번호 변경
function summitUpdatePassword(event) {
    event.preventDefault(); // 기본 폼 제출 방지

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

    const passwordInput = document.getElementById('password');
    const newPasswordInput = document.getElementById('new-password');
    const confirmPasswordInput = document.getElementById('confirm-password');

    const newPassword = newPasswordInput.value;
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    const errorMessage = document.getElementById('pwd-error-message');

    // 유효성 검사
    if (!password) {
        passwordInput.focus();
        return showError("password", "현재 비밀번호를 입력해주세요.", true);
    }

    if (!newPassword) {
        newPasswordInput.focus();
        return showError("new-password", "새로운 비밀번호를 입력해주세요.", true);
    }

    if (!validatePassword(newPassword)) {
        newPasswordInput.focus();
        return showError("new-password", "새로운 비밀번호가 양식에 맞지 않습니다.", true);
    }

    if (!confirmPassword) {
        confirmPasswordInput.focus();
        return showError("confirm-password", "확인 비밀번호를 입력해주세요.", true);
    }

    if (confirmPassword !== newPassword) {
        newPasswordInput.focus();
        return showError("new-password", "새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.", true);
    }

    errorMessage.textContent = '';

    const form = event.target;
    const formData = new FormData(form);

    fetch(form.action, {
        method: 'PUT',
        body: formData
    })
        .then(response => response.text().then(data => ({
            status: response.status,
            text: data
        })))
        .then(response => {
            console.log('서버 응답 데이터 : ', response.text);
            if (response.status === 200) {
                alert(response.text);
                window.location.href = "/auth/logout";
            } else if (response.status === 404) {
                errorMessage.textContent = response.text;
            } else if (response.status === 400) {
                errorMessage.textContent = response.text;
            } else if (response.status === 500) {
                errorMessage.textContent = response.text;
            } else {
                errorMessage.textContent = '비밀번호 확인 중 오류가 발생했습니다.\n관리자에게 문의해주세요.';
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });

}

// 비밀번호 변경 날짜 계산 로직
function calculateDateDifference() {
    const lastModifiedElement = document.getElementById('lastModified');
    const lastModified = lastModifiedElement.textContent.trim();

    const lastModifiedDate = new Date(lastModified);

    // 마지막 비밀번호 변경 날짜에서 3개월 후의 날짜를 계산
    const nextChangeDate = new Date(lastModifiedDate);
    nextChangeDate.setMonth(nextChangeDate.getMonth() + 3);

    const today = new Date();
    const timeDiff = nextChangeDate.getTime() - today.getTime();
    const dayDiff = Math.floor(timeDiff / (1000 * 3600 * 24));

    if (isNaN(dayDiff)) {
        document.getElementById('date-difference').textContent = '비밀번호 변경 기록이 없습니다. 비밀번호를 변경해주세요.';
    } else if (dayDiff <= 0) {
        document.getElementById('date-difference').textContent = '비밀번호 변경 기간이 지났습니다. 비밀번호를 즉시 변경해주세요.';
    } else {
        document.getElementById('date-difference').textContent = `다음 비밀번호 변경일까지 ${dayDiff}일 남았습니다.`;
    }
}

// 배터리 레벨 애니메이션 조정 함수
function updateBatteryLevel() {
    var strength = parseInt(document.getElementById('password-strength').innerText);
    var batteryLevel = document.querySelector('.battery-level');
    var indicatorText = document.querySelector('.battery-indicator');

    // 초기화
    batteryLevel.innerHTML = '';

    // 배터리 셀을 강도에 맞춰 추가
    var color;
    var indicator;
    var textColor;
    switch (strength) {
        case 4:
            color = 'linear-gradient(to right, #18A8F1, #5EC5F8)';
            indicator = '매우 강함';
            textColor = '#18A8F1';
            break;
        case 3:
            color = 'linear-gradient(to right, #32C113, #32C113)';
            indicator = '강함';
            textColor = '#32C113';
            break;
        case 2:
            color = 'linear-gradient(to right, #F1CC18, #F1CC18)';
            indicator = '보통';
            textColor = '#F1CC18';
            break;
        case 1:
            color = 'linear-gradient(to right, #F16018, #F16018)';
            indicator = '약함';
            textColor = '#F16018';
            break;
        default:
            color = 'linear-gradient(to right, #F1183C, #F1183C)';
            indicator = '매우 약함';
            textColor = '#F1183C';
    }

    // 강도에 따른 셀 개수 및 색상 적용
    for (var i = 0; i <= strength; i++) {
        var cell = document.createElement('div');
        cell.className = 'battery-cell on';
        cell.style.backgroundImage = color;
        batteryLevel.appendChild(cell);
    }

    // 빈 셀 추가
    for (var i = strength + 1; i < 5; i++) {
        var cell = document.createElement('div');
        cell.className = 'battery-cell';
        cell.style.backgroundColor = '#e6e6e6';
        batteryLevel.appendChild(cell);
    }

    // 인디케이터 텍스트 색상 변경
    indicatorText.innerText = indicator;
    indicatorText.style.color = textColor;
}

