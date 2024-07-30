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

//     function toggleEmployeeNameField() {
//     const specificOption = document.getElementById('specificOption');
//     const employeeNameField = document.getElementById('employeeName');
//     employeeNameField.disabled = !specificOption.checked;
// }

// 임원 선택 시, 부서 선택하고 임원을 선택함
$(document).ready(function() {
    // 초기에는 전체 선택 체크박스 숨기기
    $('#selectAllContainer').hide();

    // 부서 선택 시 직원 목록을 불러오는 함수
    $('#departmentId').on('change', function() {
        var departmentId = $(this).val();

        $.ajax({
            url: '/admin/request/employee', // API 엔드포인트를 설정
            method: 'GET',
            data: { departmentId: departmentId },
            success: function(data) {
                const select = $('#writerIdContainer');
                select.empty(); // 기존 옵션 초기화
                data.forEach(function(employee) {
                    const checkboxContainer = $('<div></div>');
                    const checkbox = $('<input type="checkbox" class="writerIdCheckbox" name="writerId" value="' + employee.employeeId + '">');
                    const nameInput = $('<input type="hidden" class="writerNameInput" name="writerName" value="' + employee.name + '">');
                    const label = $('<label>' + employee.name + '</label>');
                    checkboxContainer.append(checkbox).append(label).append(nameInput);
                    select.append(checkboxContainer);
                });
                $('#selectAllContainer').show(); // 부서 선택 후에 전체 선택 체크박스 보이기
            },
            error: function(error) {
                console.error('Error:', error);
            }
        });
    });

    // 모든 직원을 선택/해제하는 함수
    $('#selectAllEmployeesCheckbox').on('change', function() {
        const isChecked = $(this).prop('checked');
        $('.writerIdCheckbox').prop('checked', isChecked);
        addSelectedEmployees();
    });

    // 선택된 직원을 목록에 추가하는 함수
    $('#writerIdContainer').on('change', '.writerIdCheckbox', function() {
        addSelectedEmployees();
    });

    function addSelectedEmployees() {
        const selectedContainer = $('#selectedEmployees');
        selectedContainer.empty();
        $('.writerIdCheckbox:checked').each(function() {
            const id = $(this).val();
            const name = $(this).siblings('.writerNameInput').val(); // name을 가져오기
            const employeeDiv = $('<div class="selected-employee" data-id="' + id + '"></div>');
            employeeDiv.append('<span>' + name + '</span>');
            const removeButton = $('<button type="button">x</button>');
            removeButton.on('click', function() {
                $(this).parent().remove();
                $('#writerIdContainer input[value="' + id + '"]').prop('checked', false);
            });
            employeeDiv.append(removeButton);
            selectedContainer.append(employeeDiv);
        });
    }

    // 폼 제출 시 체크되지 않은 writerName 필드 제거
    $('form').on('submit', function(event) {
        $('.writerIdCheckbox').each(function() {
            if (!$(this).is(':checked')) {
                $(this).siblings('.writerNameInput').remove();
            }
        });
    });
});

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