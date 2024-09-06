package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.report.model.ReportFileLink;
import com.woosan.hr_system.file.model.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ReportFileDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportFileDAO.";

    public void createReportFile(int reportId, int fileId) {
        ReportFileLink reportFileLink = new ReportFileLink();
        reportFileLink.setReportId(reportId);
        reportFileLink.setFileId(fileId);
        sqlSession.insert(NAMESPACE + "createReportFile", reportFileLink);
    }

    // reportId에 맞는 fileIdList 반환
    public List<Integer> getFileIdsByReportId(int reportId) {
        return sqlSession.selectList(NAMESPACE + "getFileIdsByReportId", reportId);
    }

    public int getReportIdByFileId(int fileId) {
        return sqlSession.selectOne(NAMESPACE + "getReportIdByFileId", fileId);
    }

    public void deleteReportFileByReportId(int reportId) {
        sqlSession.delete(NAMESPACE + "deleteReportFileByReportId", reportId);
    }

    public void deleteReportFile(int reportId, int fileId) {
        HashMap<String, Integer> params = new HashMap<>();
        params.put("reportId", reportId);
        params.put("fileId", fileId);
        sqlSession.delete(NAMESPACE + "deleteReportFile", params);
    }


    public int countOtherReportConnect(int fileId) {
        return sqlSession.selectOne(NAMESPACE + "countOtherReportConnect", fileId);
    }

//================================================파일 아카이브 생성======================================================
    public void createReportFileArchive(File file, int reportId) {
        UserSessionInfo userSessionInfo = new UserSessionInfo(); //로그인한 사용자 id, 현재시간 설정

        Map<String, Object> params = new HashMap<>();
            params.put("fileId", file.getFileId());
            params.put("reportId", reportId); // Report ID를 FileArchive 객체에서 가져옴
            params.put("deletedDate", userSessionInfo.getNow()); // 삭제 날짜
            params.put("originalFileName", file.getOriginalFileName()); // 원본 파일 이름
            params.put("storedFileName", file.getStoredFileName()); // 저장된 파일 이름
            params.put("fileSize", file.getFileSize()); // 파일 크기
            params.put("uploadDate", file.getUploadDate()); // 업로드 날짜
            params.put("uploadedBy", file.getUploadedBy()); // 업로드한 사용자
            params.put("fileIdUsage", file.getFileIdUsage()); // 파일 ID 용도



        sqlSession.insert(NAMESPACE + "createReportFileArchive", params);
    }
}
