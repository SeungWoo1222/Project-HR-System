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