// 색상 선택 시 색상 미리보기 업데이트
function changeColor() {
    var selectedColor = document.getElementById('color').value;
    document.getElementById('colorPreview').style.backgroundColor = selectedColor;
}

// 하루종일 체크 상태에 따른 date와 dateTime 필드 변경
function toggleDateTimeFields() {
    const allDayCheckbox = document.querySelector(".switch input[type='checkbox']");
    const startDateTime = document.getElementById("startDateTime");
    const startDate = document.getElementById("startDate");
    const endDateTime = document.getElementById("endDateTime");
    const endDate = document.getElementById("endDate");

    if (allDayCheckbox.checked) {
        startDateTime.style.display = "none";
        startDate.style.display = "block";
        endDateTime.style.display = "none";
        endDate.style.display = "block";
    } else {
        startDateTime.style.display = "block";
        startDate.style.display = "none";
        endDateTime.style.display = "block";
        endDate.style.display = "none";
    }
}