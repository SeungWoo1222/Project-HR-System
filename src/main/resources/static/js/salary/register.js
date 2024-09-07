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

    const employeeId = document.getElementById("employeeId").value.trim();
    const name = document.getElementById("name").value.trim();
    const department = document.getElementById("department").value.trim();
    const position = document.getElementById("position").value.trim();
    const bank = document.getElementById("bank").value.trim();
    const accountNumber = document.getElementById("accountNumber").value.trim();
    const annualSalary = document.getElementById("annualSalary").value.trim();

    let errorMessage = document.querySelector(".error-message");

    if (employeeId === "") {
        errorMessage.textContent = "사원을 선택해주세요.";
        return false;
    }
    if (name === "") {
        errorMessage.textContent = "이름을 입력해주세요.";
        return false;
    }
    if (department === "") {
        errorMessage.textContent = "부서 정보가 없습니다.";
        return false;
    }
    if (position === "") {
        errorMessage.textContent = "직급 정보가 없습니다.";
        return false;
    }
    if (bank === "") {
        errorMessage.textContent = "은행을 선택해주세요.";
        return false;
    }
    if (accountNumber === "") {
        errorMessage.textContent = "계좌번호를 입력해주세요.";
        return false;
    }
    if (annualSalary === "") {
        errorMessage.textContent = "연봉을 입력해주세요.";
        return false;
    }
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
}