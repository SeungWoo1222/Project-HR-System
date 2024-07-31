// 전역 변수 =============================================================================================================
let errorMessage;
let fileNo = 0;
let filesArr = [];
// 전역변수 ==============================================================================================================

// 유효성 검사 ============================================================================================================
// 유효성 검사 - 사원 등록, 사원 정보 수정
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

// 유효성 검사 - 퇴사 처리
function validateResignationForm(event) {
    event.preventDefault();

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
        } else if (specificReason.length < 10) {
            errorMessage.textContent = '구체적 사유를 10자 이상 기재해주세요.';
        }
        return false;
    }
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
// 유효성 검사 ============================================================================================================

// AJAX 요청 ============================================================================================================
// AJAX POST 요청 - 사원 등록
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

// AJAX POST 요청 - 퇴사 처리
function submitResignationForm(event) {
    // 유효성 검사 실행
    if (!validateResignationForm(event)) {
        return;
    }

    // form 제출 처리
    const { formData, actionUrl } = handleFormSubmit(event);;

    for (let i = 0; i < filesArr.length; i++) {
        // 삭제되지 않은 파일만 폼데이터에 담기
        if (!filesArr[i].is_delete) {
            formData.append("resignationDocuments", filesArr[i]);
        }
    }

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
                window.location.reload();
            } else if (response.status === 404) {
                alert(response.text); // 404 오류 메세지 알림
            } else if (response.status === 400) {
                alert(response.text); // 400 오류 메시지 알림
            } else if (response.status === 500) {
                alert(response.text); // 500 오류 메시지 알림
            } else {
                alert('사원 퇴사처리 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}

// AJAX DELETE 요청
function submitDeleteRequest(event) {
    let button = event.target;
    let employeeId = button.getAttribute('employeeId');
    let employeeName = button.getAttribute('employeeName');

    var confirmMessage = '\'' + employeeName + '\' 사원을 정말 삭제하시겠습니까?';

    let actionUrl = '/employee/delete/' + employeeId;

    if (confirm(confirmMessage)) {
        fetch(actionUrl, {
            method: 'DELETE'
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                console.log('서버 응답 데이터 :', response.text);
                if (response.status === 200) {
                    alert('\'' + employeeName + '\' ' + response.text); // 성공 메시지 알림
                    window.location.reload();
                } else if (response.status === 404) {
                    alert(response.text); // 404 오류 메세지 알림
                } else if (response.status === 400) {
                    alert(response.text); // 400 오류 메시지 알림
                } else if (response.status === 500) {
                    alert(response.text); // 500 오류 메시지 알림
                } else {
                    alert('사원 삭제 처리 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX Patch 요청
function submitPatchForm(event) {
}

// AJAX 요청 ============================================================================================================

// 각종 함수 =============================================================================================================
// 이메일 조합 함수
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

// 이메일 분리 함수
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

// 이메일 직접 입력 클릭시 입력창 제어 함수
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

// 상세주소 조합 함수
function combineDetailAddress() {
    const detailAddressInput = document.getElementById("detailAddressInput").value.trim();
    const extraAddress = document.getElementById("extraAddress").value.trim();
    const detailAddress = detailAddressInput + extraAddress;
    document.getElementById("detailAddress").value = detailAddress;
}

// 전화번호 포맷팅 함수
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

// 전화번호 문자열 '-' 제거 함수
function formatInputPhoneNumber(phoneNumber) {
    return phoneNumber.replace(/\D/g, ''); // 숫자 이외의 문자 제거
}

// 페이지에서 전화번호 문자열 '-' 제거 함수
function updatePhoneNumberElement() {
    const phoneInputElement = document.getElementById("phoneInput");
    const formattedPhoneNumber = formatInputPhoneNumber(phoneInputElement.value);
    phoneInputElement.value = formattedPhoneNumber;
}

// 날짜 문자열 'T' 제거 함수
function formatDateTime(dateTimeString) {
    return dateTimeString.replace('T', ' ');
}

// 페이지에서 모든 날짜 문자열 'T' 제거 함수
function updateDateTimeElements() {
    const dateTimeElements = document.querySelectorAll('.dateTime');
    dateTimeElements.forEach(element => {
        element.textContent = formatDateTime(element.textContent);
    });
}

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
// 각종 함수 =============================================================================================================

// File 관련 함수 ========================================================================================================
// 이미지 파일 검증 - 사원 사진
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

// 첨부 파일 추가
function addFile(obj){
    var maxFileCnt = 3;   // 첨부파일 최대 개수
    var attFileCnt = document.querySelectorAll('.filebox').length; // 기존 추가된 첨부파일 개수
    var remainFileCnt = maxFileCnt - attFileCnt; // 추가로 첨부 가능한 개수
    var curFileCnt = obj.files.length; // 현재 선택된 첨부파일 개수

    // 첨부파일 개수 확인
    if (curFileCnt > remainFileCnt) {
        alert("첨부파일은 최대 " + maxFileCnt + "개 까지 첨부 가능합니다.");
    }

    for (var i = 0; i < Math.min(curFileCnt, remainFileCnt); i++) {
        const file = obj.files[i];

        // 첨부파일 검증
        if (validateFile(file)) {
            // 파일 배열에 담기
            filesArr.push(file);

            // 목록 추가
            let htmlData = '';
            htmlData += '<div id="file' + fileNo + '" class="filebox">';
            htmlData += '   <p class="name">' + file.name + '</p>';
            htmlData += '   <a class="delete" onclick="deleteFile(' + fileNo + ');"><i class="far fa-minus-square"></i></a>';
            htmlData += '</div>';
            document.querySelector('.file-list').insertAdjacentHTML('beforeend', htmlData);
            fileNo++;
        }
    }
    // 초기화
    document.querySelector("input[type=file]").value = "";
}

// 첨부파일 검증
function validateFile(obj){
    const fileTypes = ['application/pdf', 'image/gif', 'image/jpeg', 'image/png', 'image/bmp', 'image/tif', 'application/haansofthwp', 'application/x-hwp', 'application/vnd.hancom.hwp', '']; // hwp application/unknown 등 다 해봤는데 mime 데이터 ''으로 나와서 해보니 업로드 됨
    if (obj.name.length > 100) {
        alert("파일명이 100자 이상인 파일은 제외되었습니다.");
        return false;
    } else if (obj.size > (10 * 1024 * 1024)) { // 10MB
        alert("최대 파일 용량인 10MB를 초과한 파일은 제외되었습니다.");
        return false;
    } else if (obj.name.lastIndexOf('.') == -1) {
        alert("확장자가 없는 파일은 제외되었습니다.");
        return false;
    } else if (!fileTypes.includes(obj.type)) {
        alert("첨부가 불가능한 파일은 제외되었습니다.");
        return false;
    } else {
        return true;
    }
}

// 첨부파일 삭제
function deleteFile(num) {
    document.querySelector("#file" + num).remove();
    filesArr[num].is_delete = true;
}
// File 관련 함수 ========================================================================================================

