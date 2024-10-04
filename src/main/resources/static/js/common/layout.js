document.addEventListener("DOMContentLoaded", function() {
    // 페이지 로드 시 저장된 토글 상태 복원
    const sections = ["attendance-section", "communication-section", "department-section", "management-section"];
    sections.forEach(function(sectionId) {
        var displayState = localStorage.getItem(sectionId);
        if (displayState) {
            document.getElementById(sectionId).style.display = displayState;
        }
    });

    // 로그아웃 확인
    const logoutLink = document.getElementById("logout-link");
    logoutLink.addEventListener("click", function(event) {
        event.preventDefault();
        if (confirm("정말 로그아웃하시겠습니까?")) {
            window.location.href = logoutLink.href;
        }
    });

    // 현재 URL에 따라 활성화된 메뉴 항목 표시
    var currentUrl = window.location.pathname;
    var menuItems = document.querySelectorAll('.sidebar ul li a, .submenu li a');

    menuItems.forEach(function(menuItem) {
        if (menuItem.getAttribute('href') === currentUrl) {
            var parentLi = menuItem.closest('li');
            if (parentLi) {
                parentLi.classList.add('active');
                var parentUl = parentLi.closest('ul');
                if (parentUl.classList.contains('submenu')) {
                    parentUl.style.display = 'block';  // 하위 메뉴 표시
                    parentUl.closest('li').classList.add('open'); // 상위 메뉴 활성화
                } else {
                    parentLi.classList.add('.active');
                }
            }
        }
    });
});

// 사이드바 메뉴 토글 여닫기
function toggleMenu(sectionId) {
    var section = document.getElementById(sectionId);
    if (section.style.display === "block" || section.style.display === "") {
        section.style.display = "none";
    } else {
        section.style.display = "block";
    }
    // 토글 상태를 로컬 저장소에 저장
    localStorage.setItem(sectionId, section.style.display);
}
function toggleSubmenu(sectionId) {
    var section = document.getElementById(sectionId);
    if (section.style.display === "none" || section.style.display === "") {
        section.style.display = "block";

    } else {
        section.style.display = "none";
    }
}

// 페이지 이동 시 로딩 화면 보여주기
window.addEventListener('beforeunload', function() {
    document.getElementById('loader').style.display = 'flex';
});

// 페이지가 완전히 로드된 후 로딩 화면 숨기기
window.addEventListener('load', function() {
    document.getElementById('loader').style.display = 'none';
});

// 모달 열기 함수
function openModal(contentUrl) {
    var modal = document.getElementById("myModal");
    var modalBody = document.getElementById("modal-body");

    // 컨텐츠 로드
    fetch(contentUrl)
        .then(response => {
            if (response.status === 400) {
                response.text().then(errorMessage => {
                    alert(errorMessage);
                });
            }
            if (response.status === 404) {
                return fetch('/error/modal/404').then(res => res.text());
            }
            if (response.status === 401) {
                return fetch('/error/modal/401').then(res => res.text());
            }
            if (response.status === 403) {
                return fetch('/error/modal/403').then(res => res.text());
            }
            if (response.status === 500) {
                return fetch('/error/modal/500').then(res => res.text());
            }
            if (!response.ok) { // 다른 HTTP 오류 처리
                throw new Error('서버 오류 발생: ' + response.status);
            }
            return response.text();
        })
        // .then(response => response.text())
        .then(html => {
            modalBody.innerHTML = html;
            modal.style.display = "flex";

            // 확대 애니메이션을 위해 'show' 클래스 추가
            setTimeout(() => {
                modal.classList.add("show");
            }, 10);


            // 모달 컨텐츠가 로드된 후 포커스 설정
            const firstInput = modalBody.querySelector(".first-input");
            if (firstInput) {
                firstInput.focus();
                console.log('Focus set on:', firstInput);
            } else {
                console.error('First input field not found');
            }
            updateDateTimeElements(); // DateTime T 제거

            // 모달이 열리고 나서 추가 작업 실행
            if (contentUrl.includes('/admin/request/write') || contentUrl.includes('/admin/request/statistic')) {
                initEventListeners();
            }
        });
}

// 모달 닫기 함수
function closeModal() {
    var modal = document.getElementById("myModal");
    modal.classList.remove("show"); // 'show' 클래스 제거하여 축소 애니메이션 적용
    setTimeout(() => {
        modal.style.display = "none";
    }, 300); // 애니메이션이 끝난 후 모달 숨김
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    var modal = document.getElementById("myModal");
    if (event.target === modal) {
        closeModal();
    }
}