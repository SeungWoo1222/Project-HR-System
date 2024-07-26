let errorMessage;

// 이메일 조합 로직
function combineEmail() {
    const emailLocal = document.getElementById("emailLocal").value.trim();
    const emailDomain = document.getElementById("emailDomain").value;
    let email = '';
    if (emailDomain === 'custom') {
        const customDomain = document.getElementById("customEmailDomain").value.trim();
        email = emailLocal + "@" + customDomain;
    } else {
        email = emailLocal + "@" + emailDomain;
    }
    document.getElementById("email").value = email;
}

// 이메일 분리 로직
function separateEmail() {
    const email = document.getElementById("email").value;
    if (!email) return;
    const [local, domain] = email.split('@');
    document.getElementById("emailLocal").value = local;
    const emailDomainSelect = document.getElementById("emailDomain");
    if ([...emailDomainSelect.options].some(option => option.value === domain)) {
        emailDomainSelect.value = domain;
    } else {
        emailDomainSelect.value = "custom";
        document.getElementById("customEmailDomain").classList.remove("hidden");
        document.getElementById("customEmailDomain").value = domain;
    }
}

// 이메일 직접 입력 클릭시 입력창 제어 로직
function updateCustomField() {
    const emailDomainSelect = document.getElementById("emailDomain");
    const customDomainInput = document.getElementById("customEmailDomain");
    if (emailDomainSelect.value === "custom") {
        customDomainInput.classList.remove("hidden");
    } else {
        customDomainInput.classList.add("hidden");
        document.getElementById("customEmailDomain").value = '';
    }
}

// 상세주소 조합 로직
function combineDetailAddress() {
    const detailAddressInput = document.getElementById("detailAddressInput").value.trim();
    const extraAddress = document.getElementById("extraAddress").value.trim();
    const detailAddress = detailAddressInput + extraAddress;
    document.getElementById("detailAddress").value = detailAddress;
}

// 전화번호 포맷팅 로직
function formatPhoneNumber() {
    const phoneInput = document.getElementById("phoneInput").value.replace(/\D/g, '');
    const match = phoneInput.match(/^(\d{3})(\d{4})(\d{4})$/); // 3자리-4자리-4자리 매칭
    if (match) {
        const formattedPhonNumbere = match[1] + '-' + match[2] + '-' + match[3];
        document.getElementById("phone").value = formattedPhonNumbere;
    } else {
        document.getElementById("phone").value = phoneInput; // 매칭되지 않으면 숫자만 설정
    }
}

// 전화번호 문자열 '-' 제거 로직
function formatInputPhoneNumber(phoneNumber) {
    return phoneNumber.replace(/\D/g, ''); // 숫자 이외의 문자 제거
}

// 페이지에서 전화번호 문자열 '-' 제거 로직
function updatePhoneNumberElement() {
    const phoneInputElement = document.getElementById("phoneInput");
    const formattedPhoneNumber = formatInputPhoneNumber(phoneInputElement.value);
    phoneInputElement.value = formattedPhoneNumber;
}

// 날짜 문자열 'T' 제거 로직
function formatDateTime(dateTimeString) {
    return dateTimeString.replace('T', ' ');
}

// 페이지에서 모든 날짜 문자열 'T' 제거 로직
function updateDateTimeElements() {
    const dateTimeElements = document.querySelectorAll('.dateTime');
    dateTimeElements.forEach(element => {
        element.textContent = formatDateTime(element.textContent);
    });
}

// 유효성 검사
function validateForm(event) {
    event.preventDefault();

    var picture = document.getElementById('picture').files[0];
    if (picture) {

    }

    const name = document.getElementById("name").value.trim();
    const birth = document.getElementById("birth").value.trim();
    const residentRegistrationNumber = document.getElementById("residentRegistrationNumber").value.trim();
    const phoneInput = document.getElementById("phoneInput").value.trim();
    const emailLocal = document.getElementById("emailLocal").value.trim();
    const emailDomain = document.getElementById("emailDomain").value.trim();
    const address = document.getElementById("address").value.trim();
    const detailAddressInput = document.getElementById("detailAddressInput").value.trim();
    const department = document.getElementById("department").value;
    const position = document.getElementById("position").value;
    const hireDate = document.getElementById("hireDate").value;

    if (errorMessage) {
        errorMessage.textContent = "";
    } else {
        console.error("Error message element not found.");
    }

    if (name === "") {
        errorMessage.textContent = "이름을 입력해주세요.";
        return false;
    }
    if (birth === "" || birth.length !== 6 || !/^\d+$/.test(birth)) {
        errorMessage.textContent = "유효한 생년월일(6자리 숫자)을 입력해주세요.";
        return false;
    }
    if (residentRegistrationNumber === "" || residentRegistrationNumber.length !== 7 || !/^\d+$/.test(residentRegistrationNumber)) {
        errorMessage.textContent = "유효한 주민번호 뒷자리(7자리 숫자)를 입력해주세요.";
        return false;
    }
    if (phoneInput === "" || !/^\d{11}$/.test(phoneInput)) {
        errorMessage.textContent = "'-'를 제외한 유효한 전화번호를 입력해주세요.";
        return false;
    }
    if (emailLocal === "") {
        errorMessage.textContent = "이메일을 입력해주세요.";
        return false;
    }
    if (emailDomain === "") {
        errorMessage.textContent = "이메일 도메인을 선택해주세요.";
        return false;
    }
    if (address === "") {
        errorMessage.textContent = "주소를 입력해주세요.";
        return false;
    }
    if (detailAddressInput === "") {
        errorMessage.textContent = "상세 주소를 입력해주세요.";
        return false;
    }
    if (department === "") {
        errorMessage.textContent = "부서를 선택해주세요.";
        return false;
    }
    if (position === "") {
        errorMessage.textContent = "직위를 선택해주세요.";
        return false;
    }
    if (hireDate === "") {
        errorMessage.textContent = "입사일을 선택해주세요.";
        return false;
    }

    // DB 데이터 형식에 맞게 처리
    combineEmail();
    combineDetailAddress();
    formatPhoneNumber();

    return true;
}

// form 제출 처리
function handleFormSubmit(event) {
    event.preventDefault();

    const form = event.target.closest('form');
    const formData = new FormData(form);
    const actionUrl = form.action;

    for (var pair of formData.entries()) {
        console.log(pair[0] + ': ' + pair[1]);
    }

    return { formData, actionUrl };
}

// AJAX POST 요청
function submitInsertForm(event) {
    // 유효성 검사 실행
    if (!validateForm(event)) {
        return;
    }

    // form 제출 처리
    const { formData, actionUrl } = handleFormSubmit(event);

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
            console.log('서버 응답 데이터 :', response.text);
            if (response.status === 200) {
                alert(response.text); // 성공 메시지 알림
                window.location.href = "/employee/list";
            } else if (response.status === 404) {
                alert(response.text); // 404 오류 메세지 알림
                window.location.reload();
            } else if (response.status === 400) {
                alert(response.text); // 400 오류 메시지 알림
            } else if (response.status === 500) {
                alert(response.text); // 500 오류 메시지 알림
            } else {
                alert('사원 등록 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                window.location.reload();
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}


// AJAX PUT 요청
function submitUpdateForm(event) {
    // 유효성 검사 실행
    if (!validateForm(event)) {
        return;
    }

    // form 제출 처리
    const { formData, actionUrl } = handleFormSubmit(event);

    // 데이터를 서버로 전송
    fetch(actionUrl, {
        method: 'PUT',
        body: formData
    })
        .then(response => response.text().then(data => ({
        status: response.status,
        text: data
    })))
        .then(response => {
            console.log('서버 응답 데이터 :', response.text);
            if (response.status === 200) {
                alert(response.text); // 성공 메시지 알림
                window.location.href = "/employee/myInfo";
            } else if (response.status === 404) {
                alert(response.text); // 404 오류 메세지 알림
                window.location.reload();
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

// AJAX Patch 요청
function submitPatchForm(event) {
}

// 이미지 파일 검증
function validateImage(obj) {
    const files = obj.files;
    if (files.length > 0) {
        const file = files[0];

        if (obj) {
            // 파일 크기 확인
            if (file.size > 10 * 1024 * 1024) {
                alert("파일 크기는 10MB를 초과할 수 없습니다.");
                obj.value = ""
                return false;
            }

            // 파일 형식 확인
            const fileTypes = ['image/jpeg', 'image/png', 'image/svg+xml', 'image/webp', 'image/heif', 'image/heif'];
            if (!fileTypes.includes(file.type)) {
                alert("파일 형식이 맞지 않습니다.");
                obj.value = ""
                return false;
            }

            // 파일명 업데이트
            document.getElementById('file-name').textContent = file.name;

            // 이미지 미리보기 업데이트
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('employee-picture').src = e.target.result;
            }
            reader.readAsDataURL(file);
        }
    }
}