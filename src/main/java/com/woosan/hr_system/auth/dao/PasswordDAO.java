package com.woosan.hr_system.auth.dao;

import com.woosan.hr_system.auth.model.Password;

public interface PasswordDAO {
    Password getPasswordInfoById(String employeeId);
    void insertPassword(Password password);
    void updatePassword(Password password);
    void deletePassword(String employeeId);
    int getPasswordCount(String employeeId);
    void incrementPasswordCount(String employeeId);
    void resetPasswordCount(String employeeId);
    void maxOutPasswordCount(String employeeId);
}
