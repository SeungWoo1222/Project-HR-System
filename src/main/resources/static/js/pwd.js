// 유효성 검사
function validationPassword(event) {
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('error-message');
    if (!password) {
        errorMessage.textContent = '비밀번호를 입력해주세요.';
        return false;
    }
    errorMessage.textContent = '';

    return true;
}

// Ajax 요청
function submitPassword(event) {
    event.preventDefault(); // 기본 폼 제출 방지
    if (!validationPassword()) {
        return;
    }

    var form = event.target;
    var formData = new FormData(form);
    const errorMessage = document.getElementById('error-message');

    const xhr = new XMLHttpRequest();
    xhr.open('POST', form.action, true);
    xhr.onreadystatechange = function(){
        if  (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                window.location.href = xhr.responseText;
            } else if (xhr.status === 404) { // '비밀번호가 틀렸습니다. 현재 시도 횟수 : ? / 5 입니다.'
                // 여기에 console.log 추가하여 xhr.responseText 확인
                console.log('Error:', xhr.status, xhr.responseText);
                errorMessage.textContent = xhr.responseText;
            } else if (xhr.status === 400) {
                errorMessage.textContent = '비밀번호 오류 횟수 초과로 계정이 차단되었습니다.\n관리자에게 문의해주세요.';
            } else {
                errorMessage.textContent = '비밀번호 확인 중 오류가 발생했습니다.\n관리자에게 문의해주세요.'
            }
        }
    }
    xhr.send(formData);
}
