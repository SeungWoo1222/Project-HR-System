package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.dao.ReportFileDAO;
import com.woosan.hr_system.upload.dao.FileDAO;
import com.woosan.hr_system.upload.model.File;
import com.woosan.hr_system.upload.service.FileService;
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
    @Autowired
    private FileService fileService;
    private ReportDAO reportDAO;

    @Override // 보고서 파일 생성
    public void createReportFile(int reportId, int fileId) {
        reportFileDAO.createReportFile(reportId, fileId);
    }

    @Override  // reportId로 fileId를 가져옴
    public List<Integer> getFileIdsByReportId(int reportId) {
        return reportFileDAO.getFileIdsByReportId(reportId);
    }

    @Override
    public int getReportIdByFileId(int fileId) {
        return reportFileDAO.getReportIdByFileId(fileId);
    }

    @Override // 보고서 파일 삭제
    public void deleteReportFileByReportId(int reportId) {
        log.info("보고서 삭제 과정 중 ReportFileServiceImpl deleteReport도착완료");

        // 조인 테이블에서 reportId에 해당하는 fileId 리스트를 가져옴
        List<Integer> fileIdList = reportFileDAO.getFileIdsByReportId(reportId);
        log.info("reportFileDAO로 fileIdList받아옴 fileIdList {}", fileIdList);

        for (Integer fileId : fileIdList) {
            // report의 file 개수만큼 조인테이블 삭제
            deleteReportFile(fileId, reportId);
        }
    }

//    @Override // 보고서 파일 삭제
//    public void deleteReportFile(int reportId, int fileId) {
//        log.info("deleteReportFileByFileId의 fileId : {}", fileId);
//        // file이 다른 report에도 연결이 돼있는지 확인
//        int count = reportFileDAO.countOtherReportConnect(fileId);
//        // 없다면 file 삭제
//        if (count <= 1) {
//            int reportId = getReportIdByFileId(fileId);
//            log.info("deleteReportFileByFileId의 fileId : {}, reportId : {}", fileId, reportId);
//            deleteFile(fileId, reportId);
//        }
//    }

    @Override
    // 파일 삭제 - ReportId는 파일 아카이브 테이블에 등록하기 위함
    public void deleteReportFile(int reportId, int fileId) {
        log.info("deleteReportFile의 reportId : {},  : fileId{}", reportId, fileId);
        // 조인 테이블에서 파일Id와 reportId에 해당하는 레코드를 삭제
        reportFileDAO.deleteReportFile(reportId, fileId);
        log.info("reportFileDAO.deleteReportFile 완료");

        // ↓ 파일 아카이브에 삽입하는 로직 ↓
        // 파일 정보를 가져옴
        File file = fileService.getFileInfo(fileId);
        log.info("fileDAO.getFileListById에서 받아온 fileList 형태 : {}", file);

        // 파일아카이브 삽입
        reportFileDAO.createReportFileArchive(file, reportId);
        log.info("reportFileDAO.createReportFileArchive 완료");
        // ↑ 파일 아카이브에 삽입하는 로직 ↑

        log.debug("fileService.deleteFile 하기 전 fileId 확인 : {}", fileId);

        fileService.deleteFile(fileId); // 파일삭제또한 fileIdList가 아닌 fileId를 보내줘서 그 파일만 삭제하는 방식으로 바꿔야함
        log.info("fileDAO.deleteFile 완료");
    }

}
