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
    }
}

// x버튼을 누르면 registeredFileIdList에서 파일 ID를 제거
function deleteRegisteredFile(button, fileId) {
    const index = registeredFileIdList.indexOf(fileId);
    if (index > -1) {
        registeredFileIdList.splice(index, 1);
    }

    // UI에서 파일 항목을 삭제
    const fileItem = button.closest('li');
    fileItem.remove();
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
    } else if (filesArr.some(existingFile => existingFile.name === obj.name && existingFile.size === obj.size && !existingFile.is_delete)) {
        alert("동일한 파일이 이미 추가되어 있습니다.");
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
    // filesArr[num].is_delete = true;
    filesArr[num].is_delete = true;
}
