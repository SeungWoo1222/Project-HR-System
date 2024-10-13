// // 우편번호 입력 창 생성
// function sample6_execDaumPostcode(button) {
//     console.log("edit.js 주소 입력창 호출");
//
//     const parentRow = button.closest('tr');
//     new daum.Postcode({
//         oncomplete: function (data) {
//             var addr = ''; // 주소 변수
//             var extraAddr = ''; // 참고항목 변수
//
//             // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
//             if (data.userSelectedType === 'R') {
//                 addr = data.roadAddress;
//             } else {
//                 addr = data.jibunAddress;
//             }
//
//             // 조합된 참고항목을 해당 필드에 넣는다.
//             if (data.userSelectedType === 'R') {
//                 if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
//                     extraAddr += data.bname;
//                 }
//                 if (data.buildingName !== '' && data.apartment === 'Y') {
//                     extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
//                 }
//                 if (extraAddr !== '') {
//                     extraAddr = ' (' + extraAddr + ')';
//                 }
//                 parentRow.querySelector(".sample6_extraAddress").value = extraAddr;
//             } else {
//                 parentRow.querySelector(".sample6_extraAddress").value = '';
//             }
//
//             // 우편번호와 주소 정보를 해당 필드에 넣는다.
//             parentRow.querySelector('.sample6_postcode').value = data.zonecode;
//             parentRow.querySelector('.sample6_address').value = addr;
//             parentRow.querySelector(".sample6_detailAddress").focus();
//         }
//     }).open();
// }
//
