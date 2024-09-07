package com.woosan.hr_system.exception;

import com.woosan.hr_system.exception.employee.EmployeeNotFoundException;
import com.woosan.hr_system.exception.employee.NoChangesDetectedException;
import com.woosan.hr_system.exception.employee.PasswordNotFoundException;
import com.woosan.hr_system.exception.employee.ResignationNotFoundException;
import com.woosan.hr_system.exception.file.FileBadRequestException;
import com.woosan.hr_system.exception.file.FileInfoNotFoundException;
import com.woosan.hr_system.exception.file.FileProcessingException;
import com.woosan.hr_system.exception.salary.SalaryNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ade) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/error/403");
        return mav;
    }

    // 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ModelAndView handleAuthenticationException(AuthenticationException ae) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/error/401");
        return mav;
    }

    // 404 Not Found 처리
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ModelAndView handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/error/employee-error");
        return mav;
    }
    @ExceptionHandler(PasswordNotFoundException.class)
    public ResponseEntity<String> handlePasswordNotFoundException(PasswordNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ResignationNotFoundException.class)
    public ResponseEntity<String> handleResignationNotFoundException(ResignationNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileInfoNotFoundException.class)
    public ResponseEntity<String> handleFileInfoNotFoundException(FileInfoNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SalaryNotFoundException.class)
    public ResponseEntity<String> handleSalaryNotFoundException(SalaryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 400 Bad Request 처리
    @ExceptionHandler(NoChangesDetectedException.class)
    public ResponseEntity<String> handleNoChangesDetectedException(NoChangesDetectedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKeyException(DuplicateKeyException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileBadRequestException.class)
    public ResponseEntity<String> handleFileBadRequestException(FileBadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 500 Internal Server Error 처리
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleFileExceptions(FileProcessingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public Object handleAllRuntimeExceptions(RuntimeException ex, HttpServletRequest request) {
        log.error("알 수 없는 오류가 발생했습니다: {}", ex.getMessage(), ex);

        // 요청의 Accept 헤더를 확인하여 API 요청인지 페이지 요청인지 확인
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            // API 요청인 경우 JSON 응답을 반환
            return new ResponseEntity<>("시스템 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            // 페이지 요청인 경우 리디렉션
            return new ModelAndView("redirect:/error/500");
        }
    }
}

