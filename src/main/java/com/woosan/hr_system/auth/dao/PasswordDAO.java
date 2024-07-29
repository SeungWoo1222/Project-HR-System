package com.woosan.hr_system.auth.dao;

import com.woosan.hr_system.auth.model.Password;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.auth.mapper.PasswordMapper.";

    // 비밀번호 조회
    public Password selectPassword(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "selectPassword", employeeId);
    }

    // 비밀번호 등록
    public void insertPassword(Password password) {
        sqlSession.insert(NAMESPACE + "insertPassword", password);
    }

    // 비밀번호 수정
    public void updatePassword(Password password) {
        sqlSession.update(NAMESPACE + "updatePassword", password);
    }

    // 비밀번호 삭제
    public void deletePassword(String employeeId) {
        sqlSession.delete(NAMESPACE + "deletePassword", employeeId);
    }

    // 비밀번호 카운트 조회
    public int getPasswordCount(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "getPasswordCount", employeeId);
    }

    // 비밀번호 카운트 +1 수정
    public void addPasswordCount(String employeeId) {
        sqlSession.update(NAMESPACE + "addPasswordCount", employeeId);
    }

    // 비밀번호 카운트 0으로 수정
    public void removePasswordCount(String employeeId) {
        sqlSession.update(NAMESPACE + "removePasswordCount", employeeId);
    }
}
