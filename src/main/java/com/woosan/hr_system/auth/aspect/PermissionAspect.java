package com.woosan.hr_system.auth.aspect;

import com.woosan.hr_system.auth.service.AuthService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect { // 권한 관리를 위한 AOP Aspect
    @Autowired
    private AuthService authService;

    @Before("@annotation(com.woosan.hr_system.auth.aspect.RequireHRPermission)")
    public void verifyHRPermissions() {
        authService.verifyDepartment("HR");
    }
}
