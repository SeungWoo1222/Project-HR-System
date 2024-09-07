let fileNo = 0;
let filesArr = [];
let oldFileIdArr = [];

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
    } else if (obj.name.lastIndexOf('.') === -1) {
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