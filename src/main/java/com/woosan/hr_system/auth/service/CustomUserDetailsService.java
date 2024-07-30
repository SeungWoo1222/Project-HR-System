package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.CustomUserDetails;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.dao.ResignationDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeDAO employeeDAO;
    private final PasswordDAO passwordDAO;
    private final ResignationDAO resignationDAO;

    @Autowired
    public CustomUserDetailsService(EmployeeDAO employeeDAO, PasswordDAO passwordDAO, ResignationDAO resignationDAO) {
        this.employeeDAO = employeeDAO;
        this.passwordDAO = passwordDAO;
        this.resignationDAO = resignationDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeDAO.getEmployeeById(username);
        Password password = passwordDAO.selectPassword(username);

        if (employee == null) { throw new UsernameNotFoundException("해당 사원을 찾을 수 없습니다."); }

        // 비밀번호 카운트 한도 초과로 계정 잠금
        boolean isAccountNonLocked = passwordDAO.getPasswordCount(employee.getEmployeeId()) < 5;

        // 퇴사 날짜가 지나면 계정 만료
        boolean isAccountNonExpired = true;
        Resignation resignation = resignationDAO.getResignedEmployee(employee.getEmployeeId());
        if (resignation != null) {
            LocalDate resignationDate = resignation.getResignationDate();
            LocalDate today = LocalDate.now();
            isAccountNonExpired = !resignationDate.isBefore(today);
        }

        return new CustomUserDetails(
                employee.getEmployeeId(),
                password.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getPosition().name())),
                employee.getDepartment().name(),
                isAccountNonLocked,
                isAccountNonExpired
        );
    }
}
