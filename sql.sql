-- Departments(부서) Table
CREATE TABLE `departments` (
	`department_id` VARCHAR(2) NOT NULL COMMENT 'PK',
	`department_name` ENUM('생산', '품질관리', '영업', '마케팅', '재무', '인사', '연구개발') NOT NULL,
    PRIMARY KEY (`department_id`)
);
INSERT INTO departments (department_id, department_name) VALUES
('PR', '생산'),
('QC', '품질관리'),
('SA', '영업'),
('MK', '마케팅'),
('FI', '재무'),
('HR', '인사'),
('RD', '연구개발');

-- Positions(직급) Table
CREATE TABLE `positions` (
    `position_id` VARCHAR(2) NOT NULL COMMENT 'PK, 직급명',
    `position_rank` INTEGER NOT NULL COMMENT '직급 순위',
    PRIMARY KEY (`position_id`)
);
INSERT INTO positions (position_id, position_rank) VALUES
('사원', 1),
('대리', 2),
('과장', 3),
('차장', 4),
('부장', 5),
('사장', 6);

-- Files(파일) Table
CREATE TABLE `files` (
    `file_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `original_file_name` VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    `stored_file_name` VARCHAR(255) NOT NULL COMMENT '바뀐 파일명',
    `file_size` BIGINT NOT NULL COMMENT '파일 크기 (바이트 단위)',
    `uploaded_by` VARCHAR(20) NOT NULL COMMENT '업로드 사원',
    `uploaded_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '업로드 일시',
    `file_id_usage` VARCHAR(100) NOT NULL COMMENT '사용처',
    PRIMARY KEY (`file_id`)
);

INSERT INTO files VALUES (
	'1', 'man.png', '1725268442761.man.png', '180160', 'MASTER', '2024-09-02 18:14:03', 'employee'
);

-- Employees(사원 정보) Table
CREATE TABLE `employees` (
	`employee_id` VARCHAR(10) NOT NULL COMMENT 'PK, 형식 AABBCCC(AA: 부서코드, BB: 입사년도, CCC: 해당년도 입사순서)',
	`name` VARCHAR(50) NOT NULL,
	`birth` VARCHAR(6) NOT NULL COMMENT '생년월일 6자리',
	`resident_registration_number` VARCHAR(7) NOT NULL COMMENT '주민번호 뒷자리 7자리',
	`phone` VARCHAR(20) NOT NULL,
	`email` VARCHAR(100) NULL,
	`address` VARCHAR(255) NOT NULL COMMENT '도로명 주소',
	`detail_address` VARCHAR(255) NOT NULL COMMENT '상세 주소',
	`department_id` VARCHAR(2) NOT NULL COMMENT 'FK, departments 테이블 참조',
	`position_id` VARCHAR(2) NOT NULL COMMENT 'FK, positions 테이블 참조',
	`hire_date` DATE NOT NULL COMMENT '입사날짜',
	`status` ENUM('재직', '휴직', '퇴사 예정', '퇴사') NOT NULL DEFAULT '재직' COMMENT '재직 상태',
	`picture` INTEGER NULL COMMENT '사원 사진 파일 ID',
	`remaining_leave` DECIMAL(3,1) NOT NULL COMMENT '잔여 연차',
	`marital_status` BOOLEAN NOT NULL COMMENT '결혼 상태',
	`num_dependents` INTEGER NOT NULL COMMENT '부양 가족 수',
	`num_children` INTEGER NULL COMMENT '자녀 수',
	`modified_by` VARCHAR(20) NULL COMMENT '마지막 수정자',
	`last_modified` DATETIME NULL COMMENT '마지막 수정일시',
	PRIMARY KEY (`employee_id`),
	FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`),
	FOREIGN KEY (`position_id`) REFERENCES `positions` (`position_id`),
	FOREIGN KEY (`picture`) REFERENCES `files`(`file_id`) ON DELETE SET NULL
);

-- Passwords(비밀번호) Table
CREATE TABLE `passwords` (
    `employee_id` VARCHAR(10) NOT NULL COMMENT 'PK, FK, employees 테이블 참조',
    `password` VARCHAR(255) NOT NULL COMMENT '비밀번호',
    `password_count` TINYINT UNSIGNED DEFAULT 0 COMMENT '비밀번호 입력 시도 횟수',
    `strength` TINYINT UNSIGNED NULL COMMENT '비밀번호 강도 (0~4)',
    `modified_by` VARCHAR(20) NULL COMMENT '마지막 수정자',
    `last_modified` DATETIME NULL COMMENT '마지막 수정일시',
    PRIMARY KEY (`employee_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE
);

-- Resignations(퇴사 정보) Table
CREATE TABLE `resignations` (
  `employee_id` VARCHAR(10) NOT NULL COMMENT 'PK, employees 테이블 참조',
  `resignation_date` DATE NOT NULL COMMENT '퇴사날짜',
  `resignation_reason` VARCHAR(100) NOT NULL COMMENT '퇴사 사유',
  `code_number` VARCHAR(100) NOT NULL COMMENT '퇴사 코드',
  `specific_reason` VARCHAR(100) NOT NULL COMMENT '구체적인 사유',
  `processed_by` VARCHAR(20) NOT NULL COMMENT '처리 사원',
  `processed_at` DATETIME NOT NULL COMMENT '처리 일시',
  PRIMARY KEY (`employee_id`),
	FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE
);

-- Resignation And File Join Table
CREATE TABLE `resignation_files` (
    `resignation_id` VARCHAR(10) NOT NULL COMMENT 'FK, resignations 테이블 참조',
    `file_id` INTEGER NOT NULL COMMENT 'FK, files 테이블 참조',
    PRIMARY KEY (`resignation_id`, `file_id`),
    FOREIGN KEY (`resignation_id`) REFERENCES `resignations`(`employee_id`) ON DELETE CASCADE,
		FOREIGN KEY (`file_id`) REFERENCES `files`(`file_id`) ON DELETE CASCADE
);

-- Salaries(급여 정보) Table
CREATE TABLE `salaries` (
    `salary_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `employee_id` VARCHAR(10) NOT NULL COMMENT 'FK, employees 테이블 참조',
    `name` VARCHAR(100) NOT NULL,
    `department_id` VARCHAR(2) NOT NULL COMMENT 'FK, departments 테이블 참조',
    `position_id` VARCHAR(2) NOT NULL COMMENT 'FK, positions 테이블 참조',
    `bank` VARCHAR(10) NOT NULL COMMENT '은행명',
    `account_number` VARCHAR(50) NOT NULL COMMENT '계좌번호',
    `annual_salary` INTEGER NOT NULL COMMENT '연봉',
    `created_at` DATE DEFAULT (CURRENT_DATE) COMMENT '등록된 날짜',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용 여부 (1: 사용, 0: 미사용)',
    PRIMARY KEY (`salary_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `employees`(`employee_id`) ON DELETE CASCADE,
    FOREIGN KEY (`department_id`) REFERENCES `departments`(`department_id`),
    FOREIGN KEY (`position_id`) REFERENCES `positions`(`position_id`)
);

-- Salary Payments(급여지급내역) Table
CREATE TABLE `salary_payments` (
    `payment_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `salary_id` INTEGER COMMENT 'FK, salaries 테이블 참조',
    `compensation_month` CHAR(7) NOT NULL COMMENT '해당 급여 년월 (YYYY-MM)',
    `payment_date` DATE NOT NULL COMMENT '급여 지급일',
    `gross_salary` INTEGER NOT NULL COMMENT '총 급여',
    `base_salary` INTEGER NOT NULL COMMENT '기본급',
    `overtime_pay` INTEGER NULL COMMENT '연장 근무 수당',
    `position_allowance` INTEGER NULL COMMENT '직책 수당',
    `meal_allowance` INTEGER NULL COMMENT '식대 (비과세)',
    `transport_allowance` INTEGER NULL COMMENT '교통비 (비과세)',
    `personal_bonus` INTEGER NULL COMMENT '개인 성과급',
    `team_bonus` INTEGER NULL COMMENT '팀 성과급',
    `holiday_bonus` INTEGER NULL COMMENT '명절 보너스',
    `year_end_bonus` INTEGER NULL COMMENT '연말 보너스',
    `income_tax` INTEGER NOT NULL COMMENT '근로소득세',
    `local_income_tax` INTEGER NOT NULL COMMENT '지방소득세',
    `national_pension` INTEGER NOT NULL COMMENT '국민연금',
    `health_insurance` INTEGER NOT NULL COMMENT '건강보험',
    `long_term_care_insurance` INTEGER NOT NULL COMMENT '장기요양보험',
    `employment_insurance` INTEGER NOT NULL COMMENT '고용보험',
    `deductions` INTEGER NOT NULL COMMENT '총 공제 금액',
    `net_salary` INTEGER NOT NULL COMMENT '실 지급액',
    `days` INTEGER NOT NULL COMMENT '근로일수',
    `total_time` DECIMAL(5,2) NOT NULL COMMENT '총 근로시간',   
    `total_overtime` DECIMAL(4,2) NOT NULL COMMENT '총 초과근로시간',
    `total_night_time` DECIMAL(4,2) NOT NULL COMMENT '총 야간근로시간',
    `remarks` VARCHAR(255) NULL COMMENT '비고',
    PRIMARY KEY (`payment_id`),
    FOREIGN KEY (`salary_id`) REFERENCES `salaries`(`salary_id`) ON DELETE SET NULL
);

-- Payroll Ratios(급여 비율) Table
CREATE TABLE payroll_ratios (
    base_salary_ratio DOUBLE NOT NULL COMMENT '기본급 비율',
    position_allowance_ratio DOUBLE NOT NULL COMMENT '직책 수당 비율',
    meal_allowance_ratio DOUBLE NOT NULL COMMENT '식대 비율',
    transport_allowance_ratio DOUBLE NOT NULL COMMENT '교통비 비율',
    personal_bonus_ratio DOUBLE NOT NULL COMMENT '개인 성과급 비율',
    team_bonus_ratio DOUBLE NOT NULL COMMENT '팀 성과급 비율',
    holiday_bonus_ratio DOUBLE NOT NULL COMMENT '명절 보너스 비율',
    year_end_bonus_ratio DOUBLE NOT NULL COMMENT '연말 보너스 비율'
);
INSERT INTO payroll_ratios (
    base_salary_ratio,
    position_allowance_ratio,
    meal_allowance_ratio,
    transport_allowance_ratio,
    personal_bonus_ratio,
    team_bonus_ratio,
    holiday_bonus_ratio,
    year_end_bonus_ratio
) VALUES (
    70.0, 10.0, 5.0, 5.0, 2.5, 2.5, 2.5, 2.5
);

-- Deduction Ratios(공제 비율) Table
CREATE TABLE deduction_ratios (
    local_income_tax_rate DOUBLE NOT NULL COMMENT '지방소득세율 (소득세의 10%)',
    national_pension_rate DOUBLE NOT NULL COMMENT '국민연금 비율 (4.5%)',
    health_insurance_rate DOUBLE NOT NULL COMMENT '건강보험 비율 (3.545%)',
    long_term_care_rate DOUBLE NOT NULL COMMENT '장기요양보험 비율 (0.4591)',
    employment_insurance_rate DOUBLE NOT NULL COMMENT '고용보험 비율 (0.9%)'
);
INSERT INTO deduction_ratios (
    local_income_tax_rate,
    national_pension_rate,
    health_insurance_rate,
    long_term_care_rate,
    employment_insurance_rate
) VALUES (
    10.0, 4.5, 3.545, 0.4591, 0.9
);

-- Notifications(알림) Table
CREATE TABLE notifications (
    notification_id INTEGER AUTO_INCREMENT,
    employee_id VARCHAR(10) NOT NULL COMMENT 'FK, employees 테이블 참조',
    message VARCHAR(255) NOT NULL COMMENT '알림 메시지',
    url VARCHAR(255) NULL COMMENT '연결된 URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    read_status BOOLEAN DEFAULT FALSE COMMENT '읽음 여부',
    read_at DATETIME NULL COMMENT '읽은 시간',
    PRIMARY KEY (`notification_id`),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

-- Vacation(휴가) Table
CREATE TABLE `vacation` (
	`vacation_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`employee_id` VARCHAR(10) NOT NULL COMMENT 'FK, employees 테이블 참조',
	`start_at` DATETIME NOT NULL,
	`end_at` DATETIME NOT NULL,
	`vacation_type` ENUM('연차', '병가', '출산 휴가', '배우자 출산 휴가', '생리 휴가', '가족 돌봄 휴가', '경조사 휴가', '기타 휴가') NOT NULL DEFAULT '연차',
	`reason` VARCHAR(255) NOT NULL,
	`used_days` DECIMAL(3,1) NULL,
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '휴가 신청 일시',
	`approval_status` ENUM('미처리', '승인', '거절') NOT NULL DEFAULT '미처리',
	`processing_by` VARCHAR(20) NULL,
	`processing_at` DATETIME NULL,
  PRIMARY KEY (`vacation_id`),
	FOREIGN KEY (`employee_id`) REFERENCES employees(`employee_id`) ON DELETE CASCADE
);

-- Overtime(초과근무) Table
CREATE TABLE `overtime` (
    `overtime_id` INT AUTO_INCREMENT COMMENT 'PK',
    `employee_id` VARCHAR(10) NOT NULL COMMENT 'FK',
    `date` DATE NOT NULL COMMENT '날짜',
    `start_time` TIME NOT NULL COMMENT '시작 시간',
    `end_time` TIME NOT NULL COMMENT '종료 시간',
    `night_hours` DECIMAL(3,2) DEFAULT 0.00 COMMENT '야간근무 시간',
    `total_hours` DECIMAL(5,2) NOT NULL COMMENT '총 초과근무 시간',
     PRIMARY KEY (`overtime_id`),
     FOREIGN KEY (`employee_id`) REFERENCES employees(`employee_id`) ON DELETE CASCADE
);

-- Holidays(공휴일) Table
CREATE TABLE holidays (
    holiday_id INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
    date_name VARCHAR(50) NOT NULL COMMENT '공휴일 이름',
    loc_date DATE NOT NULL COMMENT '공휴일 날짜',
    PRIMARY KEY (`holiday_id`)
);

-- Reports(보고서) Table
CREATE TABLE `reports` (
	`report_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`writer_id` VARCHAR(10) NOT NULL,
	`approver_id` VARCHAR(10) NOT NULL,
	`approver_name` VARCHAR(50) NOT NULL,
	`title` VARCHAR(255) NOT NULL,
	`content` TEXT NOT NULL,
	`created_date` TIMESTAMP NOT NULL,
	`modified_date` TIMESTAMP NULL,
	`status` ENUM('미처리', '승인', '거절') NOT NULL DEFAULT '미처리',
	`reject_reason` VARCHAR(255) NULL,
	`complete_date` DATE NOT NULL,
   PRIMARY KEY (`report_id`)
);

-- Report Requests(보고서 요청) Table
CREATE TABLE `report_requests` (
	`request_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`report_id` INTEGER NULL COMMENT 'FK, reports 테이블 참조',
	`requester_id` VARCHAR(10) NOT NULL COMMENT 'FK, employees 테이블 참조',
	`writer_id` VARCHAR(10) NOT NULL,
	`writer_name` VARCHAR(50) NOT NULL,
	`request_date` TIMESTAMP NOT NULL,
	`modified_date` TIMESTAMP NULL,
	`due_date` DATE NOT NULL,
	`request_note` TEXT NOT NULL,
   PRIMARY KEY (`request_id`),
   FOREIGN KEY (`report_id`) REFERENCES `reports`(`report_id`) ON DELETE CASCADE,
   FOREIGN KEY (`requester_id`) REFERENCES `employees`(`employee_id`) ON DELETE CASCADE
);

-- Reports And Files Join Table
CREATE TABLE `report_files` (
	`file_id` INTEGER NOT NULL,
	`report_id` INTEGER NOT NULL,
	PRIMARY KEY (`report_id`, `file_id`),
  FOREIGN KEY (`report_id`) REFERENCES `reports`(`report_id`) ON DELETE CASCADE,
  FOREIGN KEY (`file_id`) REFERENCES `files`(`file_id`) ON DELETE CASCADE
);

-- Report Files Archive(보고서 파일 아카이브) Table
CREATE TABLE report_file_archive (
    archive_id INT PRIMARY KEY AUTO_INCREMENT,  -- 아카이브 테이블의 PK로 사용, 자동 증가
    file_id INT NOT NULL,                       -- 파일 ID
    report_id INT NOT NULL,                     -- 보고서 ID
    deleted_date TIMESTAMP ,                      -- 삭제된 날짜
    original_file_name VARCHAR(255),            -- 원본 파일 이름
    stored_file_name VARCHAR(255),              -- 저장된 파일 이름
    file_size BIGINT,                           -- 파일 크기
    upload_date TIMESTAMP ,                       -- 업로드 날짜
    uploaded_by VARCHAR(10),                   -- 업로드한 사용자
    file_id_usage VARCHAR(10)                   -- 파일 ID 사용 용도
);
	
-- Shared Trash(보고서 요청 아카이브) Table
CREATE TABLE `shared_trash` (
    `id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK, 아카이브 ID',
    `original_table` VARCHAR(50) NOT NULL COMMENT '오리지널 테이블',
    `original_id` INTEGER NOT NULL COMMENT '오리지널 ID',
    `deleted_date` TIMESTAMP NOT NULL COMMENT '삭제 날짜',
    `created_date` TIMESTAMP NULL COMMENT '생성 날짜 (created_date, upload_date, request_date)',
    `deleted_by` VARCHAR(50) NOT NULL COMMENT '삭제한 사람 (writer_id, requester_id)',
    `content` TEXT NULL COMMENT '내용 (reports.content, report_requests.request_note)',
    `approver_writer_id` VARCHAR(10) NULL COMMENT '요청 작성자 ID (approval_id, writer_id)',
    `approver_writer_name` VARCHAR(10) NULL COMMENT '요청 작성자 이름 (approval_name, writer_name)',
    `completion_due_date` DATE NULL COMMENT '처리, 마감 날짜 (complete_date, due_date)',
    `linked_report_id` INTEGER NULL COMMENT '연결된 보고서 ID',
    `title` VARCHAR(255) NULL COMMENT '제목',
    `status` ENUM('미처리', '승인', '거절') NULL COMMENT '상태',
    `reject_reason` TEXT NULL COMMENT '거절 사유',
    `modified_date` TIMESTAMP NULL COMMENT '수정 날짜',
    PRIMARY KEY (`id`)
);

-- Schedules(일정) Table
CREATE TABLE `schedules` (
    `task_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK, 일정ID',
    `member_id` VARCHAR(10) NOT NULL COMMENT '멤버ID',
    `task_name` VARCHAR(50) NOT NULL COMMENT '작업 이름',
    `content` VARCHAR(255) NOT NULL COMMENT '내용',
    `start_time` DATETIME NOT NULL COMMENT '시작 시간',
    `end_time` DATETIME NOT NULL COMMENT '종료 시간',
    `all_day` TINYINT(1) DEFAULT '0' COMMENT '종일 여부',
    `status` ENUM('미완료', '진행중', '완료') NOT NULL COMMENT '상태',
    `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 날짜',
    `project_id` INT COMMENT '프로젝트 ID',
    `color` VARCHAR(20) COMMENT '색상',
    FOREIGN KEY (`member_id`) REFERENCES `employees`(`employee_id`) ON DELETE CASCADE
);

-- Schedule Archive(일정 아카이브) Table
CREATE TABLE `schedule_archive` (
    `archive_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK, 아카이브ID',
    `task_id` INT NOT NULL COMMENT '일정ID FK schedules테이블 참조',
    `member_id` VARCHAR(10) NOT NULL COMMENT '멤버ID FK employees테이블 참조',
    `task_name` VARCHAR(50) NOT NULL COMMENT '작업 이름',
    `content` VARCHAR(255) NOT NULL COMMENT '내용',
    `start_time` DATETIME NOT NULL COMMENT '시작 시간',
    `end_time` DATETIME NOT NULL COMMENT '종료 시간',
    `all_day` TINYINT(1) DEFAULT '0' COMMENT '종일 여부',
    `status` ENUM('미완료', '진행중', '완료') NOT NULL COMMENT '상태',
    `created_date` TIMESTAMP NULL COMMENT '생성 날짜',
    `project_id` INT COMMENT '프로젝트 ID',
    `color` VARCHAR(20) NULL COMMENT '색상'
);

-- Business Trips Table
CREATE TABLE `business_trips` (
    `trip_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK, 출장ID',
    `task_id` INT NOT NULL COMMENT 'task 아이디 FK schedules테이블 참조',
    `address` VARCHAR(255) NOT NULL COMMENT '주소',
    `detailed_address` VARCHAR(255) NOT NULL COMMENT '상세주소',
    `created_date` TIMESTAMP NOT NULL COMMENT '생성 날짜',
    `status` ENUM('방문완료', '미방문') NOT NULL COMMENT '방문 상태',
    `trip_name` VARCHAR(100) NOT NULL COMMENT '출장 이름',
    `contact_tel` VARCHAR(20) NOT NULL COMMENT '출장지 연락처',
    `contact_email` VARCHAR(30) NOT NULL COMMENT '출장지 이메일',
    `note` VARCHAR(255) NULL COMMENT '참고 사항',
    FOREIGN KEY (`task_id`) REFERENCES `schedules`(`task_id`) ON DELETE CASCADE
);

-- Business Trip Archive Table
CREATE TABLE `business_trip_archive` (
	`archive_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK, 아카이브ID',
	`trip_id` INT NOT NULL COMMENT 'trip 아이디',
	`task_id` INT NOT NULL COMMENT 'task 아이디 FK schedules테이블 참조',
	`address` VARCHAR(100) NOT NULL COMMENT '주소',
	`detailed_address` VARCHAR(100) NOT NULL COMMENT '상세주소',
	`created_date` TIMESTAMP NOT NULL COMMENT '생성 날짜',
	`status` ENUM('방문완료', '미방문') NOT NULL COMMENT '방문 상태',
	`trip_name` VARCHAR(100) NOT NULL COMMENT '출장 이름',
	`contact_tel` VARCHAR(20) NOT NULL COMMENT '출장지 연락처',
	`contact_email` VARCHAR(30) NOT NULL COMMENT '출장지 이메일',
	`note` VARCHAR(255) COMMENT '참고 사항'
);

-- Attendance(근태) Table
CREATE TABLE `attendance` (
	`attendance_id` INTEGER NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`employee_id` VARCHAR(10) NOT NULL COMMENT 'FK, employees 테이블 참조',
	`date` DATE NOT NULL COMMENT '근무 날짜',
	`check_in` TIME NOT NULL COMMENT '출근 시간',
	`check_out` TIME NULL COMMENT '퇴근 시간',
	`working_hours` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '정규 근무시간',
	`status` ENUM('출근', '결근', '지각', '조퇴', '출장', '휴가') NOT NULL DEFAULT '출근' COMMENT '근태 상태',
	`overtime_id` INTEGER NULL COMMENT 'FK, overtime 테이블 참조',
	`vacation_id` INTEGER NULL COMMENT 'FK, vacation 테이블 참조',
	`trip_id` INTEGER NULL COMMENT 'FK, trip 테이블 참조',
	`notes` VARCHAR(255) NULL COMMENT '메모',
	`last_modified` DATETIME NULL COMMENT '마지막 수정일시',
	`modified_by` VARCHAR(20) NULL COMMENT '마지막 수정자',
  PRIMARY KEY (`attendance_id`),
  FOREIGN KEY (`employee_id`) REFERENCES employees(`employee_id`) ON DELETE CASCADE,
  FOREIGN KEY (`overtime_id`) REFERENCES overtime(`overtime_id`) ON DELETE SET NULL,
  FOREIGN KEY (`vacation_id`) REFERENCES vacation(`vacation_id`) ON DELETE SET NULL,
  FOREIGN KEY (`trip_id`) REFERENCES business_trips(`trip_id`) ON DELETE SET NULL
);

-- Surveys(설문조사 정보) Table
CREATE TABLE surveys (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `created_by` VARCHAR(20) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `status` ENUM('조사 중', '조사 종료') DEFAULT '조사 중',
    `expires_at` DATETIME NOT NULL
);

-- Questions(설문조사 질문 정보) Table
CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    survey_id INT NOT NULL,
    question_text VARCHAR(255) NOT NULL,
    question_type ENUM('text', 'textarea', 'radio', 'checkbox', 'date', 'time') NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
);

-- Question_options(설문조사 질문 옵션) Table
CREATE TABLE question_options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Responses(설문조사 응답) Table
CREATE TABLE responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    survey_id INT NOT NULL,
    employee_id VARCHAR(10) NOT NULL,
    question_id INT NOT NULL,
    answer TEXT,
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Survey Participants(설문조사 참여자) Table
CREATE TABLE survey_participants (
    survey_id INT NOT NULL,
    employee_id VARCHAR(10) NOT NULL,
    participation_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (survey_id, employee_id),
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
);

-- Income Tax(간이세액표) Table
CREATE TABLE income_tax (
    salary_min INT NOT NULL,
    salary_max INT NOT NULL,
    tax_amount_for_1_dependent INT NOT NULL,    
    tax_amount_for_2_dependent INT NOT NULL,    
    tax_amount_for_3_dependent INT NOT NULL,    
    tax_amount_for_4_dependent INT NOT NULL,    
    tax_amount_for_5_dependent INT NOT NULL,    
    tax_amount_for_6_dependent INT NOT NULL,    
    tax_amount_for_7_dependent INT NOT NULL,    
    tax_amount_for_8_dependent INT NOT NULL,    
    tax_amount_for_9_dependent INT NOT NULL,    
    tax_amount_for_10_dependent INT NOT NULL,    
    tax_amount_for_11_dependent INT NOT NULL
);