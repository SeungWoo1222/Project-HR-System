function togglePassword() {
    const passwordMasked = document.getElementById('password-masked'); // 마스킹된 비밀번호 (*)
    const passwordRaw = document.getElementById('password-raw');       // 실제 비밀번호
    const togglePasswordBtn = document.getElementById('togglePasswordBtn'); // 보이기/숨기기 버튼
    let isVisible = togglePasswordBtn.textContent !== '보기';

    // 버튼 클릭 시 비밀번호 표시/감추기
    if (isVisible) {
        passwordRaw.style.display = 'none';
        passwordMasked.style.display = 'block';
        togglePasswordBtn.textContent = '보기';
    } else {
        passwordRaw.style.display = 'block';
        passwordMasked.style.display = 'none';
        togglePasswordBtn.textContent = '숨기기';
    }
}