var hadTripInfo = false;
// 지도 표시 로직
function viewMap() {
    const address = document.getElementById('sample6_address').value;
    const detailAddress = document.getElementById('sample6_detailAddress').value;

    if (!address) {
        alert('주소를 입력하세요.');
        return;
    }

    document.getElementById('map-section').style.display = 'flex';
    document.getElementById('mapButton2').style.display = 'block';
    document.getElementById('mapButton').style.display = 'none';

    var fullAddress = address + ' ' + detailAddress;

    // 지도를 페이지 내에 표시
    naver.maps.Service.geocode({query: fullAddress}, function (status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
            alert('Geocode Error');
            return;
        }

        if (response.v2.addresses.length > 0) {
            var x = parseFloat(response.v2.addresses[0].x);
            var y = parseFloat(response.v2.addresses[0].y);

            var newCenter = new naver.maps.LatLng(y, x);

            var mapOptions = {
                center: new naver.maps.LatLng(37.3595704, 127.105399),
                zoom: 15,
                minZoom: 8,
                maxZoom: 19,
                zoomControl: true,
                zoomControlOptions: {
                    style: naver.maps.ZoomControlStyle.SMALL,
                    position: naver.maps.Position.TOP_RIGHT,
                },
            };

            // 지도를 생성하고 마커를 추가
            var map = new naver.maps.Map('map', mapOptions);

            new naver.maps.Marker({
                position: newCenter,
                map: map,
                title: '검색된 위치',
            });

            map.setCenter(newCenter);
        } else {
            alert('No result');
        }
    });
}

// 주소 수정 버튼 클릭 시 주소 입력창 다시 표시
function closeMap() {
    document.getElementById('map-section').style.display = 'none';
    document.getElementById('mapButton2').style.display = 'none';
    document.getElementById('mapButton').style.display = 'block';
}

// 우편번호 입력 창 생성
function sample6_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            if (data.userSelectedType === 'R') {
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                document.getElementById("sample6_extraAddress").value = extraAddr;

            } else {
                document.getElementById("sample6_extraAddress").value = '';
            }

            document.getElementById('sample6_postcode').value = data.zonecode;
            document.getElementById("sample6_address").value = addr;
            document.getElementById("sample6_detailAddress").focus();
        }
    }).open();
}

function validateTripInfo() {

    const address = document.getElementById('sample6_address')?.value || '';
    const detailedAddress = document.getElementById('sample6_detailAddress')?.value || '';
    const tripName = document.getElementById('tripName')?.value || '';
    const contactTel = document.getElementById('tripTel')?.value || '';
    const contactEmail = getEmail().trim() || '';

    if (address || detailedAddress || tripName || contactTel || contactEmail) {
        // 오류 메시지 초기화
        const errorMessage = document.getElementById('error-message');
        errorMessage.textContent = '';

        if (!address) {
            errorMessage.textContent = '주소를 입력해주세요.';
            return false;
        }
        if (!detailedAddress) {
            errorMessage.textContent = '상세주소를 입력해주세요.';
            return false;
        }
        if (!tripName) {
            errorMessage.textContent = '출장지 이름을 입력해주세요.';
            return false;
        }
        if (!contactTel) {
            errorMessage.textContent = "전화번호를 입력해주세요";
            return false;
        }
        // 전화번호 검사
        const telValidationResult = validatePhoneNumber(contactTel);
        if (telValidationResult) {
            errorMessage.textContent = telValidationResult;
            return false;
        }
        if (contactEmail.startsWith('@') || contactEmail.endsWith('@')) {
            errorMessage.textContent = '이메일을 입력해주세요.';
            return false;
        }
        // 이메일 검사
        const emailValidationResult = validateEmail();
        if (emailValidationResult) {
            errorMessage.textContent = emailValidationResult;
            return false;
        }
    }

    return true;
}

// 전화번호 유효성 검사 함수 추가
function validatePhoneNumber(phoneNumber) {
    const cleaned = phoneNumber.replace(/[^0-9]/g, ''); // 숫자만 남기기

    // 유효한 시작 번호 패턴 (지역번호 또는 휴대전화 번호 패턴)
    const validStartPatterns = /^(010\d{8}|02\d{7,8}|0[3-6][1-5]\d{6,7})$/;

    // 자릿수 확인: 9자리 이상 11자리 이하ㅁ
    const isValidLength = cleaned.length >= 9 && cleaned.length <= 11;
    if (!isValidLength) {
        return "전화번호 자리 수는 9자리 이상 11자리 이하입니다."
    }

    // 시작 번호가 올바른지 확인
    const isValidStart = validStartPatterns.test(cleaned);
    if (!isValidStart) {
        return "지역번호 또는 휴대전화 번호 형태가 아닙니다."
    }

    // 최종 유효성 결과 반환
    return "";
}

// 이메일 유효성 검사 함수
function validateEmail() {
    const emailLocalPart = document.getElementById('emailLocalPart').value;
    const domainInput = document.getElementById('domainInput').value;

    // 이메일 아이디 부분 유효성 검사
    const localPartRegex = /^[a-zA-Z0-9._-]*$/;
    if (!localPartRegex.test(emailLocalPart)) {
        return "이메일 아이디는 영어, 특수문자(. _ -)로 입력해주세요.";
    }

    // 도메인 부분 유효성 검사
    const domainRegex = /^[a-zA-Z0-9.]*$/;
    if (!domainRegex.test(domainInput)) {
        return "이메일 도메인은 'example.com' 형태로 입력해주세요.";
    }

    // 이메일 전체 유효성 검사
    const fullEmail = emailLocalPart + '@' + domainInput;
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(fullEmail)) {
        return "유효한 이메일 형식이 아닙니다.";
    }

    return ""; // 모든 검사를 통과했을 때 빈 문자열 반환
}

function getEmail() {
    const localPart = document.getElementById('emailLocalPart').value;
    const domainPart = document.getElementById('domainSelect').value === 'custom'
        ? document.getElementById('domainInput').value
        : document.getElementById('domainSelect').value;

    return localPart + '@' + domainPart;
}

// 출장 필드를 동적으로 추가하는 함수
function addTripFields() {
    let tripFieldsContainer = document.getElementById('tripTable');

    if (!tripFieldsContainer) {
        // tripTable이 없으면 새롭게 생성
        tripFieldsContainer = document.createElement('table');
        tripFieldsContainer.id = 'tripTable';
        tripFieldsContainer.classList.add('details');

        // 새로운 테이블을 일정 상세 정보 아래에 추가 (적절한 부모 요소 찾아서 append)
        const detailContainer = document.querySelector('.detail-container'); // 적절한 부모 요소 선택
        detailContainer.appendChild(tripFieldsContainer);  // 부모 요소에 추가
    }

    const tripFieldsHTML = `
    <table class="details" id="project-travel-table">
      <tr>
        <td class="section-title">연관된 프로젝트</td>
        <td>
          <select id="projectId" name="projectId">
            <option value="" disabled selected>선택해주세요.</option>
          </select>
        </td>
      </tr>
      <tr>
        <td class="section-title">출장지 주소</td>
        <td>
          <div class="postcode-container">
            <input type="text" id="sample6_postcode" placeholder="우편번호" readonly>
            <button type="button" class="inner-btn blue-btn" onclick="sample6_execDaumPostcode()">우편번호 찾기</button>
          </div>
          <div class="address-container">
            <input type="text" id="sample6_address" placeholder="주소" readonly>
          </div>
          <div class="detail-address-container">
            <input type="text" id="sample6_detailAddress" placeholder="상세주소">
            <input type="text" id="sample6_extraAddress" placeholder="참고항목">
            <div class="flex-center" style="gap: 10px;">
              <button type="button" id="mapButton" class="inner-btn green-btn"
                      onclick="viewMap()">지도 보기</button>
              <button type="button" id="mapButton2" class="inner-btn green-btn"
                      onclick="closeMap()" style="display: none;">지도 닫기</button>
            </div>
          </div>
          <div id="map-section" class="flex-center" style="display: none; margin-top: 10px;">
            <div id="map" style="width:500px; height:400px;"></div>
          </div>
        </td>
      </tr>
      <tr>
        <td class="section-title">출장지 이름</td>
        <td>
          <input id="tripName" type="text"/>
        </td>
      </tr>
      <tr>
        <td class="section-title">전화번호</td>
        <td>
          <input id="tripTel" type="text" class="input-text" oninput="localAutoHyphen(this)" maxlength="13" placeholder="숫자만 입력하세요."/>
        </td>
      </tr>
      <tr>
        <td class="section-title">이메일</td>
        <td>
          <div class="email-container">
            <input id="emailLocalPart" type="text" class="input-text"/>
            <div class="characters">@</div>
            <input id="domainInput" type="text" style="display: none;" disabled>
            <select id="domainSelect">
              <option value="" disabled selected>도메인 선택</option>
              <option value="naver.com">naver.com</option>
              <option value="google.com">google.com</option>
              <option value="hanmail.net">hanmail.net</option>
              <option value="nate.com">nate.com</option>
              <option value="kakao.com">kakao.com</option>
              <option value="custom">직접 입력</option>
            </select>
          </div>
        </td>
      </tr>
      <tr>
        <td class="section-title">참고사항</td>
        <td>
          <input id="note" type="text" class="input-text"/>
        </td>
      </tr>
    </table>
    `

    // 새로운 HTML 추가
    tripFieldsContainer.innerHTML = tripFieldsHTML;
    tripFieldsContainer.style.display = 'table';
    document.querySelector('.addTripButton').style.display = 'none';
    document.querySelector('.closeTripButton').style.display = 'block';
}

function closeTripFields() {
    // 기존에 출장 정보가 있다면 플래그 변수 true
    if (document.getElementById('sample6_address').defaultValue) {
        hadTripInfo = true;
    }
    document.getElementById('tripTable').style.display = 'none';
    document.querySelector('.closeTripButton').style.display = 'none';
    document.querySelector('.addTripButton').style.display= 'block';

    // 출장 정보 필드를 비워서 제출되지 않도록 설정
    document.getElementById('sample6_address').value = '';
    document.getElementById('sample6_detailAddress').value = '';
    document.getElementById('tripName').value = '';
    document.getElementById('tripTel').value = '';
    document.getElementById('emailLocalPart').value = '';
    document.getElementById('domainSelect').value = '';
    document.getElementById('domainInput').value = '';
}

// 프로젝트 및 출장 정보 테이블 표시 여부 토글
function toggleProjectAndTravelTable() {
    const table = document.getElementById('project-travel-table');
    const toggleBtn = document.getElementById('toggleBtn');
    if (table.style.display === 'none' || table.style.display === '') {
        table.style.display = 'table';
        toggleBtn.textContent = '삭제';
    } else { // 테이블 안의 모든 입력 필드를 초기화
        const inputs = table.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.value = '';
        });
        table.style.display = 'none';
        toggleBtn.textContent = '추가';
    }
}