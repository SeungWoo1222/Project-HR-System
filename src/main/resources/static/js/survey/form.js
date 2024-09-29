// add-btn을 마지막 질문과 수평으로 배치
function alignAddButtonWithLastQuestion() {
    const lastQuestion = document.querySelector('.survey-container .question-row:last-child');
    const addButton = document.querySelector('.add-btn');

    if (lastQuestion) {
        const questionRect = lastQuestion.getBoundingClientRect();
        const containerRect = document.querySelector('.survey-container').getBoundingClientRect();

        addButton.style.top = (questionRect.top - containerRect.top) + 'px'; // 마지막 질문의 위치에 맞춤
    }
}

// 새로운 질문 추가
function addNewQuestion() {
    const surveyContainer = document.querySelector('.survey-container');
    const questionCount = surveyContainer.querySelectorAll('.question-row').length;

    // 새로운 질문 row 생성
    const newQuestion = document.createElement('div');
    newQuestion.classList.add('question-row');
    newQuestion.innerHTML = `
                <div class="content question">
                    <div class="question-header">
                        <input type="text" id="questionText${questionCount}" name="questionText${questionCount}" placeholder="질문"/>
                        <select id="questionType${questionCount}" name="questionType${questionCount}">
                            <option value="text">단답형</option>
                            <option value="textarea">장문형</option>
                            <option value="radio">단일 선택</option>
                            <option value="checkbox">다중 선택</option>
                            <option value="date">날짜</option>
                            <option value="time">시간</option>
                        </select>
                        <div class="question-remove-btn">
                            <img src="/images/icons/trash.png"/>
                        </div>
                    </div>
                    <div class="options-container" style="display:none;">
                        <div class="option-wrapper">
                            <input type="text" name="option${questionCount}_0" placeholder="옵션 1"/>
                        </div>
                        <div class="option-add-btn">옵션 추가</div>
                    </div>
                </div>
            `;

    // 새로운 질문 추가 후 이벤트 리스너 추가
    surveyContainer.appendChild(newQuestion);

    const newSelect = newQuestion.querySelector(`select[name="questionType${questionCount}"]`);
    newSelect.addEventListener('change', function () {
        toggleOptionsVisibility(this);
    });

    const addOptionBtn = newQuestion.querySelector('.option-add-btn');
    addOptionBtn.addEventListener('click', function () {
        addNewOptionField(this);
    });

    const removeOptionBtn = newQuestion.querySelectorAll('.option-remove-btn');
    removeOptionBtn.forEach(btn => {
        btn.addEventListener('click', function () {
            removeOptionField(this);
        });
    });

    const removeQuestionBtn = newQuestion.querySelector('.question-remove-btn img');
    removeQuestionBtn.addEventListener('click', function () {
        removeQuestion(this);
    });

    alignAddButtonWithLastQuestion();
}

// 질문 유형에 따라 옵션 필드를 표시하거나 숨기기
function toggleOptionsVisibility(selectElement) {
    const questionRow = selectElement.closest('.question-row');
    const optionsContainer = questionRow.querySelector('.options-container');

    if (selectElement.value === 'radio' || selectElement.value === 'checkbox') {
        optionsContainer.style.display = 'block';
    } else {
        optionsContainer.style.display = 'none';
    }
    alignAddButtonWithLastQuestion();
}

// 새로운 옵션 필드를 추가
function addNewOptionField(button) {
    const optionsContainer = button.previousElementSibling;
    const optionCount = optionsContainer.querySelectorAll('input[type="text"]').length;
    const newOption = document.createElement('div');
    newOption.classList.add('option-row');
    newOption.innerHTML = `
                <input type="text" name="option${optionCount}" placeholder="옵션 ${optionCount + 1}" />
                <span class="option-remove-btn"><img src="/images/icons/minus.png"></span>
            `;

    // 삭제 버튼에 이벤트 리스너 추가
    newOption.querySelector('.option-remove-btn').addEventListener('click', function () {
        removeOptionField(this);
    });

    optionsContainer.appendChild(newOption);
    alignAddButtonWithLastQuestion();
}

// 옵션 필드를 삭제
function removeOptionField(button) {
    const optionRow = button.closest('.option-row');
    optionRow.remove();
    alignAddButtonWithLastQuestion();
}

// 질문을 삭제
function removeQuestion(button) {
    const questionRow = button.closest('.question-row');
    questionRow.remove();
    alignAddButtonWithLastQuestion();
}