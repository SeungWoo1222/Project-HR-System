// 휴가 유형에 따른 폼 노출 조정 함수
function updateFormVisibility() {
    const vacationTypeSelect = document.getElementById("vacationType");
    const dateAt = document.getElementById("dateAt");
    const startAt = document.getElementById("startAt");
    const endAt = document.getElementById("endAt");

    const selectedValue = vacationTypeSelect.value;

    if (selectedValue === "오전 반차" || selectedValue === "오후 반차") {
        // 반차 선택 시
        dateAt.style.display = "block";
        startAt.style.display = "none";
        endAt.style.display = "none";
    } else {
        // 연차 또는 기타 휴가 선택 시
        dateAt.style.display = "none";
        startAt.style.display = "block";
        endAt.style.display = "block";
    }
}

// 휴가일 설정 시 휴가 시작일과 종료일 설정 함수
function setDateAt() {
    const dateAtValue = document.getElementById("dateAt").value;
    document.getElementById("startAt").value = dateAtValue;
    document.getElementById("endAt").value = dateAtValue;
    calculateUsedDays();
}

// 연차 사용 일 수 계산
function calculateUsedDays() {
    const vacationType = document.getElementById("vacationType").value;
    const usedDaysInput = document.getElementById("usedDays");

    const startAt = document.getElementById("startAt").value;
    const endAt = document.getElementById("endAt").value;

    // 날짜 값을 Date 객체로 변환
    let startDate = new Date(startAt);
    let endDate = new Date(endAt);

    document.getElementById("error-message").textContent = "";

    if (vacationType === "오전 반차" || vacationType === "오후 반차") {
        if (startAt) {
            const dayOfWeek = startDate.getDay(); // 요일 (0: 일요일, 6: 토요일)
            const formattedDate = startDate.toISOString().split('T')[0]; // yyyy-mm-dd 형식으로 변환

            // 공휴일이나 주말 확인
            const isHoliday = holidays.some(holiday => holiday.locDate === formattedDate);

            if (dayOfWeek === 0 || dayOfWeek === 6 || isHoliday) {
                alert("선택한 날짜는 공휴일이거나 주말입니다. 다른 날짜를 선택해주세요.");
                document.getElementById("dateAt").value = "";
                document.getElementById("startAt").value = "";
                document.getElementById("endAt").value = "";
                usedDaysInput.value = "";
                return;
            }

            usedDaysInput.value = "0.5";
        } else {
            document.getElementById("error-message").textContent = "시작일을 선택하세요.";
        }
    } else if (vacationType === "연차") {
        if (startAt && endAt) {
            // 연차 계산 로직
            let dayDifference = 0;
            while (startDate <= endDate) {
                const dayOfWeek = startDate.getDay(); // 요일 (0: 일요일, 6: 토요일)
                const formattedDate = startDate.toISOString().split('T')[0]; // yyyy-mm-dd 형식으로 변환

                // 주말과 공휴일이 아닌 경우만 카운트
                const isHoliday = holidays.some(holiday => holiday.locDate === formattedDate);

                if (dayOfWeek !== 0 && dayOfWeek !== 6 && !isHoliday) {
                    dayDifference++; // 주말이나 공휴일이 아니면 카운트 증가
                }

                // 날짜를 하루 증가
                startDate.setDate(startDate.getDate() + 1);
            }

            // 차이를 화면에 출력
            if (dayDifference > 0) {
                usedDaysInput.value = dayDifference;
            } else {
                alert("종료일은 시작일보다 이후여야 합니다.");
                document.getElementById("endAt").value = "";
            }
        } else {
            document.getElementById("error-message").textContent = "시작일과 종료일을 모두 선택하세요.";
        }
    } else {
        if (startAt && endAt) {
            const timeDifference = endDate - startDate;
            const dayDifference = timeDifference / (1000 * 60 * 60 * 24); // 시간 차이를 일 단위로 변환
            if (dayDifference >= 0) {
                usedDaysInput.value = 0;
            } else {
                alert("종료일은 시작일보다 이후여야 합니다.");
                document.getElementById("endAt").value = "";
            }
        } else {
            document.getElementById("error-message").textContent = "시작일과 종료일을 모두 선택하세요.";
        }
    }
}

// 사원이 선택되면 서버로 API 요청해서 사원 정보를 받아와서 업데이트
function fetchEmployeeInfo(employeeId) {
    fetch(`/api/employee/` + employeeId)
        .then(response => {
            if (!response.ok) {
                alert('사원 정보를 불러오는데 실패했습니다.');
            }
            return response.json();
        })
        .then(employee => {
            // 사원 정보를 input 필드에 업데이트
            document.getElementById("department").textContent = employee.department;
            document.getElementById("position").textContent = employee.position;
            document.getElementById("phone").textContent = employee.phone;
            document.getElementById("remainingLeave").textContent = employee.remainingLeave;
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}