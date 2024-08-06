package com.woosan.hr_system.error;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

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
}

