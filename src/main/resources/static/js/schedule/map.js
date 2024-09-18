document.addEventListener('DOMContentLoaded', function() {
    // 지도의 초기 중심 좌표 및 줌 설정
    var mapOptions = {
        center: new naver.maps.LatLng(37.3595704, 127.105399),
        zoom: 10,
        minZoom: 8,
        maxZoom: 19,
        zoomControl: true,
        zoomControlOptions: {
            style: naver.maps.ZoomControlStyle.SMALL,
            position: naver.maps.Position.TOP_RIGHT,
        },
        mapDataControl: false,
        scaleControl: false
    };

    // 지도 생성
    var map = new naver.maps.Map('map', mapOptions);

    // Geocoder를 사용해 주소로 좌표를 찾고, 지도 중심을 이동하는 함수
    function searchAddressToCoordinate(address) {
        naver.maps.Service.geocode({ query: address }, function(status, response) {
            if (status === naver.maps.Service.Status.ERROR) {
                alert('Geocode Error');
                return;
            }

            if (response.v2.addresses.length > 0) {
                var x = parseFloat(response.v2.addresses[0].x);
                var y = parseFloat(response.v2.addresses[0].y);

                var newCenter = new naver.maps.LatLng(y, x);
                map.setCenter(newCenter); // 지도의 중심 이동

                // 마커 추가
                new naver.maps.Marker({
                    position: newCenter,
                    map: map,
                    title: '검색된 위치'
                });
            } else {
                alert('No result');
            }
        });
    }

    // 검색 버튼 이벤트 핸들러
    document.getElementById('addressForm').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출 방지

        var address = document.getElementById('addressInput').value;
        if (address) {
            searchAddressToCoordinate(address); // 주소 검색 함수 호출
        } else {
            alert('주소를 입력하세요.');
        }
    });
});