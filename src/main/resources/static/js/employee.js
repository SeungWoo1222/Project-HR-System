// ==================================================== 전역변수 =========================================================
let errorMessage;
let fileNo = 0;
let filesArr = [];
let oldFileIdArr = [];
// ==================================================== 전역변수 =========================================================

// =================================================== 유효성 검사 ========================================================
// 유효성 검사 - 사원 등록, 사원 정보 수정
function validateForm(event) {
    event.preventDefault();

    const name = document.getElementById("name").value.trim();
    const birth = document.getElementById("birth").value.trim();
    const residentRegistrationNumber = document.getElementById("residentRegistrationNumber").value.trim();
    const phoneInput = document.getElementById("phoneInput").value.trim();
    const emailLocal = document.getElementById("emailLocal").value.trim();
    const emailDomain = document.getElementById("emailDomain").value.trim();
    const address = document.getElementById("address").value.trim();
    const detailAddressInput = document.getElementById("detailAddressInput").value.trim();
    const maritalStatus = document.querySelector('input[name="maritalStatus"]:checked');
    const numDependents = document.getElementById("numDependents").value.trim();
    const numChildren = document.getElementById("numChildren").value.trim();
    const department = document.getElementById("department").value;
    const position = document.getElementById("position").value;
    const hireDate = document.getElementById("hireDate").value;

    if (errorMessage) {
        errorMessage.textContent = "";
    } else {
        console.error("에러 메세지 요소를 찾을 수 없습니다.");
    }

    if (name === "" || !validateName(name)) {
        errorMessage.textContent = "유효한 이름을 입력해주세요. (한글 또는 영어만 허용)";
        return false;
    }
    if (!validateBirthDate(birth)) {
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
    if (emailDomain === 'custom') {
        if (document.getElementById("customEmailDomain").value.trim() === "") {
            errorMessage.textContent = "이메일 도메인을 입력해주세요.";
            return false;
        }
    }
    if (address === "") {
        errorMessage.textContent = "주소를 입력해주세요.";
        return false;
    }
    if (detailAddressInput === "") {
        errorMessage.textContent = "상세 주소를 입력해주세요.";
        return false;
    }
    if (!maritalStatus) {
        errorMessage.textContent = "결혼 여부를 선택해주세요.";
        return false;
    }
    if (numDependents === "" || !/^\d+$/.test(numDependents) || parseInt(numDependents) < 0) {
        errorMessage.textContent = "유효한 부양 가족 수를 입력해주세요.";
        return false;
    }
    if (numChildren === "" || !/^\d+$/.test(numChildren) || parseInt(numChildren) < 0) {
        errorMessage.textContent = "유효한 자녀 수를 입력해주세요.";
        return false;
    }
    if (parseInt(numChildren) > parseInt(numDependents)) {
        errorMessage.textContent = "자녀 수는 부양 가족 수보다 많을 수 없습니다.";
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
// 유효성 검사 - 이름
function validateName(name) {
    // 한글, 영어만 허용 (공백 및 숫자, 특수문자 등 금지)
    const namePattern = /^[가-힣a-zA-Z]+$/;
    return namePattern.test(name);
}

// 유효성 검사 - 생년월일
function validateBirthDate(birth) {
    if (birth === "" || birth.length !== 6 || !/^\d+$/.test(birth)) {
        return false;
    }

    const year = parseInt(birth.substring(0, 2), 10);
    const month = parseInt(birth.substring(2, 4), 10);
    const day = parseInt(birth.substring(4, 6), 10);

    // 00~99의 연도 범위 체크 (1900년대, 2000년대 둘 다 가능)
    const fullYear = year > 50 ? 1900 + year : 2000 + year;

    // 월과 일 범위 체크
    if (month < 1 || month > 12 || day < 1 || day > 31) {
        return false;
    }

    // 실제로 존재하는 날짜인지 체크
    const date = new Date(fullYear, month - 1, day);
    return date.getFullYear() === fullYear && date.getMonth() + 1 === month && date.getDate() === day;
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
    const actionUrl = form.action;

    // for (var pair of form.entries()) {
    //     console.log(pair[0] + ': ' + pair[1]);
    // }

    return { form, actionUrl };
}
// =================================================== 유효성 검사 ========================================================

// ==================================================== AJAX 요청 =======================================================
// AJAX POST 요청 - 사원 등록
function submitInsertForm(event) {
    // 유효성 검사 실행
    if (!validateForm(event)) { return; }

    // form 제출 처리
    const { form, actionUrl } = handleFormSubmit(event);
    const formData = new FormData();

    // FormData 객체에 employee 필드를 추가
    const employee = {
        name: form.name.value,
        birth: form.birth.value,
        residentRegistrationNumber: form.residentRegistrationNumber.value,
        phone: form.phone.value,
        email: form.email.value,
        address: form.address.value,
        detailAddress: form.detailAddress.value,
        department: form.department.value,
        position: form.position.value,
        hireDate: form.hireDate.value,
        maritalStatus: Boolean(parseInt(document.querySelector('input[name="maritalStatus"]:checked').value)),
        numDependents: form.numDependents.value,
        numChildren: form.numChildren.value
    };
    formData.append("employee", new Blob([JSON.stringify(employee)], { type: "application/json" }));

    // FormData 객체에 picture 필드를 추가
    const picture = form.picture.files[0];
    if (picture) {
        formData.append("picture", picture);
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


// AJAX PUT 요청 - 사원 정보 수정
function submitUpdateForm(event) {
    // 유효성 검사 실행
    if (!validateForm(event)) { return; }

    // form 제출 처리
    const { form, actionUrl } = handleFormSubmit(event);
    const formData = new FormData();

    // FormData 객체에 employee 필드를 추가
    const employee = {
            employeeId: form.employeeId.value,
            name: form.name.value,
            birth: form.birth.value,
            residentRegistrationNumber: form.residentRegistrationNumber.value,
            phone: form.phone.value,
            email: form.email.value,
            address: form.address.value,
            detailAddress: form.detailAddress.value,
            status: form.status.value,
            department: form.department.value,
            position: form.position.value,
            hireDate: form.hireDate.value,
            maritalStatus: Boolean(parseInt(document.querySelector('input[name="maritalStatus"]:checked').value)),
            numDependents: form.numDependents.value,
            numChildren: form.numChildren.value,
            remainingLeave: form.remainingLeave.value,
            modifiedBy: form.modifiedBy.value,
            lastModified: form.lastModified.value,
            picture: form['original-picture'].value
    };
    formData.append("employee", new Blob([JSON.stringify(employee)], { type: "application/json" }));

    // FormData 객체에 picture 필드를 추가
    const picture = form.picture.files[0];
    if (picture) {
        formData.append("picture", picture);
    }

    // 수정 성공 후 이동할 URL
    const submitButton = document.querySelector('button[type="submit"]');
    const dataUrl = submitButton.getAttribute('data-url');
    console.log('Submit Button Data URL:', dataUrl);

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
                window.location.href = dataUrl;
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
    const { form, actionUrl } = handleFormSubmit(event);
    const formData = new FormData();

    // FormData 객체에 resignation 필드를 추가
    const resignation = {
        resignationDate: form.resignationDate.value,
        resignationReason: form.resignationReason.value,
        codeNumber: form.codeNumber.value,
        specificReason: form.specificReason.value
    };
    formData.append("resignation", new Blob([JSON.stringify(resignation)], { type: "application/json" }));

    // FormData 객체에 resignationDocuments 필드를 추가
    for (let i = 0; i < filesArr.length; i++) {
        // 삭제되지 않은 파일만 폼 데이터에 담기
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

// AJAX PUT 요청 - 퇴사 수정
function submitUpdateResignationForm(event, employeeId) {
    // 확인 메세지
    const confirmMessage = "'" + employeeId + "'사원의 퇴사 정보를 수정하시겠습니까?";
    if (!confirm(confirmMessage)) {
        return;
    }

    // 유효성 검사 실행
    if (!validateResignationForm(event)) {
        return;
    }

    // form 제출 처리
    const { form, actionUrl } = handleFormSubmit(event);
    const formData = new FormData();

    // FormData 객체에 resignation 필드를 추가
    const resignation = {
        resignationDate: form.resignationDate.value,
        resignationReason: form.resignationReason.value,
        codeNumber: form.codeNumber.value,
        specificReason: form.specificReason.value
    };
    formData.append("resignation", new Blob([JSON.stringify(resignation)], { type: "application/json" }));

    // 삭제되지 않은 기존 파일 ID만 필터링하여 배열 생성
    const filteredFileIdArr = oldFileIdArr.filter(fileId => fileId !== "");
    formData.append("oldFileIdList", new Blob([JSON.stringify(filteredFileIdArr)], { type: "application/json" }));

    // // FormData 객체에 oldFileIdArr 필드를 추가
    // for (let i = 0; i < oldFileIdArr.length; i++) {
    //     // 삭제되지 않은 기존 파일만 폼 데이터에 담기
    //     if (oldFileIdArr[i] !== "") {
    //         formData.append("oldFileIdArr", oldFileIdArr[i]);
    //     }
    // }

    // FormData 객체에 newFile 필드를 추가
    for (let i = 0; i < filesArr.length; i++) {
        // 삭제되지 않은 새로운 파일만 폼 데이터에 담기
        if (!filesArr[i].is_delete) {
            formData.append("newFile", filesArr[i]);
        }
    }

    // FormData에 담긴 데이터 콘솔에 출력
    for (let pair of formData.entries()) {
        console.log(pair[0] + ':', pair[1]);
    }

    // 데이터를 서버로 전송
    fetch(actionUrl  + employeeId, {
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
                window.location.href = '/employee/' + employeeId + '/detail';
            } else if (response.status === 404) {
                alert(response.text); // 404 오류 메세지 알림
            } else if (response.status === 400) {
                alert(response.text); // 400 오류 메시지 알림
            } else if (response.status === 500) {
                alert(response.text); // 500 오류 메시지 알림
            } else {
                alert('퇴사 정보 수정 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}

// AJAX DELETE 요청
function submitDelete(event) {
    event.stopPropagation();
    let button = event.target;
    let employeeId = button.getAttribute('employeeId');
    let employeeName = button.getAttribute('employeeName');

    var confirmMessage = '\'' + employeeName + '\' 사원을 정말 삭제하시겠습니까?';

    let actionUrl = '/api/admin/employee/' + employeeId;

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
                    alert(response.text); // 성공 메시지 알림
                    window.location.reload();
                } else if (response.status === 404) {
                    alert(response.text); // 404 오류 메세지 알림
                } else if (response.status === 400) {
                    alert(response.text); // 400 오류 메시지 알림
                } else if (response.status === 500) {
                    alert(response.text); // 500 오류 메시지 알림
                } else if (response.status === 403) {
                    openModal("/error/modal/403");
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

// ==================================================== AJAX 요청 =======================================================

// ==================================================== 각종 함수 ========================================================
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

// 퇴사 사유와 퇴사 코드 input 값으로 변환하는 함수
function changeCodeNumberAndResignationReason() {
    const resignationReason = document.getElementById("original-resignationReason").textContent;
    const codeNumber = document.getElementById("original-codeNumber").textContent;

    const formatedResignationReason = formatCodeNumberAndResignationReason(resignationReason);
    const formatedCodeNumber = formatCodeNumberAndResignationReason(codeNumber);

    document.getElementById("resignationReason").value = formatedResignationReason;
    updateCodeNumber();
    document.getElementById("codeNumber").value = formatedCodeNumber;

}

// .을 기준으로 앞에만 추출하는 함수
function formatCodeNumberAndResignationReason(text) {
    const index = text.indexOf('.');
    if (index !== -1) {
        return text.substring(0, index).trim();
    }
    return text.trim();
}

// 기존 퇴사 관련 문서 초기화하는 함수
function initializeResignationDocuments() {
    console.log(fileList);
    if (!fileList || !Array.isArray(fileList)) {
        console.warn('fileList가 없거나 배열이 아닙니다.');
        return;
    }
    // fileList에서 fileId와 originalFileName을 추출하여 객체로 배열 생성
    const oldFileArray = fileList.map(function (file) {
        return { fileId: file.fileId, originalFileName: file.originalFileName };
    });

    // oldFileArray에 요소가 있는지 확인
    if (oldFileArray.length === 0) {
        console.warn('oldFileArray가 비어 있습니다.');
        return;
    }

    console.log('oldFileArray : ', oldFileArray);

    // 목록에 기존 문서 추가
    for (let i = 0; i < oldFileArray.length; i++) {
        let htmlData = '';
        htmlData += '<div id="file' + fileNo + '" class="filebox">';
        htmlData += '   <p class="name">' + oldFileArray[i].originalFileName + '</p>';
        htmlData += '   <a class="delete" onclick="deleteRegisteredFile(' + fileNo + ');"><img src="/images/icons/delete.png" class="delete-btn" alt="delete-btn" width="20"/></a>';
        htmlData += '</div>';
        document.querySelector('.file-list').insertAdjacentHTML('beforeend', htmlData);
        oldFileIdArr[fileNo] = oldFileArray[i].fileId;
        console.log(oldFileIdArr);
        fileNo++;
    }
}

// 기혼 선택 시 부양 가족 수 2로 수정하는 함수
function updateDependents() {
    var isMarried = document.getElementById('marriedYes').checked;
    var numDependentsField = document.getElementById('numDependents');

    if (isMarried) {
        numDependentsField.value = 2;
    } else {
        numDependentsField.value = 1;
    }
}
// ==================================================== 각종 함수 ========================================================

//=================================================== File 관련 함수 =====================================================
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
            htmlData += '   <a class="delete" onclick="deleteFile(' + fileNo + ');"><img src="/images/icons/delete.png" class="delete-btn" alt="delete-btn" width="20"/></a>';
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
    // UI에서 파일을 삭제
    document.querySelector("#file" + num).remove();
    // 'filesArr' 배열에서 해당 파일 객체의 'is_delete' 속성 설정
    filesArr[num].is_delete = true;

}

// 기존 첨부파일 삭제
function deleteRegisteredFile(num) {
    // UI에서 파일을 삭제
    document.querySelector("#file" + num).remove();
    // 'registeredFilesArr[num]' 배열에서 해당 배열 값 ""로 초기화
    oldFileIdArr[num] = "";
    for (var i = 0; i < oldFileIdArr.length; i++) {
        console.log(oldFileIdArr[i]);
    }
}
//=================================================== File 관련 함수 =====================================================

