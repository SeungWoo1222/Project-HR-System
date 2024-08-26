// 전역변수
let registeredFileIdList = [];
let fileNo = 0;
let filesArr = [];

// 기존 등록된 파일ID를 registeredFileIdList 저장
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
        console.log(registeredFileIdList);
    }
}

// x버튼을 누르면 registeredFileIdList에서 파일 ID를 제거
function deleteRegisteredFile(button, fileId) {
    console.log(fileId);
    const index = registeredFileIdList.indexOf(fileId);
    console.log(index);
    if (index > -1) {
        registeredFileIdList.splice(index, 1);
    }

    // UI에서 파일 항목을 삭제
    const fileItem = button.closest('li');
    fileItem.remove();

    console.log('현재 등록된 파일 ID 목록:', registeredFileIdList);
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
    } else if (filesArr.some(existingFile => existingFile.name === obj.name && existingFile.size === obj.size)) {
        alert("동일한 이름과 크기의 파일이 이미 추가되어 있습니다.");
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

// ============================================================제출 예시============================================================
// 보고서 수정 시 파일 데이터를 처리하고 제출하는 함수
function submitUpdatedFiles(event, url) {
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
                window.location.href = response.text;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error);
        });
}
