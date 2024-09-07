package com.woosan.hr_system.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/employee-error")
    public String handleEmployeeNotFound() {
        return "/error/employee-error";
    }

    @RequestMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound() {
        return "/error/404";
    }

    @RequestMapping("/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError() {
        return "/error/500";
    }

    @RequestMapping("/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbidden() {
        return "/error/403";
    }

    @RequestMapping("/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorized() {
        return "/error/401";
    }

    @RequestMapping("/405")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotAllowed() {
        return "/error/405";
    }

    // modal 오류 페이지
    @RequestMapping("/modal/404")
    public String handleNotFoundInModal() {
        return "/error/modal/404";
    }

    @RequestMapping("/modal/500")
    public String handleInternalServerErrorInModal() {
        return "/error/modal/500";
    }

    @RequestMapping("/modal/403")
    public String handleForbiddenInModal() {
        return "/error/modal/403";
    }

    @RequestMapping("/modal/401")
    public String handleUnauthorizedInModal() {
        return "/error/modal/401";
    }
}
