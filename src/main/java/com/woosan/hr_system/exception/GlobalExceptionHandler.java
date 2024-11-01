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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 첫 번째 오류 메시지만 추출
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = errors.stream().findFirst().orElse("유효성 검사 오류가 발생했습니다.");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // 500 Internal Server Error 처리
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleFileExceptions(FileProcessingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public Object handleAllRuntimeExceptions(RuntimeException ex, HttpServletRequest request) {
        log.error("알 수 없는 오류가 발생했습니다: {}", ex.getMessage(), ex);

        String contentType = request.getContentType();
        log.info("contentType : {}", contentType);

        if (contentType == null) {
            return "redirect:/error/500";
        } else {
            return new ResponseEntity<>("시스템 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

