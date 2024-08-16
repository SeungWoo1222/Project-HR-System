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


    @Override // 보고서 파일 삭제
    public void deleteReportFile(int reportId) {
        log.info("보고서 삭제 과정 중 ReportFileServiceImpl deleteReport도착완료");

        // 조인 테이블에서 reportId에 해당하는 fileId 리스트를 가져옴
        List<Integer> fileIdList = reportFileDAO.getFileIdsByReportId(reportId);
        log.info("reportFileDAO로 fileIdList받아옴 fileIdList {}", fileIdList);

        for (Integer fileId : fileIdList) {
            // 다른 reportId가 fileId를 참조하는지 확인 - 한다면 count는 0보다 큼
            int count = reportFileDAO.countOtherReportConnect(fileId);
            log.info("reportFileDAO.countOtherReportConnect 완료 : {}", count);

            if (count <= 0) {
                log.info("다른 reportId에 연결된 것이 없으므로 삭제 대상임");
                // 다른 reportId에 연결된 것이 없으면 파일 삭제

                // 조인 테이블에서 reportId에 해당하는 레코드를 삭제
                reportFileDAO.deleteReportFileByFileId(fileId); // 수정필요 fileId로 그거에 맞는 fileId를 지워야됨
                log.info("reportFileDAO.deleteReportFile 완료");

                // ↓ 파일 아카이브에 삽입하는 로직 ↓
                // 파일 정보를 가져옴
                File file = fileService.getFileInfo(fileId); // 수정필요 fileId로 그 파일만 생성해야함 나머지는 확인 후 생성해야함
                log.info("fileDAO.getFileListById에서 받아온 fileList 형태 : {}", file);

                // 파일아카이브 삽입
                reportFileDAO.createReportFileArchive(file, reportId); // 여기도 fileList가 아니라 file로 생성
                log.info("reportFileDAO.createReportFileArchive 완료");
                // ↑ 파일 아카이브에 삽입하는 로직 ↑

                // 파일 삭제
                fileService.deleteFile(fileId); // 파일삭제또한 fileIdList가 아닌 fileId를 보내줘서 그 파일만 삭제하는 방식으로 바꿔야함
                log.info("fileDAO.deleteFile 완료");
            } else {
                // fileId에 연결된 보고서 있다면 reportId로 조인테이블 삭제
                reportFileDAO.deleteReportFileByReportId(reportId);
            }
        }
    }
}
