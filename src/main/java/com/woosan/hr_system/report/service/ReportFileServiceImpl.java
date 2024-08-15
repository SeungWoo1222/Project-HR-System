package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.ReportFileDAO;
import com.woosan.hr_system.upload.dao.FileDAO;
import com.woosan.hr_system.upload.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReportFileServiceImpl implements ReportFileService {
    @Autowired
    private ReportFileDAO reportFileDAO;
    @Autowired
    private FileDAO fileDAO;

    @Override // 보고서 파일 생성
    public void createReportFile(int reportId, int fileId) {
        reportFileDAO.createReportFile(reportId, fileId);
    }

    @Override  // reportId로 fileId를 가져옴
    public List<Integer> getFileIdsByReportId(int reportId) { return reportFileDAO.getFileIdsByReportId(reportId); }


    @Override // 보고서 파일 삭제
    public void deleteReportFile(int reportId) {
        log.info("보고서 삭제 과정 중 ReportFileServiceImpl deleteReport도착완료");

        // 조인 테이블에서 reportId에 해당하는 fileId 리스트를 가져옴
        List<Integer> fileIdList = reportFileDAO.getFileIdsByReportId(reportId);
        log.info("reportFileDAO로 fileIdList받아옴 fileIdList {}", fileIdList);

        // 조인 테이블에서 reportId에 해당하는 레코드를 삭제
        reportFileDAO.deleteReportFile(reportId);
        log.info("reportFileDAO.deleteReportFile 완료");


        // fileList에 있는 fileId들을 이용하여 실제 파일 데이터를 삭제
        if (fileIdList != null && !fileIdList.isEmpty()) {
            List<File> fileList = fileDAO.getFileListById(fileIdList);
            reportFileDAO.createReportFileArchive(fileList, reportId);
            log.info("reportFileDAO.deleteReportFile 완료");
            fileDAO.deleteFileByFileIdList(fileIdList);
            log.info("fileDAO.deleteFileByFileIdList 완료");


        }
    }


}
