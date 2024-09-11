// 휴가 유형에 따른 폼 노출 조정 함수
function updateFormVisibility() {
    const vacationTypeSelect = document.getElementById("vacationType");
    const dateAtGroup = document.querySelector(".form-group #dateAt").parentElement;
    const startAtGroup = document.querySelector(".form-group #startAt").parentElement;
    const endAtGroup = document.querySelector(".form-group #endAt").parentElement;

    const selectedValue = vacationTypeSelect.value;

    if (selectedValue === "오전 반차" || selectedValue === "오후 반차") {
        // 반차 선택 시
        dateAtGroup.style.display = "block";
        startAtGroup.style.display = "none";
        endAtGroup.style.display = "none";
    } else {
        // 연차 또는 기타 휴가 선택 시
        dateAtGroup.style.display = "none";
        startAtGroup.style.display = "block";
        endAtGroup.style.display = "block";
    }
}

// 휴가일 설정 시 휴가 시작일과 종료일 설정 함수
function setDateAt() {
    const dateAtValue = document.getElementById("dateAt").value;
    document.getElementById("startAt").value = dateAtValue;
    document.getElementById("endAt").value = dateAtValue;
    calculateUsedDays();
}

// 공휴일 배열 (전역 변수로 사용)
let holidays = {};
let holidaysFetchedYearMonth = new Set(); // 이미 불러온 연도를 저장

// 공휴일 불러오는 함수 (콜백 사용)
function getHolidays(year, month, callback) {
    // 이미 데이터를 가져온 경우 바로 콜백 실행
    if (holidaysFetchedYearMonth.has(`${year}-${month}`)) {
        callback();
        return;
    }

    var xhr = new XMLHttpRequest();
    var url = 'http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo'; // API URL
    var serviceKey = 'RAkHJ6RjhKqi3n0QzIOSv691iUpS%2B7e5vbMU4Epf5iGdCpHQI34ji%2FPQstUq8wWtEwxYwB48qYoBOH7DcCbqNg%3D%3D';

    // 요청 파라미터 설정
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + serviceKey; // 서비스 키
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); // 페이지 번호
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('100'); // 한 페이지에 가져올 데이터 수
    queryParams += '&' + encodeURIComponent('solYear') + '=' + encodeURIComponent(year); // 요청할 연도
    queryParams += '&' + encodeURIComponent('solMonth') + '=' + encodeURIComponent(month); // 요청할 월
    xhr.open('GET', url + queryParams);

    // 응답 처리
    xhr.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            // XML 응답을 파싱
            var parser = new DOMParser();
            var xmlDoc = parser.parseFromString(this.responseText, "text/xml");

            // 공휴일 데이터를 추출 (공휴일 날짜는 <locdate> 태그에 있음)
            var items = xmlDoc.getElementsByTagName("item");

            // 해당 년월의 공휴일 배열이 없으면 생성
            if (!holidays[`${year}-${month}`]) {
                holidays[`${year}-${month}`] = [];
            }

            for (var i = 0; i < items.length; i++) {
                var locdate = items[i].getElementsByTagName("locdate")[0].textContent;

                // locdate를 yyyy-mm-dd 형식으로 변환하여 holidays 배열에 추가
                var formattedDate = locdate.slice(0, 4) + '-' + locdate.slice(4, 6) + '-' + locdate.slice(6, 8);
                holidays[`${year}-${month}`].push(formattedDate);
            }

            holidaysFetchedYearMonth.add(`${year}-${month}`); // 해당 년월 저장
            callback(); // 콜백 호출
            console.log(`공휴일 데이터(${year}-${month}):`, holidays[`${year}-${month}`]); // 공휴일 데이터 출력
        }
    };

    // 요청 전송
    xhr.send();
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
        usedDaysInput.value = "0.5";
    } else if (vacationType === "연차") {
        if (startAt && endAt) {
            // 공휴일 데이터 가져옴
            let startYear = startDate.getFullYear();
            let startMonth = (startDate.getMonth() + 1).toString().padStart(2, '0');
            let endYear = endDate.getFullYear();
            let endMonth = (endDate.getMonth() + 1).toString().padStart(2, '0');

            // 공휴일 데이터를 다 가져온 후 연차 계산
            const calculate = () => {
                let dayDifference = 0;
                while (startDate <= endDate) {
                    const dayOfWeek = startDate.getDay();
                    const formattedDate = startDate.toISOString().split('T')[0]; // yyyy-mm-dd 형식으로 변환

                    // 주말과 공휴일이 아닌 경우만 카운트
                    if (dayOfWeek !== 0 && dayOfWeek !== 6 &&
                        !holidays[`${startYear}-${startMonth}`].includes(formattedDate) &&
                        (!holidays[`${endYear}-${endMonth}`] || !holidays[`${endYear}-${endMonth}`].includes(formattedDate))) {
                        dayDifference++;
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
            };

            // 공휴일 데이터를 불러오고 나서 계산
            if (startYear === endYear && startMonth === endMonth) {
                getHolidays(startYear, startMonth, calculate);
            } else {
                // 시작일과 종료일이 다른 연도나 월에 있을 경우
                getHolidays(startYear, startMonth, () => getHolidays(endYear, endMonth, calculate));
            }
        } else {
            document.getElementById("error-message").textContent = "시작일과 종료일을 모두 선택하세요.";
        }
    } else {
        if (startAt && endAt) {
            const timeDifference = endDate - startDate;
            const dayDifference = timeDifference / (1000 * 60 * 60 * 24);
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

// 유효성 검사
function validateForm() {
    const vacationType = document.getElementById("vacationType").value;
    const startAt = document.getElementById("startAt").value;
    const endAt = document.getElementById("endAt").value;
    const usedDays = document.getElementById("usedDays").value;
    const reason = document.getElementById("reason").value;
    const remainingLeave = document.getElementById("remainingLeave").value;

    // 휴가 유형 체크
    if (!vacationType) {
        document.getElementById("error-message").textContent = "휴가 유형을 선택해주세요.";
        return false;
    }

    // 시작일과 종료일 체크
    if (!startAt || !endAt) {
        document.getElementById("error-message").textContent = "휴가 시작일과 종료일을 입력해주세요.";
        return false;
    }

    // 사용 일수가 잔여 연차보다 많으면 오류 메세지 출력
    if (parseFloat(usedDays) > parseFloat(remainingLeave)) {
        document.getElementById("error-message").textContent = "사용 일수가 잔여 연차보다 많습니다.";
        return false;
    }

    // 사용 일수 체크
    if (parseFloat(usedDays) < 0) {
        document.getElementById("error-message").textContent = "유효한 연차 사용 일수를 입력해주세요.";
        return false;
    }

    // 휴가 사유 체크
    if (!reason.trim()) {
        document.getElementById("error-message").textContent = "휴가 사유를 입력해주세요.";
        return false;
    }

    // 오류 메시지 없애기
    document.getElementById("error-message").textContent = "";
    return true;
}


// AJAX POST 요청 - 휴가 신청
function submitInsertForm(event, requestor) {
    event.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
        return;
    }

    const form = event.target;
    const actionUrl = form.action;

    // 날짜 필드의 값 가져와 (현재 yyyy-mm-dd 형식)
    const startDate = document.getElementById("startAt").value;
    const endDate = document.getElementById("endAt").value;
    let vacationType = document.getElementById("vacationType").value;

    // LocalDateTime 형식으로 변경
    let startAt;
    let endAt;
    switch (vacationType) {
        case '오전 반차':
            startAt = `${startDate}T09:00:00`;
            endAt = `${endDate}T14:00:00`;
            vacationType = '연차';
            break;
        case '오후 반차':
            startAt = `${startDate}T13:00:00`;
            endAt = `${endDate}T18:00:00`;
            vacationType = '연차';
            break;
        default:
            startAt = `${startDate}T09:00:00`;
            endAt = `${endDate}T18:00:00`;
            break;
    }

    // 폼 데이터 생성
    const formData = new FormData(form);

    // 기존 값 덮어쓰기
    formData.set("startAt", startAt);
    formData.set("endAt", endAt);
    formData.set("vacationType", vacationType);

    if (confirm('휴가를 신청하시겠습니까?')) {
        fetch(actionUrl, {
            method: "POST",
            body: formData
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    if (requestor === 'employee') window.location.href = '/vacation/employee';
                    else { window.location.href = '/vacation/list'; }
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('휴가 신청 중 오류가 발생하였습니다.\n재신청 시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}
// 사원이 선택되면 서버로 API 요청해서 잔여 연차를 받아와서 업데이트
function fetchEmployeeInfo(employeeId) {
    fetch(`/api/employee/` + employeeId + '/remainingLeave')
        .then(response => {
            if (!response.ok) {
                alert('잔여 연차 정보를 불러오는데 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {
            // 잔여 연차 값을 input 필드에 업데이트
            document.getElementById("remainingLeave").value = data.remainingLeave;
        })
        .catch(error => {
            console.error('Error :', error.message);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}