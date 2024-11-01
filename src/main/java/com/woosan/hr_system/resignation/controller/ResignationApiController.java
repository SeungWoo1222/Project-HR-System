package com.woosan.hr_system.resignation.controller;

import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.resignation.model.Resignation;
import com.woosan.hr_system.resignation.service.ResignationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resignation")
public class ResignationApiController {
    @Autowired
    private ResignationService resignationService;

    // 사원 퇴사 처리
    @RequireHRPermission
    @PostMapping(value = "/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> resignEmployee(@PathVariable("employeeId") String employeeId,
                                                 @RequestPart("resignation") Resignation resignation,
                                                 @RequestPart(value = "resignationDocuments", required = false) MultipartFile[] resignationDocuments) {
        // 사원 퇴사 처리
        return ResponseEntity.ok(resignationService.resignEmployee(employeeId, resignation, resignationDocuments));
    }

    // 사원 퇴사 정보 수정
    @RequireHRPermission
    @PutMapping(value = "/{employeeId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateResignationInfo(@PathVariable("employeeId") String employeeId,
                                                        @RequestPart("resignation") Resignation resignation,
                                                        @RequestPart(value = "oldFileIdList", required = false) List<Integer> oldFileIdList,
                                                        @RequestPart(value = "newFile", required = false) MultipartFile[] newFileArr) {
        // 퇴사 정보 수정
        return ResponseEntity.ok(resignationService.updateResignation(employeeId, resignation, oldFileIdList, newFileArr));
    }
}
