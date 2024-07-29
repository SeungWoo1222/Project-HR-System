package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.CustomUserDetails;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private PasswordDAO passwordDAO;

    @Autowired
    public CustomUserDetailsService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeDAO.getEmployeeById(username);
        Password password = passwordDAO.selectPassword(username);

        if (employee == null) {
            throw new UsernameNotFoundException("해당 사원을 찾을 수 없습니다.");
        }
        return new CustomUserDetails(
                employee.getEmployeeId(),
                password.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getPosition().name())),
                employee.getDepartment().name()
        );
    }
}
