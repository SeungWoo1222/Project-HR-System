// AJAX PUT 요청 - 급여 및 공제 비율 수정
function submitUpdateForm(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const formData = new FormData(form);
    const actionUrl = form.action;

    for (let [name, value] of formData.entries()) {
        console.log(name + " : " + value);
    }

    // 유효성 검사 - 빈 필드와 숫자 여부 확인
    let isValid = true;
    for (let [name, value] of formData.entries()) {
        if (!value.trim()) {
            alert("'" + name + "' 항목이 비어있습니다.\n모든 비율을 채워 설정해주세요.");
            isValid = false;
            break;
        }
        if (isNaN(value)) {
            alert("'" + name + "' 항목에 숫자만 입력해주세요.");
            isValid = false;
            break;
        }
    }
    if (isValid &&  actionUrl.includes('/api/salary/payment/payroll')) {
        let total = 0;
        for (let [name, value] of formData.entries()) {
            if (!isNaN(parseFloat(value))) {
                total += parseFloat(value); // 숫자 변환 후 합산
            }
        }
        console.log(total);
        if (total !== 100.0) {
            alert("급여 항목 비율이 잘못되었습니다.\n비율의 총 합을 100%로 다시 설정해주세요.");
            isValid = false;
        }
    }
    if (!isValid) return;

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
                alert(response.text);
                window.location.reload();
            } else if (response.status === 400) {
                alert(response.text);
            } else {
                alert('비율 수정 중 오류가 발생하였습니다.\n재시도 후 문제가 지속하여 발생시 관리자에게 문의해주세요');
            }
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}