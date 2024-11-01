// 급여명세서 수정 페이지로 이동
function goToUpdateForm(button) {
    const paymentId = button.getAttribute('data-paymentId');
    if (confirm("급여명세서를 수정하시겠습니까?")) {
        openModal('/salary/payment/' + paymentId + '/edit');
    }
    return;
}

// AJAX PUT 요청 - 급여명세서 수정
function submitUpdateForm(event) {
    event.preventDefault();

    // 유효성 검사
    const baseSalary = document.getElementById('baseSalary').value.trim();
    const positionAllowance = document.getElementById('positionAllowance').value.trim();
    const mealAllowance = document.getElementById('mealAllowance').value.trim();
    const transportAllowance = document.getElementById('transportAllowance').value.trim();
    const personalBonus = document.getElementById('personalBonus').value.trim();
    const teamBonus = document.getElementById('teamBonus').value.trim();
    const holidayBonus = document.getElementById('holidayBonus').value.trim();
    const yearEndBonus = document.getElementById('yearEndBonus').value.trim();

    let errorMessage = document.getElementById('error-message');

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

    if (baseSalary === '' || parseInt(baseSalary) <= 0) {
        return showError('baseSalary', '유효한 기본급을 입력해주세요.');
    }
    if (positionAllowance === '' || parseInt(positionAllowance) <= 0) {
        return showError('positionAllowance', '유효한 직책수당을 입력해주세요.');
    }
    if (mealAllowance === '' || parseInt(mealAllowance) <= 0) {
        return showError('mealAllowance', '유효한 식대를 입력해주세요.');
    }
    if (transportAllowance === '' || parseInt(transportAllowance) <= 0) {
        return showError('transportAllowance', '유효한 교통비을 입력해주세요.');
    }
    if (personalBonus !== '' && parseInt(personalBonus) <= 0) {
        return showError('personalBonus', '유효한 개인 성과급을 입력해주세요.');
    }
    if (teamBonus !== '' && parseInt(teamBonus) <= 0) {
        return showError('teamBonus', '유효한 팀 성과급을 입력해주세요.');
    }
    if (holidayBonus !== '' && parseInt(holidayBonus) <= 0) {
        return showError('holidayBonus', '유효한 명절 보너스를 입력해주세요.');
    }
    if (yearEndBonus !== '' && parseInt(yearEndBonus) <= 0) {
        return showError('yearEndBonus', '유효한 연말 보너스를 입력해주세요.');
    }

    const form = event.currentTarget;
    const formData = new FormData(form);

    if (confirm("급여명세서를 수정하시겠습니까?")) {
        fetch('/api/salary/payment/' + form.paymentId.value, {
            method: 'PUT',
            body: formData
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                if (response.status === 200) {
                    alert(response.text);
                } else if (response.status === 400) {
                    alert(response.text);
                } else {
                    alert('급여명세서 수정 중 오류가 발생하였습니다.\n재수정 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                }
                openModal('/salary/payment/' + form.paymentId.value);
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// AJAX DELETE 요청 - 급여명세서 삭제
function deletePayslip(button) {
    if (!confirm("급여명세서를 정말 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.\n이 작업을 계속하시려면 확인을 눌러주세요.")) {
        return;
    }

    const paymentId = button.getAttribute('data-paymentId');

    fetch('/api/salary/payment/' + paymentId, {
        method: 'DELETE'
    })
        .then(response => response.text().then(data => ({
            status: response.status,
            text: data
        })))
        .then(response => {
            if (response.status === 200) {
                alert(response.text);
                window.location.reload();
            } else if (response.status === 400) {
                alert(response.text);
            } else {
                alert('급여명세서 삭제 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}