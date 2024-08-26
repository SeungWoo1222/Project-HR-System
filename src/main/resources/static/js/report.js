//========================================결재 상태 변경 =================================================================

// 결재 상태 변경 시 "거절"을 선택한 경우 거절 사유를 적는 칸이 생김
function toggleRejectionReason(status, rejectReason) {

    const selectElement = document.getElementById(status);
    const rejectionReasonContainer = document.getElementById(rejectReason);

    console.log(selectElement);
    console.log(rejectionReasonContainer);

    // 요소가 존재하지 않으면 오류 메시지 출력
    if (!selectElement || !rejectionReasonContainer) {
        console.error("요소를 찾을 수 없습니다. 사용된 ID를 확인하세요.");
        return;
    }

    // "거절"을 선택했을 때 거절 사유 입력란 표시
    if (selectElement.value === "거절") {
        rejectionReasonContainer.style.display = "block";
        rejectionReasonContainer.querySelector('textarea').setAttribute("required", "required");
    } else {
        rejectionReasonContainer.style.display = "none";
        rejectionReasonContainer.querySelector('textarea').removeAttribute("required");
    }
}

//
function toggleApprovalSection(formId) {
    var approvalForm = document.getElementById(formId);
    approvalForm.style.display = approvalForm.style.display === 'none' ? 'block' : 'none';
}


//========================================결재 상태 변경 =================================================================

//========================================== 임원 선택 ==================================================================

// 부서 선택 및 전체 선택 이벤트 리스너 초기화
function initEventListeners() {
    const departmentSelect = document.getElementById('departmentId');
    const selectAllButton = document.getElementById('selectAllEmployeesButton'); // 전체 선택 버튼
    const deselectAllButton = document.getElementById('deselectAllEmployeesButton'); // 전체 해제 버튼

    departmentSelect.addEventListener('change', loadEmployeesByDepartment);
    if (selectAllButton) {
        selectAllButton.addEventListener('click', selectAllEmployees); // 전체 선택 이벤트 리스너
    }
    if (deselectAllButton) {
        deselectAllButton.addEventListener('click', deselectAllEmployees); // 전체 해제 이벤트 리스너
    }
}

// 선택된 부서에 맞는 임원들 리스트를 반환해줌
function loadEmployeesByDepartment() {
    const departmentId = document.getElementById('departmentId').value;

    // 부서 선택 시 employeeSection을 보여줌
    const employeeSection = document.getElementById('employeeSection');
    if (departmentId) {
        employeeSection.style.display = 'block';
    } else {
        employeeSection.style.display = 'none';
    }

    fetch(`/api/employee/department/list/` + departmentId)
        .then(response => {
            if (!response.ok) {
                throw new Error('response 오류!');
            }
            return response.json();
        })
        .then(employeeList => {
            const availableContainer = document.getElementById('idContainer');
            availableContainer.innerHTML = '';

            employeeList.forEach(employee => {
                if (!selectedEmployees[employee.employeeId]) {
                    const employeeItem = createEmployeeItem(employee.employeeId, employee.name, false);
                    availableContainer.appendChild(employeeItem);
                }
            });
        })
        .catch(error => console.error('데이터를 읽어 올 수 없음', error));
}

// 임원 리스트 아이템 생성
function createEmployeeItem(employeeId, employeeName, isSelected) {
    const div = document.createElement('div');
    div.className = 'employee-item';
    div.textContent = employeeName;

    const actionButton = document.createElement('button');
    actionButton.dataset.id = employeeId; // 버튼에 data-id 속성을 추가
    if (isSelected) {
        actionButton.textContent = '해제';
        actionButton.addEventListener('click', () => {
            delete selectedEmployees[employeeId];
            updateSelectedEmployees();
        });
    } else {
        actionButton.textContent = '선택';
        actionButton.addEventListener('click', () => {
            selectedEmployees[employeeId] = employeeName;
            updateSelectedEmployees();
        });
    }

    div.appendChild(actionButton);
    return div;
}

// 선택된 임원들을 나열함
function updateSelectedEmployees() {
    const selectedEmployeesContainer = document.getElementById('selectedEmployees');
    const availableContainer = document.getElementById('idContainer');

    selectedEmployeesContainer.innerHTML = '';
    availableContainer.innerHTML = '';

    // 선택된 임원 목록을 먼저 갱신
    Object.keys(selectedEmployees).forEach(id => {
        const employeeItem = createEmployeeItem(id, selectedEmployees[id], true);
        selectedEmployeesContainer.appendChild(employeeItem);
    });

    // 임원 리스트를 갱신하여 중복 없이 남은 임원만 다시 나열
    loadEmployeesByDepartment();
}

// 전체 선택 기능
function selectAllEmployees() {
    const employeeItems = document.querySelectorAll('#idContainer .employee-item'); // 임원 목록을 가져옴

    employeeItems.forEach(item => {
        const button = item.querySelector('button');
        const employeeId = button.dataset.id; // 버튼에 설정된 data-id 속성값을 가져옴
        const employeeName = item.textContent.replace('선택', '').trim();

        // 이미 선택된 임원이 아니라면 선택된 목록에 추가
        if (!selectedEmployees[employeeId]) {
            selectedEmployees[employeeId] = employeeName;
        }
    });

    updateSelectedEmployees();
}

// 전체 해제
function deselectAllEmployees() {
    selectedEmployees = {}; // 선택된 임원들 초기화
    updateSelectedEmployees();
}

//=================================================== 임원 선택 =========================================================
//================================================= 파일 생성 관련 js ===================================================
// 보고서 생성 시
function submitReport(event, url, redirectUrl) {
    event.preventDefault(); // 기본 폼 제출 방지

    const form = document.getElementById('form');
    const formData = new FormData(form); // 기존 폼 데이터 가져오기

    // filesArr에 저장된 파일들을 FormData에 추가
    if (filesArr != null) {
        filesArr.forEach((file, index) => {
            if (!file.is_delete) { // 삭제된 파일 제외
                formData.append('reportFiles', file);
            }
        });
    }


    if (Object.keys(selectedEmployees).length > 0) {
        const idList = Object.keys(selectedEmployees);
        const nameList = Object.values(selectedEmployees);

        // 숨겨진 필드에 값 설정
        formData.set('idList', idList.join(','));
        formData.set('nameList', nameList.join(','));
    }

    // FormData의 모든 키-값 쌍을 출력합니다.
    for (var pair of formData.entries()) {
        console.log(pair[0] + ': ' + pair[1]);
    }


    // AJAX를 사용하여 폼 데이터 전송
    fetch(url, {  // 이 부분에서 '/report/write'로 전송
        method: 'POST',
        body: formData,
    }).then(response => response.text().then(data => ({
        status: response.status,
        text: data
    })))
    .then(response => {
        if (response.status === 200) {
            alert("성공!");
            window.location.href = redirectUrl; // 전달받은 URL로 리다이렉트
        } else {
            console.log(response.status);
            console.log(response.text);
        }
    }).catch(error => {
        console.error('폼 제출 오류:', error);
        alert('폼 제출 중 오류가 발생했습니다.');
    });
}

//============================================= 파일 생성 관련 js =======================================================

//========================================== 통계 관련 메소드 ============================================================

// 통계 - 선택된 임원 목록 중 임원을 삭제하는 메소드
    function removeWriter(button) {

        // 선택된 임원 목록에서 해당 임원 삭제
        var employeeId = button.getAttribute('data-employee-id');

        // 부모 노드를 찾아서 삭제
        var parentLi = button.parentNode;
        parentLi.parentNode.removeChild(parentLi);

        // 선택된 임원 ID 목록 갱신
        const remainingIds = [];
        document.querySelectorAll('#selected-writers-list li button').forEach(button => {
            remainingIds.push(button.getAttribute('data-employee-id'));
        });

        // 선택된 임원이 모두 제거된 경우 "모든 임원" 항목을 추가
        if (remainingIds.length === 0) {
            var ul = document.getElementById('selected-writers-list');
            var allMembersLi = document.createElement("li");
            allMembersLi.innerHTML = "<span>모든 임원</span>";
            ul.appendChild(allMembersLi);
        }

        // 서버에 갱신된 임원 목록을 전송하여 통계 데이터를 갱신
        updateChartWithSelectedWriters(remainingIds);
    }

// main.html에 통계를 다시 갱신하는 매소드(임원 삭제 후)
    function updateChartWithSelectedWriters(ids) {
        $.ajax({
            url: '/admin/request/updateStats',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(ids),
            success: function (response) {
                // 갱신된 통계 데이터를 이용하여 차트를 다시 그리기
                var stats = JSON.parse(response.statsJson);

                var data = google.visualization.arrayToDataTable(stats);

                var options = {};

                var chart = new google.charts.Bar(document.getElementById('columnchart_material'));
                chart.draw(data, google.charts.Bar.convertOptions(options));
            },
            error: function (error) {
                console.error('Error:', error);
            }
        });
    }
//========================================== 통계 관련 메소드 =================================================================
//========================================== 날짜 설정 메소드 =================================================================
function validateDateRange() {
    console.log("날짜 유효성 검사 완료");
    const startDate = document.getElementById('start').value;
    const endDate = document.getElementById('end').value;

    if (startDate && endDate && startDate > endDate) {
        alert('시작 월은 종료 월보다 늦을 수 없습니다.');
        return false; // 폼 제출을 막음
    }

    return true; // 폼 제출 허용
}
//========================================== 날짜 설정 메소드 =================================================================
