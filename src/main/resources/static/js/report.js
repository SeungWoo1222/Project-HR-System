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

// 부서 선택 시, 그 부서에 속한 임원들 데이터를 받아옴
function loadEmployeesByDepartment() {
    var departmentId = $('#departmentId').val();

    $.ajax({
        url: '/admin/request/employee', // API 엔드포인트를 설정
        method: 'GET',
        data: { departmentId: departmentId },
        success: function(data) {
            populateEmployeeList(data);
            $('#selectAllContainer').html('<label for="selectAllEmployeesCheckbox">부서 임원 전체 선택</label><input type="checkbox" id="selectAllEmployeesCheckbox">');
            $('#selectAllContainer').show(); // 부서 선택 후에 전체 선택 체크박스 보이기

            // 새로 생성된 체크박스에 이벤트 핸들러 설정
            $('#selectAllEmployeesCheckbox').on('change', toggleSelectAllEmployees);
        },
        error: function(error) {
            console.error('Error:', error);
        }
    });
}

// 부서에 속한 임원들 선택지 생성
function populateEmployeeList(data) {
    const select = $('#writerIdContainer');
    select.empty(); // 기존 옵션 초기화
    data.forEach(function(employee) {
        const checkboxContainer = $('<div></div>');
        const checkbox = $('<input type="checkbox" class="writerIdCheckbox" name="writerIdList" value="' + employee.employeeId + '">');
        const label = $('<label>' + employee.name + '</label>');
        checkboxContainer.append(checkbox).append(label);
        select.append(checkboxContainer);
    });

    // 체크박스에 이벤트 리스너 추가
    $('.writerIdCheckbox').on('change', function() {
        updateSelectedEmployees();
    });
}

// 전체선택 체크박스 생성
function toggleSelectAllEmployees() {
    const isChecked = $('#selectAllEmployeesCheckbox').prop('checked');
    $('.writerIdCheckbox').prop('checked', isChecked);
    updateSelectedEmployees();
}

// 선택된 임원들을 업데이트
function updateSelectedEmployees() {
    const selectedContainer = $('#selectedEmployees');
    selectedContainer.empty(); // 이전에 추가된 필드 제거

    // 선택된 임원들의 ID와 이름을 추가
    $('.writerIdCheckbox:checked').each(function() {
        const id = $(this).val();
        const name = $(this).siblings('label').text();

        // 선택된 직원의 ID와 이름을 포함하는 숨겨진 필드를 폼에 추가
        const hiddenNameInput = $('<input type="hidden" name="writerNameList" value="' + name + '">');
        selectedContainer.append(hiddenNameInput);

        // 사용자에게 선택된 임원 표시
        const employeeDiv = $('<div class="selected-employee" data-id="' + id + '"></div>');
        employeeDiv.append('<span>' + name + '</span>');
        const removeButton = $('<button type="button">x</button>');
        removeButton.on('click', function() {
            $(this).parent().remove();
            $('#writerIdContainer input[value="' + id + '"]').prop('checked', false);
            updateSelectedEmployees(); // 선택 사항 업데이트
        });
        employeeDiv.append(removeButton);
        selectedContainer.append(employeeDiv);
    });
}


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
        success: function(response) {
            // 갱신된 통계 데이터를 이용하여 차트를 다시 그리기
            var stats = JSON.parse(response.statsJson);
            var data = google.visualization.arrayToDataTable(stats);

            var options = {};

            var chart = new google.charts.Bar(document.getElementById('columnchart_material'));
            chart.draw(data, google.charts.Bar.convertOptions(options));
        },
        error: function(error) {
            console.error('Error:', error);
        }
    });
}