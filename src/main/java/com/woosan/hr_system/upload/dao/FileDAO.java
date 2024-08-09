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

    // 파일 생성 메소드
    public int insertFile(File file) {
        return sqlSession.insert(NAMESPACE + "insertFile", file);
    }

    // 파일 ID로 파일 조회 메소드
    public File selectFileById(int fileId) {
        return sqlSession.selectOne(NAMESPACE + "selectFileById", fileId);
    }

    // 모든 파일 조회 메소드
    public List<File> selectAllFiles() {
        return sqlSession.selectList(NAMESPACE + "selectAllFiles");
    }

    // 파일 업데이트 메소드
    public int updateFile(File file) {
        return sqlSession.update(NAMESPACE + "updateFile", file);
    }

    // 파일 삭제 메소드
    public int deleteFile(int fileId) {
        return sqlSession.delete(NAMESPACE + "deleteFile", fileId);
    }
}
