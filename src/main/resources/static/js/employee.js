// 이메일 조합 로직
function combineEmail() {
    const emailLocal = document.getElementById("emailLocal").value.trim;
    const emailDomain = document.getElementById("emailDomain").value;
    let email = '';
    if (emailDomain === 'custom') {
        const customDomain = document.getElementById("customEmailDomain").value.trim;
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

// 이메일 직접입력 클릭시 입력창 제어 로직
function updateCustomField() {
    console.log('실행됨');
    const emailDomainSelect = document.getElementById("emailDomain");
    const customDomainInput = document.getElementById("customEmailDomain");
    if (emailDomainSelect.value === "custom") {
        console.log('직접입력 클릭');
        customDomainInput.classList.remove("hidden");
    } else {
        console.log('직접입력 외 클릭');
        customDomainInput.classList.add("hidden");
        document.getElementById("customEmailDomain").value = '';
    }
}

// 상세주소 조합 로직
function combineDetailAddress() {
    const detailAddressInput = document.getElementById("detailAddressInput").value;
    const extraAddress = document.getElementById("extraAddress").value;
    const detailAddress = detailAddressInput + extraAddress;
    document.getElementById("detailAddress").value = detailAddress;
}

// 전화번호 포맷팅 로직
function formatPhoneNumber() {
    const phoneInput = document.getElementById("phoneInput").value.replace(/\D/g, ''); // 숫자 이외의 문자 제거
    const match = phoneInput.match(/^(\d{3})(\d{4})(\d{4})$/); // 3자리-4자리-4자리 매칭
    if (match) {
        const formattedPhone = match[1] + '-' + match[2] + '-' + match[3];
        document.getElementById("phone").value = formattedPhone;
    } else {
        document.getElementById("phone").value = phoneInput; // 매칭되지 않으면 숫자만 설정
    }
}

// 유효성 검사
function validateForm() {}

// AJAX 요청
function submitForm(event) {}

// AJAX 요청 - Patch
function submitEmployeePartial(event) {
    event.preventDefault(); // 폼의 기본 제출 동작을 막음

    const form = document.getElementById('employeeForm');
    const formData = new FormData(form);
    const actionUrl = form.getAttribute('th:action');

    fetch(actionUrl, {
        method: 'PATCH',
        body: formData
        // headers: {} - 생략
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
        })
        .catch(error => {
            console.error('Error:', error);
    });
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