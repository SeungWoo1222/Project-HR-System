package com.woosan.hr_system.employee.model;

import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.resignation.model.Resignation;
import com.woosan.hr_system.salary.model.Salary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private String employeeId;                  // 사원 ID
    private String name;                        // 이름
    private String birth;                       // 생년월일
    private String residentRegistrationNumber;  // 주민등록번호 뒷자리
    private String phone;                       // 핸드폰 번호
    private String email;                       // 이메일
    private String address;                     // 도로명 주소
    private String detailAddress;               // 상세 주소
    private Department department;              // 부서
    private Position position;                  // 직급
    private LocalDate hireDate;                 // 입사일
    private String status;                      // 재직 상태
    private int remainingLeave;                 // 잔여 연차
    private LocalDateTime lastModified;         // 마지막 수정일시
    private String modifiedBy;                  // 마지막 수정자
    private int picture;                        // 사원 사진 fileID
    private Boolean maritalStatus;              // 결혼 여부
    private int numDependents;                  // 부양 가족 수
    private int numChildren;                    // 8세 이상 20세 이하 자녀 수
    private Password password;                  // 비밀번호 정보
    private Salary salary;                      // 급여 정보
    private Resignation resignation;            // 퇴사 정보

    public void registerNewEmployee(Employee employee, String employeeId) {
        this.employeeId = employeeId;
        this.name = employee.getName();
        this.birth = employee.getBirth();
        this.residentRegistrationNumber = employee.getResidentRegistrationNumber();
        this.phone = employee.getPhone();
        this.email = employee.getEmail();
        this.address = employee.getAddress();
        this.detailAddress = employee.getDetailAddress();
        this.department = employee.getDepartment();
        this.position = employee.getPosition();
        this.hireDate = employee.getHireDate();
        this.status = "재직";
        this.remainingLeave = 11; // 기본 연차 11일 설정
        this.picture = employee.getPicture();
        this.maritalStatus = employee.getMaritalStatus();
        this.numDependents = employee.getNumDependents();
        this.numChildren = employee.getNumChildren();
    }

    // 사원 아이디 생성
    public String createEmployeeId(Employee employee, int numberOfEmployees) {
        // 형식 : AABBCCC (부서 코드, 입사년도, 해당 년도 입사 순서)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String BB = employee.getHireDate().format(formatter).substring(2, 4);
        String CCC = String.format("%03d", numberOfEmployees + 1);

        return employee.getDepartment() + BB + CCC;
    }

    // 사진 FileId 할당
    public void assignPicture(int fileId) {
        if (fileId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 파일 ID입니다.");
        }
        this.picture = fileId;
    }
}
