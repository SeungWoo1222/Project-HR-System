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

    // 비밀번호 정보 조회
    public Password getPasswordInfoById(String employeeId) { return sqlSession.selectOne(NAMESPACE + "selectPassword", employeeId); }

    // 비밀번호 정보 등록
    public void insertPassword(Password password) {
        sqlSession.insert(NAMESPACE + "insertPassword", password);
    }

    // 비밀번호 수정
    public void updatePassword(Password password) {
        sqlSession.update(NAMESPACE + "updatePassword", password);
    }

    // 비밀번호 정보 삭제
    public void deletePassword(String employeeId) { sqlSession.delete(NAMESPACE + "deletePassword", employeeId); }

    // 비밀번호 카운트 조회
    public int getPasswordCount(String employeeId) { return sqlSession.selectOne(NAMESPACE + "getPasswordCount", employeeId); }

    // 비밀번호 카운트 1 증가
    public void incrementPasswordCount(String employeeId) { sqlSession.update(NAMESPACE + "incrementPasswordCount", employeeId); }

    // 비밀번호 카운트 초기화
    public void resetPasswordCount(String employeeId) { sqlSession.update(NAMESPACE + "resetPasswordCount", employeeId); }

    // 비밀번호 카운트 5으로 설정
    public void maxOutPasswordCount(String employeeId) { sqlSession.update(NAMESPACE + "maxOutPasswordCount", employeeId); }
}
