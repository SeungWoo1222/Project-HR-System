// 급여명세서 수정 페이지로 이동
function goToUpdateForm(button) {
    const paymentId = button.getAttribute('data-paymentId');
    if (confirm("급여명세서를 수정하시겠습니까?")) {
        openModal('/salary/payment/' + paymentId + '/edit');
        // window.location.href='/salary/payment/' + paymentId + '/edit';
    }
    return;
}

// AJAX PUT 요청 - 급여명세서 수정
function submitUpdateForm(event) {
    event.preventDefault();

    if (!confirm("급여명세서를 수정하시겠습니까?")) {
        return;
    }

    const form = event.currentTarget;
    const formData = new FormData(form);

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
                alert('급여명세서 수정 중 오류가 발생하였습니다.\n재등록 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
            }
            openModal('/salary/payment/' + form.paymentId.value);
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}