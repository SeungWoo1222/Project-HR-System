package com.woosan.hr_system.auth;

import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    EmployeeDAO employeeDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeDAO.getEmployeeById(username);
        if (employee == null) {
            throw new UsernameNotFoundException("해당 사원을 찾을 수 없습니다.");
        }
        return new CustomUserDetails(
                employee.getEmployeeId(),
                employee.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getPosition().name())),
                employee.getDepartment()
        );
    }
}
