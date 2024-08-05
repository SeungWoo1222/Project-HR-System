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

let selectedEmployees; // 객체 선언

//  부서 선택 시, 임원 전체 선택 체크박스 선택 시 메소드 실행
function initEventListeners() {
    const departmentSelect = document.getElementById('departmentId');
    const selectAllCheckbox = document.getElementById('selectAllEmployeesCheckbox');

    if (departmentSelect) {
        console.log("departmentSelect ok");
        departmentSelect.addEventListener('change', loadEmployeesByDepartment);
    } else {
        console.log("departmentSelect is null");
    }

    if (selectAllCheckbox) {
        console.log("selectAllCheckbox ok");
        selectAllCheckbox.addEventListener('change', toggleSelectAllEmployees);
    } else {
        console.log("selectAllEmployeesCheckbox is null");
    }
}

// 선택된 부서에 맞는 임원들 리스트를 반환해줌
function loadEmployeesByDepartment() {
    const departmentId = document.getElementById('departmentId').value;

    fetch(`/admin/request/employee?departmentId=${departmentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('response 오류!');
            }
            return response.json();
        })
        .then(employees => {
            const writerIdContainer = document.getElementById('writerIdContainer');
            writerIdContainer.innerHTML = '';

            employees.forEach(employee => {
                if (employee === null) {
                    console.error('Employee가 비어 있음', employee);
                } else if (!employee.employeeId || !employee.name) {
                    console.error('Employee는 있으나 요소가 없음', employee);
                } else {
                    const checkboxContainer = document.createElement('div');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.className = 'writerIdCheckbox';
                    checkbox.name = 'writerId';
                    checkbox.value = employee.employeeId;
                    checkbox.dataset.name = employee.name;

                    // 이미 선택된 임원들은 체크 상태 유지
                    if (selectedEmployees[employee.employeeId]) {
                        checkbox.checked = true;
                    }

                    checkbox.addEventListener('change', updateSelectedEmployees);

                    const nameInput = document.createElement('input');
                    nameInput.type = 'hidden';
                    nameInput.className = 'writerNameInput';
                    nameInput.name = 'writerName';
                    nameInput.value = employee.name;

                    const label = document.createElement('label');
                    label.textContent = employee.name;

                    checkboxContainer.appendChild(checkbox);
                    checkboxContainer.appendChild(label);
                    checkboxContainer.appendChild(nameInput);
                    writerIdContainer.appendChild(checkboxContainer);
                }
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
    const checkboxes = document.querySelectorAll('#writerIdContainer input[type="checkbox"]');
    console.log("ok2");
    const isChecked = document.getElementById('selectAllEmployeesCheckbox').checked;
    checkboxes.forEach(checkbox => {
        checkbox.checked = isChecked;
        // checkbox.checked = true;
        checkbox.dispatchEvent(new Event('change'));
    });
}

// 선택된 임원을 나열함
function updateSelectedEmployees() {
    const selectedEmployeesContainer = document.getElementById('selectedEmployees');
    selectedEmployeesContainer.innerHTML = '';

    const selectedCheckboxes = document.querySelectorAll('#writerIdContainer input[type="checkbox"]:checked');
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
            document.querySelector(`#writerIdContainer input[value="${id}"]`).checked = false;
            updateSelectedEmployees(); // 선택된 임원 목록 갱신
        });
        div.appendChild(removeButton);
        selectedEmployeesContainer.appendChild(div);
    });
}

function updateFormFields() {
    const writerIdList = Object.keys(selectedEmployees);
    const writerNameList = Object.values(selectedEmployees);

    const requestForm = document.getElementById('requestForm');

    // 최종 선택된 요소들의 필드만 생성
    writerIdList.forEach(id => {
        let writerIdField = document.createElement('input');
        writerIdField.type = 'hidden';
        writerIdField.name = 'writerIdList';
        writerIdField.value = id;
        requestForm.appendChild(writerIdField);
    });

    writerNameList.forEach(name => {
        let writerNameField = document.createElement('input');
        writerNameField.type = 'hidden';
        writerNameField.name = 'writerNameList';
        writerNameField.value = name;
        requestForm.appendChild(writerNameField);
    });
}

//========================================== 임원 선택 ==================================================================
//========================================== 통계 관련 메소드 ============================================================

// 통계 - 선택된 임원 목록 중 임원을 삭제하는 메소드
function removeWriter(writerId) {
    // 선택된 임원 목록에서 해당 임원 삭제
    const writerElement = document.querySelector(`button[onclick="removeWriter('${writerId}')"]`).parentNode;
    writerElement.remove();

    // 선택된 임원 ID 목록 갱신
    const remainingWriterIds = [];
    document.querySelectorAll('#selected-writers-list li button').forEach(button => {
        remainingWriterIds.push(button.getAttribute('onclick').match(/removeWriter\('(.+?)'\)/)[1]);
    });

    // 서버에 갱신된 임원 목록을 전송하여 통계 데이터를 갱신
    updateChartWithSelectedWriters(remainingWriterIds);
}

// main.html에 통계를 다시 갱신하는 매소드(임원 삭제 후)
function updateChartWithSelectedWriters(writerIds) {
    $.ajax({
        url: '/admin/request/updateStats',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(writerIds),
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
