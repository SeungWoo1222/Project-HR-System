// 전역변수
let registeredFileIdList = [];

// 페이지 로드 시 기존 등록된 파일을 filesMap에 저장
function initializeRegisteredFiles() {
    const registeredDocumentsElement = document.getElementById('fileList');

    if (registeredDocumentsElement) {
        // 기존 파일 목록에서 파일 정보를 가져옴
        const fileElements = registeredDocumentsElement.querySelectorAll('li');

        fileElements.forEach((fileElement, index) => {
            const fileId = fileElement.querySelector('.editFileId').value;

            if (fileId) {
                // 기존 파일을 filesMap에 추가
                registeredFileIdList.push(fileId);
            }
        });
    }
    console.log("registeredFileIdList 설정완료", registeredFileIdList);
}

// 보고서 수정 시 파일 데이터를 처리하고 제출하는 함수
function submitUpdatedFiles(event, url) {
    console.log(url);
    event.preventDefault();

    const form = document.getElementById('form');
    const formData = new FormData(form); // 기존 폼 데이터 가져오기

    const idList = Object.keys(selectedEmployees);
    const nameList = Object.values(selectedEmployees);

    // 결재자를 변경하지 않은 경우, 기존 결재자를 idList와 nameList에 추가
    if (idList.length === 0 && nameList.length === 0) {
        const currentApproverId = document.getElementById('currentApproverId').value;
        const currentApproverName = document.getElementById('currentApproverName').value;

        idList.push(currentApproverId);
        nameList.push(currentApproverName);
    }

    // 폼 데이터에 idList와 nameList 추가
    formData.set('idList', idList.join(','));
    formData.set('nameList', nameList.join(','));

    // 기존 파일 전송
    if (registeredFileIdList && registeredFileIdList.length > 0) {
        // registeredFileIdList.forEach((fileId) => {
        formData.set('registeredFileIdList', JSON.stringify(registeredFileIdList));
        // });
    }

    // 새로 업로드된 파일 전송
    if (filesArr && filesArr.length > 0) {
        filesArr.forEach((file) => {
            if (!file.is_delete) { // 삭제된 파일 제외
                formData.append('reportFileList', file);
            }
        });
    }

    for (let pair of formData.entries()) {
        console.log(`${pair[0]}: ${pair[1]}`);
    }

    let fileCount = 0;
    for (let pair of formData.entries()) {
        if (pair[0] === 'reportFileList') {
            console.log(`파일 ${++fileCount}: 이름 - ${pair[1].name}, 크기 - ${pair[1].size}`);
        } else {
            console.log(`${pair[0]}: ${pair[1]}`);
        }
    }

    fetch(url, {
        method: 'POST',
        body: formData,
    }).then(response => response.text().then(data => ({
        status: response.status,
        text: data
    })))
        .then(response => {
            if (response.status === 200) {
                alert('보고서 수정이 완료되었습니다.');
                window.location.href = '/report/list';
                // window.location.href = "/report/list";
            } else {
                alert('보고서 수정 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('보고서 수정 중 오류가 발생했습니다.');
        });
}
