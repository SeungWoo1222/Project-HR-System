//========================================결재 상태 변경 =================================================================

// 결재 상태 변경 시 "거절"을 선택한 경우 거절사유를 적는 칸이 생김
function toggleRejectionReason() {
    var status = document.getElementById("status").value;
    var rejectionReasonContainer = document.getElementById("rejectionReasonContainer");
    if (status === "거절") {
        rejectionReasonContainer.style.display = "block";
        rejectionReason.setAttribute("required", "required");
    } else {
        rejectionReasonContainer.style.display = "none";
        rejectionReason.removeAttribute("required");
    }
}

//========================================결재 상태 변경 =================================================================

//========================================== 임원 선택 ==================================================================
selectedEmployees = {}; // 선택된 임원들 목록 초기화 - 다른 부서 임원을 고르기 위한 변수

//  부서 선택, 임원 전체선택 메소드 정의
function initEventListeners() {
    const departmentSelect = document.getElementById('departmentId');
    const selectAllCheckbox = document.getElementById('selectAllEmployeesCheckbox');

    departmentSelect.addEventListener('change', loadEmployeesByDepartment);
    selectAllCheckbox.addEventListener('change', toggleSelectAllEmployees);

}

// 선택된 부서에 맞는 임원들 리스트를 반환해줌
function loadEmployeesByDepartment() {
    const departmentId = document.getElementById('departmentId').value;

    fetch(`/api/employee/department/list/` + departmentId)
        .then(response => {
            if (!response.ok) {
                throw new Error('response 오류!');
            }
            return response.json();
        })
        .then(employeeList => {
            const idContainer = document.getElementById('idContainer');
            idContainer.innerHTML = '';

            employeeList.forEach(employee => {
                    const checkboxContainer = document.createElement('div');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.className = 'idCheckbox';
                    // checkbox.name = 'id';
                    checkbox.value = employee.employeeId;
                    checkbox.dataset.name = employee.name;

                    // 이미 선택된 임원들은 체크 상태 유지
                    if (selectedEmployees[employee.employeeId]) {
                        checkbox.checked = true;
                    }

                    checkbox.addEventListener('change', updateSelectedEmployees);

                    const nameInput = document.createElement('input');
                    nameInput.type = 'hidden';
                    nameInput.className = 'nameInput';
                    // nameInput.name = 'name';
                    nameInput.value = employee.name;

                    const label = document.createElement('label');
                    label.textContent = employee.name;

                    checkboxContainer.appendChild(checkbox);
                    checkboxContainer.appendChild(label);
                    checkboxContainer.appendChild(nameInput);
                    idContainer.appendChild(checkboxContainer);
            });

            // 임원 전체선택 체크박스를 보여줌
            const selectAllContainer = document.getElementById('selectAllContainer');
            if (selectAllContainer) {
                selectAllContainer.style.display = 'block';
            }
        })
        .catch(error => console.error('데이터를 읽어 올 수 없음', error));
}

// 임원 전체 선택
function toggleSelectAllEmployees() {
    const checkboxes = document.querySelectorAll('#idContainer input[type="checkbox"]');
    const isChecked = document.getElementById('selectAllEmployeesCheckbox').checked;
    checkboxes.forEach(checkbox => {
        checkbox.checked = isChecked;
        checkbox.dispatchEvent(new Event('change'));
    });
}

// 선택된 임원을 나열함
function updateSelectedEmployees() {
    const selectedEmployeesContainer = document.getElementById('selectedEmployees');
    selectedEmployeesContainer.innerHTML = '';

    const selectedCheckboxes = document.querySelectorAll('#idContainer input[type="checkbox"]:checked');
    selectedCheckboxes.forEach(checkbox => {
        selectedEmployees[checkbox.value] = checkbox.dataset.name;
    });

    // 선택된 임원들을 다시 나열
    Object.keys(selectedEmployees).forEach(id => {
        const div = document.createElement('div');
        div.className = 'selected-employee';
        div.textContent = selectedEmployees[id];
        const removeButton = document.createElement('button');
        removeButton.textContent = 'x';
        removeButton.addEventListener('click', () => {
            delete selectedEmployees[id];
            document.querySelector(`#idContainer input[value="${id}"]`).checked = false;
            updateSelectedEmployees(); // 선택된 임원 목록 갱신
        });
        div.appendChild(removeButton);
        selectedEmployeesContainer.appendChild(div);
    });
}

//=================================================== 임원 선택 =========================================================
//================================================= 파일 생성 관련 js ===================================================
// 보고서 생성 시
function submitReport(event, url) {
    event.preventDefault(); // 기본 폼 제출 방지

    const form = document.getElementById('form');
    const formData = new FormData(form); // 기존 폼 데이터 가져오기

    // filesArr에 저장된 파일들을 FormData에 추가
    filesArr.forEach((file, index) => {
        if (!file.is_delete) { // 삭제된 파일 제외
            formData.append('reportFiles', file);
        }
    });

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
            alert(response.text);
            window.location.href = "/report/list"; // 성공 후 리디렉션
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
