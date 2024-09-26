// 지도 표시 로직
function viewMapByEdit(button) {
    const address = button.getAttribute('data-address');
    const detailAddress = button.getAttribute('data-detail');
    const mapSection = button.closest('tr').querySelector('.map-section');

    console.log("edit.js viewMapWithData호출", address, detailAddress);

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
    console.log("edit.js editAddress 호출");

    // 'detail-container' 안에서 관련된 테이블을 찾기
    const parentSection = button.closest('.detail-container');
    const table = parentSection.querySelector('table');  // 테이블 찾기
    const parentRow = table.querySelector('tr');  // 테이블 내의 첫 번째 tr 찾기

    if (!parentRow) {
        console.error("tr 요소를 찾을 수 없습니다.");
        return;
    }

    const addressDisplay = parentRow.querySelector('td[data-address]');
    const detailAddressRow = parentRow.querySelector('td[data-detail]');

    // 상세주소 행 숨기기
    if (detailAddressRow) {
        detailAddressRow.style.display = 'none';
    }

    // 지도 섹션 숨기기
    const mapSection = parentRow.querySelector('.map-section');
    if (mapSection) {
        mapSection.style.display = 'none';
    }

    // 주소 입력 섹션 표시
    let addressInputSection = parentRow.querySelector('.address-input-section');
    if (!addressInputSection) {
        addressInputSection = document.createElement('div');
        addressInputSection.classList.add('address-input-section');

        addressInputSection.innerHTML = `
                <input type="text" class="sample6_postcode" placeholder="우편번호">
                <input type="button" value="우편번호 찾기" onclick="sample6_execDaumPostcode(this)"><br>
                <input type="text" class="sample6_address" value="${addressDisplay ? addressDisplay.textContent : ''}" placeholder="주소"><br>
                <input type="text" class="sample6_detailAddress" placeholder="상세주소"><br>
                <input type="text" class="sample6_extraAddress" placeholder="참고항목"><br>
                <button type="button" onclick="saveUpdatedAddress(this)">저장</button>
            `;

        parentRow.appendChild(addressInputSection);
    }

    addressInputSection.style.display = 'block';

    if (addressDisplay) {
        addressDisplay.style.display = 'none';
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
    const parentRow = button.closest('tr');
    const address = parentRow.querySelector('.sample6_address').value;
    const detailedAddress = parentRow.querySelector('.sample6_detailAddress').value;

    // 수정된 주소를 반영
    parentRow.querySelector('td[data-address]').innerText = address;
    parentRow.querySelector('td[data-detail-address]').innerText = detailedAddress;

    alert('주소가 성공적으로 저장되었습니다.');

    // 주소 입력창 숨기고 지도를 다시 표시
    parentRow.querySelector('.address-input-section').style.display = 'none';
    parentRow.querySelector('.map-section').style.display = 'block';
}
