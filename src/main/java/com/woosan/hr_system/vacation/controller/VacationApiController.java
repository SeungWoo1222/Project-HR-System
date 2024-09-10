package com.woosan.hr_system.vacation.controller;

import com.woosan.hr_system.vacation.model.Vacation;
import com.woosan.hr_system.vacation.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/vacation")
public class VacationApiController {
    @Autowired
    private VacationService vacationService;

    @PostMapping // 휴가 신청
    public ResponseEntity<String> requestVacation(@ModelAttribute Vacation vacation) {
        return ResponseEntity.ok(vacationService.requestVacation(vacation));
    }

    @PutMapping // 휴가 수정
    public ResponseEntity<String> updateVacation(@ModelAttribute Vacation vacation) {
        return ResponseEntity.ok(vacationService.editVacation(vacation));
    }

    @PatchMapping // 휴가 처리
    public ResponseEntity<String> processVacation(@RequestParam("vacationId") int vacationId,
                                                  @RequestParam("status") String status) {
        return ResponseEntity.ok(vacationService.processVacation(vacationId, status));
    }

}
