package com.woosan.hr_system.upload.dao;

import com.woosan.hr_system.upload.model.File;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.upload.dao.FileDAO.";

    // 모든 파일 정보 조회
    public List<File> getAllFiles() {
        return sqlSession.selectList(NAMESPACE + "selectAllFiles");
    }

    // 파일 ID로 파일 정보 조회
    public File getFileById(int fileId) { return sqlSession.selectOne(NAMESPACE + "selectFileById", fileId); }

    // 파일 ID 리스트 파일 정보 조회
    public List<File> getFileListById(List<Integer> fileIdList) { return sqlSession.selectList(NAMESPACE + "selectFileListById", fileIdList); }

    // 파일 ID로 저장된 파일 이름 조회
    public String getFileStoredNameById(int fileId) { return sqlSession.selectOne(NAMESPACE + "selectFileStoredNameById", fileId); }

    // 파일 정보 생성
    public int insertFile(File file) { return sqlSession.insert(NAMESPACE + "insertFile", file); }

    // 파일 정보 업데이트
    public int updateFile(File file) {
        return sqlSession.update(NAMESPACE + "updateFile", file);
    }

    // 파일 정보 삭제
    public int deleteFile(int fileId) {
        return sqlSession.delete(NAMESPACE + "deleteFile", fileId);
    }
}
