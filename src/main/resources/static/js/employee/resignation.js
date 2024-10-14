let errorMessage;

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
            errorMessage.textContent = '퇴사 날짜를 입력해주세요.';
        } else if (resignationReason === "") {
            errorMessage.textContent = '퇴사 사유를 선택해주세요.';
        } else if (codeNumber === "") {
            errorMessage.textContent = '퇴사 코드를 선택해주세요.';
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
    if (confirm("해당 사원을 퇴사 처리하시겠습니까?")) {
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
}

// AJAX PUT 요청 - 퇴사 수정
function submitUpdateResignationForm(event) {
    // form 제출 처리
    const { form, actionUrl } = handleFormSubmit(event);
    const employeeId = form.employeeId.value;

    // 유효성 검사 실행
    if (!validateResignationForm(event)) {
        return;
    }

    // FormData 객체에 resignation 필드를 추가
    const formData = new FormData();
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

    // 데이터를 서버로 전송
    if (confirm("사원의 퇴사 정보를 수정하시겠습니까?")) {
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
}

// AJAX DELETE 요청
function submitDelete(event) {
    event.stopPropagation();
    let button = event.target;
    let employeeId = button.getAttribute('employeeId');
    let employeeName = button.getAttribute('employeeName');

    let actionUrl = '/api/admin/employee/' + employeeId;

    if (confirm('\'' + employeeName + '\' 사원을 정말 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.\n이 작업을 계속하시려면 확인을 눌러주세요.')) {
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