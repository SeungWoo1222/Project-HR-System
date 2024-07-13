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
