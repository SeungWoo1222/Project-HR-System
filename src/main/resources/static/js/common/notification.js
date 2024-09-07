// 페이지 로드 시 읽지 않은 메세지 조회
document.addEventListener("DOMContentLoaded", function() {
    fetchUnreadNotificationCount();
});

// 알림 수신함 여닫기
function toggleNotificationBox() {
    var notificationBox = document.getElementById("notification-box");
    var notificationList = document.getElementById("notification-li");

    notificationBox.classList.toggle("open");
    notificationList.classList.toggle("active");

    // 알림 수신함을 열 때 모든 알림 조회
    if (notificationBox.classList.contains("open")) {
        fetchNotifications();
    }
}

// 알림 전체 조회
function fetchNotifications() {
    const notificationContent = document.getElementById('notification-box-content');

    fetch('/notification/all')
        .then(response => response.text())
        .then(html => {
            notificationContent.innerHTML = html;
        })
        .catch(error => console.error('알림을 불러오는 중 오류가 발생했습니다. :', error));
}

// 읽지 않은 알림 개수 조회
function fetchUnreadNotificationCount() {
    fetch('/notification/unread')
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답에 문제가 있습니다.');
            }
            return response.json();
        })
        .then(data => {
            const unreadCount = data.unreadCount; // JSON 객체에서 unreadCount 값 추출
            updateUnreadNotificationCount(unreadCount);
        })
        .catch(error => {
            console.error('알림 개수 가져오기 오류:', error);
        });
}
function updateUnreadNotificationCount(count) {
    const notificationBadge = document.querySelector('.notification-badge');
    if (notificationBadge) {
        notificationBadge.textContent = count;
        notificationBadge.style.display = count > 0 ? 'inline-block' : 'none';
    }
}

// 알림 URL 이동
function openNotificationUrl(url, notificationId, readStatus) {
    // URL로 이동
    window.location.href = url;
    // 알림 읽음 처리
    readNotification(notificationId, readStatus);
}

// 알림 읽음 처리
function readNotification(notificationId, readStatus) {
    // 알림을 이미 읽었다면 실행 중단
    if (readStatus) return;

    fetch('/notification/' + notificationId, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchNotifications(); // 알림 전체 새로고침
            } else {
                alert('알림을 읽음 처리 중 오류가 발생하였습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}

// 모든 알림 읽음 처리
function readAllNotification() {
    // 읽음 처리 전 확인
    let message = '모든 알림을 읽음 처리하시겠습니까?'

    if (!confirm(message)) return;

    // 읽음 처리
    fetch('/notification/all', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchNotifications(); // 알림 전체 새로고침
            } else {
                alert('모든 알림을 읽음 처리 중 오류가 발생하였습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}

// 알림 삭제 처리
function deleteNotification(notificationId) {
    // 삭제 처리 전 확인
    let message = '해당 알림을 삭제 처리하시겠습니까?'

    if (!confirm(message)) return;

    console.log(notificationId)

    fetch('/notification/' + notificationId, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchNotifications(); // 알림 전체 새로고침
            } else {
                alert('알림을 삭제 처리 중 오류가 발생하였습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}

// 모든 알림 삭제 처리
function deleteAllNotification() {
    // 삭제 처리 전 확인
    let message = '모든 알림을 삭제 처리하시겠습니까?'

    if (!confirm(message)) return;

    // 읽음 처리
    fetch('/notification/all', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchNotifications(); // 알림 전체 새로고침
            } else {
                alert('모든 알림을 삭제 처리 중 오류가 발생하였습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
        });
}
