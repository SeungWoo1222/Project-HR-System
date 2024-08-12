package com.woosan.hr_system.resignation.dao;

import com.woosan.hr_system.resignation.model.ResignationFile;
import com.woosan.hr_system.upload.model.File;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResignationFileDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.resignation.dao.ResignationFileDAO";

    // resignationId(employeeId)의 모든 파일 정보 조회
    public List<File> selectAllFileInfo(String employeeId) {
        return sqlSession.selectList(NAMESPACE + ".selectAllFileInfo", employeeId);
    }

    // resignationId(employeeId)의 모든 파일 ID 조회
    public List<Integer> selectFileIdsByResignationId(String employeeId) {
        return sqlSession.selectList(NAMESPACE + ".selectFileIdsByResignationId", employeeId);
    }

    // resignationId(employeeId)의 파일 개수 조회
    public int countFilesByResignationId(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + ".countFilesByResignationId", employeeId);
    }

    // resignationFile 등록
    public void insertResignationFile(ResignationFile resignationFile) {
        sqlSession.insert(NAMESPACE + ".insertResignationFile", resignationFile);
    }

    // resignationFile 삭제
    public void deleteResignationFile(ResignationFile resignationFile) {
        sqlSession.delete(NAMESPACE + ".deleteResignationFile", resignationFile);
    }

    // 특정 resignationId(employeeId)에 대한 모든 파일 레코드 삭제
    public void deleteAllByResignationId(String employeeId) {
        sqlSession.delete(NAMESPACE + ".deleteAllResignationFile", employeeId);
    }
}
