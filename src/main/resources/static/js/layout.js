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