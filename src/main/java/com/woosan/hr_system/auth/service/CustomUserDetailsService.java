package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.CustomUserDetails;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.resignation.dao.ResignationDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.resignation.model.Resignation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
        if (employee == null) { throw new UsernameNotFoundException("해당 사원을 찾을 수 없습니다."); }

        Password password = passwordDAO.getPasswordInfoById(username);

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

        // 사용자 권한 설정
        String position = employee.getPosition().name();
        String authority = "STAFF";
        if (position.equals("차장") || position.equals("부장")) authority = "MANAGER";
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_" + authority),
            new SimpleGrantedAuthority("ROLE_" + employee.getDepartment().name())
        );

        return new CustomUserDetails(
                employee.getEmployeeId(),
                employee.getName(),
                password.getPassword(),
                authorities,
                employee.getDepartment().name(), // 이미 권한에 설정해두어서 삭제해도 되지만 승우 코드와 프론트쪽에서 검사 때문에 일단 남겨둠
                isAccountNonLocked,
                isAccountNonExpired
        );
    }
}
