// employeeId 선택시 부서와 직급 업데이트
function updateEmployeeDetails() {
    const selectElement = document.getElementById("employeeSelect");
    const selectedOption = selectElement.options[selectElement.selectedIndex];

    const name = selectedOption.getAttribute("data-name");
    const department = selectedOption.getAttribute("data-department");
    const position = selectedOption.getAttribute("data-position");

    document.getElementById("name").value = name || "";
    document.getElementById("department").value = department || "";
    document.getElementById("position").value = position || "";
}

// 계좌번호 숫자 이외의 문자 제거
function validateAccountNumber() {
    const accountNumberInput = document.getElementById("accountNumber");
    const accountNumberValue = accountNumberInput.value;

    // 숫자 이외의 문자 제거
    accountNumberInput.value = accountNumberValue.replace(/[^0-9]/g, '');
}

// 연봉 포맷팅
function formatAnnualSalary() {
    const salaryInput = document.getElementById("annualSalary");
    let salaryValue = salaryInput.value;

    // 기존의 쉼표를 제거한 뒤 숫자 이외의 문자 제거
    salaryValue = salaryValue.replace(/,/g, '').replace(/[^0-9]/g, '');

    // 3자리마다 쉼표 추가
    salaryInput.value = salaryValue.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// 유효성 검사
function validateForm(event) {
    event.preventDefault();

    const employeeId = document.getElementById("employeeSelect").value.trim();
    const department = document.getElementById("department").value.trim();
    const position = document.getElementById("position").value.trim();
    const bank = document.getElementById("bank").value.trim();
    const accountNumber = document.getElementById("accountNumber").value.trim();
    const annualSalary = document.getElementById("annualSalary").value.trim();

    let errorMessage = document.querySelector(".error-message");

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

    if (employeeId === "") {
        return showError("employeeSelect", "사원을 선택해주세요.");
    }
    if (department === "") {
        return showError("department", "부서 정보가 없습니다.", true);
    }
    if (position === "") {
        return showError("position", "직급 정보가 없습니다.", true);
    }
    if (bank === "") {
        return showError("bank", "은행을 선택해주세요.");
    }
    if (accountNumber === "") {
        errorMessage.textContent = "";
        return showError("accountNumber", "계좌번호를 입력해주세요.", true);
    }
    if (annualSalary === "") {
        return showError("annualSalary", "연봉을 입력해주세요.", true);
    }

    errorMessage.textContent = '';
    return true;
}

// AJAX POST 요청 - 급여 정보 등록
function submitForm(event) {
    // 유효성 검사 실행
    if (!validateForm(event)) { return; }

    const form = event.target;
    const formData = new FormData(form);

    // 연봉에서 쉼표 제거 후 다시 formData에 설정
    let annualSalary = formData.get("annualSalary");
    annualSalary = annualSalary.replace(/,/g, ''); // 쉼표 제거
    formData.set("annualSalary", annualSalary);

    const actionUrl = form.action;
    if (confirm("사원의 급여 정보를 등록하시겠습니까?")) {
        // 급여 정보 확인
        fetch('/api/salary/check/' + form.employeeId.value, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json().then(data => ({ status: response.status, data })))  // 응답과 데이터를 함께 반환
            .then(({ status, data }) => {
                // 서버 응답 확인
                if (status === 200) {
                    // 급여 정보가 존재할 경우
                    if (confirm(data.message + "\n급여 정보를 새로 등록하시겠습니까?\n새로 등록 시 이전 급여 정보는 사용 중지 처리됩니다.")) {
                        // 급여 정보 사용 중지
                        const salaryId = data.salaryInfo.salaryId;
                        fetch('/api/salary/deactivate/' + salaryId, {
                            method: 'PATCH',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        })
                            .then(patchResponse => {
                                if (patchResponse.ok) {
                                    alert("이전 급여 정보가 사용 중지되었습니다.");
                                    // 새로운 급여 정보 등록
                                    registerSalary(actionUrl, formData);
                                } else {
                                    alert("급여 정보 사용 중지에 실패했습니다.");
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                alert('급여 정보 사용 중지 중 오류가 발생했습니다. 관리자에게 문의하세요.');
                            });
                    }
                } else {
                    // 급여 정보가 없을 경우 - 급여 정보 등록
                    registerSalary(actionUrl, formData);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생하였습니다. 관리자에게 문의하세요.');
            });
    }
}

// 급여 정보 등록
function registerSalary(actionUrl, formData) {
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
                alert(response.text);
                window.location.href = "/salary/list";
            } else {
                alert('급여 정보 등록 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                window.location.reload();
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}
