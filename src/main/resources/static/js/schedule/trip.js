// 지도 표시 로직
function viewMap() {
    const address = document.getElementById('sample6_address').value;
    const detailAddress = document.getElementById('sample6_detailAddress').value;

    console.log("address", address);
    console.log("detailAddress", detailAddress);

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

// 주소 수정 버튼 클릭 시 주소 입력창 다시 표시
function editAddress() {
    document.getElementById('map-section').style.display = 'none';
    document.getElementById('address-input-section').style.display = 'block';
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
//
// // 출장 정보 저장
// function saveTripInfo() {
//     // 유효성 검사 통과 후에만 등록 확인
//     if (validateTripInfo()) {
//         if (confirm('등록하시겠습니까?')) {
//             const address = document.getElementById('sample6_address').value;
//             const detailedAddress = document.getElementById('sample6_detailAddress').value;
//             const clientName = document.getElementById('tripName').value;
//             const contactTel = document.getElementById('tripTel').value;
//             const contactEmail = document.getElementById('email') ? document.getElementById('email').value : null;
//             const note = document.getElementById('note') ? document.getElementById('note').value : null;
//
//             console.log("주소:", address);
//             console.log("상세 주소:", detailedAddress);
//             console.log("거래처명:", clientName);
//             console.log("전화번호:", contactTel);
//             console.log("이메일:", contactEmail);
//             console.log("참고사항:", note);
//
//             alert("등록이 완료되었습니다!");
//         }
//     }
// }

function validateTripInfo() {
    const address = document.getElementById('sample6_address')?.value || '';
    const detailedAddress = document.getElementById('sample6_detailAddress')?.value || '';
    const clientName = document.getElementById('tripName')?.value || '';
    const contactTel = document.getElementById('tripTel')?.value || '';
    const contactEmail = document.getElementById('email') ?.value || '';

    if (address || detailedAddress || clientName || contactTel) {
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
            alert('유효한 이메일 주소를 입력해주세요.');
            return false;
        }
    }

    return true;
}
