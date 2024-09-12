package com.woosan.hr_system.vacation.controller;

import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.vacation.model.Vacation;
import com.woosan.hr_system.vacation.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vacation")
public class VacationApiController {
    @Autowired
    private VacationService vacationService;
    @Autowired
    private AuthService authService;

    @PostMapping // 휴가 신청
    public ResponseEntity<String> requestVacation(@ModelAttribute Vacation vacation) {
        return ResponseEntity.ok(vacationService.requestVacation(vacation));
    }

    @PutMapping // 휴가 수정
    public ResponseEntity<Map<String, String>> updateVacation(@ModelAttribute Vacation vacation) {
        String message = vacationService.editVacation(vacation);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", message);
        // 로그인한 사용자와 휴가 신청자의 ID가 같으면 개인 페이지, 다르면 관리자 페이지로 리다이렉트
        if (vacation.getEmployeeId().equals(authService.getAuthenticatedUser().getUsername())) {
            responseBody.put("url", "/vacation/employee");
        } else {
            responseBody.put("url", "/vacation/list");
        }
        return ResponseEntity.ok(responseBody);
    }

    @PatchMapping("/{vacationId}")// 휴가 처리
    public ResponseEntity<String> processVacation(@PathVariable("vacationId") int vacationId,
                                                  @RequestBody Map<String, String> requestBody) {
        return ResponseEntity.ok(vacationService.processVacation(vacationId, requestBody.get("status")));
    }

    @DeleteMapping("/{vacationId}") // 휴가 삭제
    public ResponseEntity<String> deleteVacation(@PathVariable("vacationId") int vacationId) {
        Vacation vacationInfo = vacationService.getVacationById(vacationId);

        if (authService.getAuthenticatedUser().getUsername().equals(vacationInfo.getEmployeeId()) &&
                !vacationInfo.getApprovalStatus().equals("미처리")) {
            throw new IllegalArgumentException("이미 처리된 휴가는 삭제할 수 없습니다.\n휴가 취소가 필요하다면 HR 부서에 문의해주세요.");
        }

        return ResponseEntity.ok(vacationService.deleteVacation(vacationId));
    }

}
