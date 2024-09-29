// 지도 표시 로직
function viewMapByEdit(button) {
    const address = button.getAttribute('data-address');
    const detailAddress = button.getAttribute('data-detail');
    const parentSection = button.closest('.detail-container');
    const mapSection = parentSection.querySelector('.map-section');

    console.log("edit.js viewMapWithData호출 address, detailAddress", address, detailAddress);

    if (!address) {
        alert('주소를 입력하세요.');
        return;
    }

    // 주소 입력창 숨기고 지도 보여주기
    if (button.closest('tr').querySelector('.address-input-section')) {
        button.closest('tr').querySelector('.address-input-section').style.display = 'none';
    }
    mapSection.style.display = 'block';

    const fullAddress = address + ' ' + detailAddress;

    // 지도 표시 로직
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

            var map = new naver.maps.Map(mapSection.querySelector('.map'), mapOptions);

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

// 주소 수정 버튼 클릭 시 지도 숨기고 주소 입력창 다시 표시
function editAddressByEdit(button) {
    console.log("edit.js editAddress 호출 button : ", button);

    const parentRow = button.closest('tr');

    console.log("parentRow", parentRow);

    const addressDisplay = parentRow.querySelectorAll('td')[1];
    const detailAddressRow = parentRow.nextElementSibling;  // 다음 <tr>이 상세주소를 포함하고 있는 행이라고 가정


    console.log("addressDisplay", addressDisplay);
    console.log("detailAddressRow", detailAddressRow);

    // 상세주소 행 숨기기
    if (detailAddressRow) {
        detailAddressRow.style.display = 'none';
    }

    // 지도 섹션 숨기기
    if (parentRow.querySelector('.map-section')) {
        parentRow.querySelector('.map-section').style.display = 'none';
    }

    // 기존 주소 칸을 숨기기
    if (addressDisplay) {
        addressDisplay.style.display = 'none';  // 기존 주소 영역 숨기기
    }

    let addressInputRow = parentRow.querySelector('.address-input-row');
    if (!addressInputRow) {
        // 'td' 요소를 생성해서 기존 'tr'에 추가
        let newTd = document.createElement('td');
        newTd.setAttribute('colspan', '2'); // colspan을 2로 설정
        newTd.classList.add('address-input-row'); // 주소 입력을 위한 td 식별 클래스 추가

        const addressValue = addressDisplay ? addressDisplay.innerText.trim() : ''; // 공백 제거
        newTd.innerHTML = `
        <input type="text" class="sample6_postcode" placeholder="우편번호">
        <input type="button" value="우편번호 찾기" onclick="sample6_execDaumPostcode(this)"><br>
        <input id="inputAddress" type="text" class="sample6_address" value="${addressValue}" placeholder="주소"><br>
        <input id="inputDetailedAddress" type="text" class="sample6_detailAddress" placeholder="상세주소"><br>
        <input type="text" class="sample6_extraAddress" placeholder="참고항목"><br>
        <button type="button" onclick="saveUpdatedAddress(this)">저장</button>
    `;

        // 새로운 td를 기존 tr(parentRow)에 추가
        parentRow.appendChild(newTd);
    }
}

// 우편번호 입력 창 생성
function sample6_execDaumPostcode(button) {
    console.log("edit.js 주소 입력창 호출");

    const parentRow = button.closest('tr');
    new daum.Postcode({
        oncomplete: function (data) {
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            // 조합된 참고항목을 해당 필드에 넣는다.
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
                parentRow.querySelector(".sample6_extraAddress").value = extraAddr;
            } else {
                parentRow.querySelector(".sample6_extraAddress").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            parentRow.querySelector('.sample6_postcode').value = data.zonecode;
            parentRow.querySelector('.sample6_address').value = addr;
            parentRow.querySelector(".sample6_detailAddress").focus();
        }
    }).open();
}

// 수정된 주소 저장 로직
function saveUpdatedAddress(button) {
    console.log("주소 저장 로직 실행");
    const parentRow = button.closest('tr');
    const address = parentRow.querySelector('.sample6_address').value;
    const detailedAddress = parentRow.querySelector('.sample6_detailAddress').value;

    // 수정된 주소를 반영
    const addressDisplay = parentRow.previousElementSibling.querySelector('td');  // 주소 표시할 칸
    const detailAddressRow = parentRow.nextElementSibling;  // 상세주소가 들어갈 <tr> 요소 찾기
    const detailAddressTd = detailAddressRow.querySelectorAll('td')[1];  // 상세주소가 들어갈 <td> 요소 찾기

    console.log("detailAddressTd", detailAddressTd);
    console.log("detailAddressRow", detailAddressRow);

    if (addressDisplay && detailAddressTd) {
        addressDisplay.innerText = address;  // 주소 업데이트
        detailAddressTd.innerText = detailedAddress;  // 상세주소 업데이트
        addressDisplay.style.display = 'block';  // 주소 영역 다시 보이기
        detailAddressRow.style.display = 'block';  // 상세주소 영역 다시 보이기
    }

    // 지도 보기 버튼에 새 주소 업데이트
    let viewMapButton = parentRow.previousElementSibling.querySelector('button[onclick^="viewMapByEdit"]');

    if (!viewMapButton) {
        // 버튼이 없으면 새로 생성하고 주소와 같은 td에 추가
        viewMapButton = document.createElement('button');
        viewMapButton.setAttribute('onclick', 'viewMapByEdit(this)');
        viewMapButton.innerText = '지도 보기';

        // viewMapButton을 주소가 있는 td 내부에 추가
        addressDisplay.appendChild(viewMapButton);
    }

    viewMapButton.setAttribute('data-address', address);
    viewMapButton.setAttribute('data-detail', detailedAddress);
    viewMapButton.style.display = 'inline-block';  // 지도 보기 버튼 다시 보이기

    // 주소 입력 폼 감추기
    const addressInputRow = parentRow.querySelector('.address-input-row');
    if (addressInputRow) {
        addressInputRow.style.display = 'none';  // 주소 입력 폼 숨기기
    }

    // 주소 수정 버튼 보이기
    const editButton = parentRow.previousElementSibling.querySelector('button[onclick^="editAddressByEdit"]');
    if (editButton) {
        editButton.style.display = 'inline-block';  // 주소 수정 버튼 다시 보이기
    }

    console.log("editButton", editButton);

    // 새로운 주소를 hidden input에도 반영
    const hiddenAddressField = parentRow.previousElementSibling.querySelector(`[id^="sample6_address_"]`);
    const hiddenDetailAddressField = parentRow.previousElementSibling.querySelector(`[id^="sample6_detailAddress_"]`);

    if (hiddenAddressField && hiddenDetailAddressField) {
        hiddenAddressField.value = address;  // hidden 필드에 업데이트된 주소 반영
        hiddenDetailAddressField.value = detailedAddress;  // hidden 필드에 업데이트된 상세주소 반영
    }

    // 주소와 상세주소 칸 다시 보이기
    addressDisplay.style.display = 'block';
    detailAddressRow.style.display = 'block';

    alert('주소가 성공적으로 저장되었습니다.');
}

// 출장 정보 저장
function saveAllTripInfo() {
    const tripTables = document.querySelectorAll('#tripTable'); // 테이블 요소 선택
    tripInfo = []; // tripInfo 배열 초기화

    if (validateTripInfoByEdit()) {
        tripTables.forEach((table, tableIndex) => {
            // 각 테이블 내의 필드를 모두 배열로 선택
            const addressFields = table.querySelectorAll(`[id^="sample6_address_"]`);
            const detailedAddressFields = table.querySelectorAll(`[id^="sample6_detailAddress_"]`);
            const clientNameFields = table.querySelectorAll(`[id^="tripName_"]`);
            const contactTelFields = table.querySelectorAll(`[id^="tripTel_"]`);
            const contactEmailFields = table.querySelectorAll(`[id^="email_"]`);
            const noteFields = table.querySelectorAll(`[id^="note_"]`);

            addressFields.forEach((addressField, index) => {
                const detailedAddressField = detailedAddressFields[index];
                const clientNameField = clientNameFields[index];
                const contactTelField = contactTelFields[index];
                const contactEmailField = contactEmailFields[index];
                const noteField = noteFields[index];

                // 필드가 제대로 선택되었는지 확인
                console.log(`Table ${tableIndex} (index: ${index}):`, addressField, detailedAddressField, clientNameField, contactTelField);

                // 필드 유효성 검사
                if (!addressField || !detailedAddressField || !clientNameField || !contactTelField) {
                    console.error(`Field not found for index ${index}`);
                    return false;
                }

                // 각 필드의 값을 추출
                const trip = {
                    address: addressField.value,
                    detailedAddress: detailedAddressField.value,
                    clientName: clientNameField.value,
                    contactTel: contactTelField.value,
                    contactEmail: contactEmailField ? contactEmailField.value : '',
                    note: noteField ? noteField.value : ''
                };

                // tripInfo 배열에 추가
                tripInfo.push(trip);
            });
        });
    }

    console.log("모든 tripInfo 저장:", tripInfo);
}

function validateTripInfoByEdit() {
    const tripTables = document.querySelectorAll(`#tripTable`); // 모든 trip 테이블 가져오기
    let isValid = true;

    tripTables.forEach((table, tableIndex) => {
        // 각 테이블 내의 주소 필드를 기반으로 index를 가져옴
        const addressFields = table.querySelectorAll(`[id^="sample6_address_"]`);
        const detailedAddressFields = table.querySelectorAll(`[id^="sample6_detailAddress_"]`);
        const clientNameFields = table.querySelectorAll(`[id^="tripName_"]`);
        const contactTelFields = table.querySelectorAll(`[id^="tripTel_"]`);
        const contactEmailFields = table.querySelectorAll(`[id^="email_"]`);

        addressFields.forEach((addressField, index) => {
            const detailedAddressField = detailedAddressFields[index];
            const clientNameField = clientNameFields[index];
            const contactTelField = contactTelFields[index];
            const contactEmailField = contactEmailFields[index];

            console.log("validateTripInfoByEdit address", addressField.value);
            console.log("validateTripInfoByEdit detailAddress", detailedAddressField.value);
            console.log("validateTripInfoByEdit clientName", clientNameField.value);
            console.log("validateTripInfoByEdit Tel", contactTelField.value);
            console.log("validateTripInfoByEdit Email", contactEmailField ? contactEmailField.value : "없음");

            // 필드가 제대로 있는지 확인
            if (!addressField || !detailedAddressField || !clientNameField || !contactTelField) {
                console.error(`Field not found for index ${index}`);
                isValid = false;
                return false;
            }

            // 유효성 검사: 주소, 상세주소, 이름, 전화번호 필수
            if (!addressField.value) {
                alert(`주소가 없습니다. (index: ${index})`);
                isValid = false;
                return false;
            }
            if (!detailedAddressField.value) {
                alert(`상세주소가 없습니다. (index: ${index})`);
                isValid = false;
                return false;
            }
            if (!clientNameField.value) {
                alert(`거래처 명을 입력해주세요. (index: ${index})`);
                isValid = false;
                return false;
            }
            if (!contactTelField.value) {
                alert(`전화번호를 입력해주세요. (index: ${index})`);
                isValid = false;
                return false;
            }

            // 이메일 유효성 검사
            if (contactEmailField && contactEmailField.value) {
                const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailPattern.test(contactEmailField.value)) {
                    alert(`유효한 이메일을 입력해주세요. (index: ${index})`);
                    isValid = false;
                    return false;
                }
            }
        });
    });

    return isValid;
}
