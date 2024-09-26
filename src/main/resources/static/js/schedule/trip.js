tripInfo = [];

// 지도 표시 로직
function viewMap() {
    const address = document.getElementById('sample6_address').value;
    const detailAddress = document.getElementById('sample6_detailAddress').value;

    if (!address) {
        alert('주소를 입력하세요.');
        return;
    }

    // 주소 입력창 숨기기, 지도 보여주기
    if (document.getElementById('address-input-section')) {
        document.getElementById('address-input-section').style.display = 'none';
    }
    document.getElementById('map-section').style.display = 'block';

    var fullAddress = address + ' ' + detailAddress;

    // 지도를 페이지 내에 표시
    naver.maps.Service.geocode({ query: fullAddress }, function (status, response) {
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

function viewMapWithData(button) {
    const address = button.getAttribute('data-address');
    const detailAddress = button.getAttribute('data-detail');

    if (!address) {
        alert('주소를 입력하세요.');
        return;
    }

    document.getElementById('map-section').style.display = 'block';

    const fullAddress = address + ' ' + detailAddress;

    // 지도 표시 로직
    naver.maps.Service.geocode({ query: fullAddress }, function (status, response) {
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
function editAddress() {
    document.getElementById('map-section').style.display = 'none';
    document.getElementById('address-input-section').style.display = 'block';
}

// 우편번호 입력 창 생성
function sample6_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("sample6_extraAddress").value = extraAddr;

            } else {
                document.getElementById("sample6_extraAddress").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('sample6_postcode').value = data.zonecode;
            document.getElementById("sample6_address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("sample6_detailAddress").focus();
        }
    }).open();
}

// 출장 정보 저장
function saveTripInfo() {

    // 유효성 검사 통과 후에만 등록 확인
    if (validateTripInfo()) {
        if (confirm('등록하시겠습니까?')) {
            // tripInfo 배열에 저장
            const address = document.getElementById('sample6_address').value;
            const detailedAddress = document.getElementById('sample6_detailAddress').value;
            const clientName = document.getElementById('tripName').value;
            const contactTel = document.getElementById('tripTel').value;
            const contactEmail = document.getElementById('email') ? document.getElementById('email').value : null;
            const note = document.getElementById('note') ? document.getElementById('note').value : null;

            const trip = {
                address: address,
                detailedAddress: detailedAddress,
                clientName: clientName,
                contactTel: contactTel,
                contactEmail: contactEmail || '',
                note: note || ''
            };

            tripInfo.push(trip);

            // trip.name을 넘겨서 출장지 이름만 업데이트
            updateTripList(trip.clientName);

            alert("등록이 완료되었습니다!");
            closeModal();
        }
    }
}

function validateTripInfo() {

    const address = document.getElementById('sample6_address').value;
    const detailedAddress = document.getElementById('sample6_detailAddress').value;
    const clientName = document.getElementById('tripName').value;
    const contactTel = document.getElementById('tripTel').value;
    const contactEmail = document.getElementById('email') ? document.getElementById('email').value : null;
    const note = document.getElementById('note') ? document.getElementById('note').value : null;

    // 이메일 유효성 검사 (@와 .com 포함)
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!address) {
        alert('주소를 입력해주세요.');
        return false;
    }
    if (!detailedAddress) {
        alert('상세주소를 입력해주세요.');
        return false;
    }
    if (!clientName) {
        alert('이름을 입력해주세요.');
        return false;
    }
    if (!contactTel) {
        alert('전화번호를 입력해주세요.');
        return false;
    }

    if (contactEmail && !emailPattern.test(contactEmail)) {
        alert('유효한 이메일 주소를 입력해주세요. (@와 .com을 포함해야 합니다)');
        return false;
    }

    return true;
}

// 출장지 이름을 리스트에 업데이트하는 함수
function updateTripList(name) {
    const tripList = document.getElementById("tripList");
    const li = document.createElement("li");
    li.textContent = name; // 출장지 이름만 표시
    tripList.appendChild(li);
}