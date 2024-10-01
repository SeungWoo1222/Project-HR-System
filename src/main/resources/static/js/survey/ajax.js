// AJAX POST 요청 - 설문조사 응답 제출
function submitSurveyResponses(event) {
    event.preventDefault();

    // 유효성 검사
    const isValid = validateSurveyResponses();
    let errorMessage = document.getElementById('error-message');
    if (!isValid) {
        errorMessage.textContent = "비어있는 항목이 있습니다. 모든 항목을 입력해주세요.";
        return;
    }
    errorMessage.textContent = '';

    // 입력 필드 정보 수집
    const responses = collectSurveyResponses();

    if (confirm('설문을 제출하시겠습니까?')) {
        fetch('/api/survey/response', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(responses)
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    window.location.href = '/survey/list';
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('응답 등록 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}

// 유효성 검사
function validateSurveyResponses() {
    const questions = document.querySelectorAll('.question');
    let isValid = true;
    let firstInvalidElement = null;

    questions.forEach((question) => {
        const inputElement = question.querySelector('textarea, input[type="text"], input[type="radio"], input[type="checkbox"], input[type="date"], input[type="time"]');
        let isQuestionValid = true;

        if (inputElement) {
            if (inputElement.type === "radio") {
                const selectedOption = question.querySelector('input[type="radio"]:checked');
                if (!selectedOption) {
                    isQuestionValid = false;
                }
            } else if (inputElement.type === "checkbox") {
                const selectedOptions = question.querySelectorAll('input[type="checkbox"]:checked');
                if (selectedOptions.length === 0) {
                    isQuestionValid = false;
                }
            } else if (inputElement.value.trim() === "") {
                isQuestionValid = false;
            }
        }

        if (!isQuestionValid) {
            isValid = false;
            // 첫 번째로 유효하지 않은 필드를 찾으면 해당 필드로 스크롤 이동
            if (!firstInvalidElement) {
                firstInvalidElement = inputElement;
            }
            // 경고 표시
            question.style.border = '1px solid red';
        } else {
            // 유효한 경우 경고 제거
            question.style.border = 'none';
        }
    });

    // 첫 번째로 유효하지 않은 필드로 스크롤 이동
    if (firstInvalidElement) {
        firstInvalidElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    return isValid;
}

// 설문조사 필드에 입력된 데이터 수집하는 함수
function collectSurveyResponses() {
    const surveyId = document.getElementById("surveyId").value;

    const responses = [];
    const questions = document.querySelectorAll('.question');

    questions.forEach((question) => {
        const questionId = question.querySelector('textarea, input').name.split('_')[1];
        let answer = '';

        // 단일 선택형 (radio) 처리
        if (question.querySelector('input[type="radio"]')) {
            const selectedOption = question.querySelector('input[type="radio"]:checked');
            if (selectedOption) {
                answer = selectedOption.value;
            }
        }

        // 다중 선택형 (checkbox) 처리
        else if (question.querySelector('input[type="checkbox"]')) {
            const selectedOptions = question.querySelectorAll('input[type="checkbox"]:checked');
            answer = Array.from(selectedOptions).map(opt => opt.value).join(',');
        }

        // 텍스트형, 장문형, 날짜, 시간 등 처리
        else {
            const inputElement = question.querySelector('textarea, input');
            if (inputElement) {
                answer = inputElement.value;
            }
        }

        // 각 질문에 대한 응답을 배열에 추가
        responses.push({
            surveyId: surveyId,
            questionId: questionId,
            answer: answer
        });
    });

    return responses;
}

// AJAX DELETE 요청 - 설문 삭제
function deleteSurvey(id) {
    if (confirm('설문을 삭제하면 모든 응답이 함께 삭제됩니다.\n이 작업은 되돌릴 수 없습니다.\n정말 삭제하시겠습니까?')) {
        fetch('/api/survey/' + id, {
            method: 'DELETE'
        })
            .then(response => response.text().then(data => ({
                status: response.status,
                text: data
            })))
            .then(response => {
                const errorStatuses = [400, 403, 404, 500];
                if (response.status === 200) {
                    alert(response.text);
                    window.location.href = '/survey/list';
                } else if (errorStatuses.includes(response.status)) {
                    alert(response.text);
                } else {
                    alert('설문 삭제 중 오류가 발생하였습니다.\n재시도 후 여전히 문제가 발생하면 관리자에게 문의해주세요');
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error :', error.message);
                alert('오류가 발생하였습니다.\n관리자에게 문의해주세요.');
            });
    }
}