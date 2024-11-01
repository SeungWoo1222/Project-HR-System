package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.file.service.FileService;
import com.woosan.hr_system.notification.service.NotificationService;
import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.dao.ReportFileDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDAO reportDAO;
    @Autowired
    private FileService fileService;
    @Autowired
    private ReportFileDAO reportFileDAO;
    @Autowired
    private ReportFileService reportFileService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private NotificationService notificationService;
    //=====================================================생성 메소드======================================================
    @Override // 보고서 생성 - 중앙 제어 함수(파일 유무에 따라 로직을 다르게 실행)
    public void createReportAndFile(Report report, List<MultipartFile> reportDocuments) {
        // 보고서 생성 후 reportId 반환(파일이 있다면 조인테이블 연결을 위함)
        List<Integer> reportIdlist = createReport(report);
        List<Integer> fileIdlist = new ArrayList<>();

        // 파일이 있다면 업로드 후 fileId 반환
        if (reportDocuments != null) {
            for (MultipartFile reportdocument : reportDocuments) {
                int fileId = fileService.uploadingFile(reportdocument, "report"); // 생성된 fileId 가져옴
                fileIdlist.add(fileId);
            }
            // 조인 테이블 연결
            joinReportAndFile(reportIdlist, fileIdlist);
        }
    }

    @Override // 요청 들어온 보고서 작성
    public int createReportFromRequest(Report report, List<MultipartFile> reportDocuments) {
        List<Integer> reportIdlist = createReport(report);
        List<Integer> fileIdlist = new ArrayList<>();

        // 파일이 있다면 업로드 후 fileId 반환
        if (reportDocuments != null) {
            for (MultipartFile reportdocument : reportDocuments) {
                int fileId = fileService.uploadingFile(reportdocument, "report"); // 생성된 fileId 가져옴
                fileIdlist.add(fileId);
            }
            // 조인 테이블 연결
            joinReportAndFile(reportIdlist, fileIdlist);
        }

        return reportIdlist.get(0);
    }

    // 보고서 생성
    private List<Integer> createReport(Report report) {
        List<Integer> reportIdList = new ArrayList<>();
        List<Integer> fileIdList = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("writerId", report.getWriterId());
        params.put("title", report.getTitle());
        params.put("content", report.getContent());
        params.put("status", "미처리");
        params.put("createdDate", report.getCreatedDate());
        params.put("completeDate", report.getCompleteDate());

        for (int i = 0; i < report.getNameList().size(); i++) {
            params.put("approverId", report.getIdList().get(i));
            params.put("approverName", report.getNameList().get(i));
            int reportId = reportDAO.createReport(params);
            reportIdList.add(reportId);
            // 보고서 생성 후 결재자에게 알림 생성
            String writerName = employeeService.getEmployeeNameById(report.getWriterId());
            notificationService.createNotification(report.getIdList().get(i), "결재할 보고서가 있습니다. <br>작성자 : " + writerName, "/admin/request/notification?reportId=" + reportId);
        }
        return reportIdList;
    }

    // 보고서, 파일 조인테이블 연결 함수
    private void joinReportAndFile(List<Integer> reportIdlist, List<Integer> fileIdlist) {
        // reportId와 fileId를 모두 순회하며 조인테이블 삽입
        for (int reportId : reportIdlist) {
            for (int fileId : fileIdlist) {
                reportFileDAO.createReportFile(reportId, fileId);
            }
        }
    }
//=====================================================생성 메소드======================================================
//=====================================================조회 메소드======================================================
    @Override // 모든 보고서 조회
    public List<Report> getAllReports(String employeeId) {
        return reportDAO.getAllReports(employeeId);
    }

    @Override // 특정 보고서 조회
    public Report getReportById(int reportId) {
        Report report = checkReportAuthorization(reportId);
        return report;
    }


    @Override // 최근 5개 보고서 조회
    public List<Report> getRecentReports(String writerId) {
        return reportDAO.getRecentReports(writerId);
    }

    @Override // 결재 미처리 보고서 조회(MANAGER)
    public List<Report> getUnprocessedReports(String approverId) {
        return reportDAO.getUnprocessedReports(approverId);
    }

    @Override // 보고서 검색
    public PageResult<Report> searchReports(PageRequest pageRequest, String writerId, Integer searchType, String approvalStatus, String startDate, String endDate) {
        // 보여줄 리스트의 범위를 지정
        int offset = pageRequest.getPage() * pageRequest.getSize();
        // 범위에 속하는 보고서를 검색함
        List<Report> reports = reportDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset, writerId, searchType, approvalStatus, startDate, endDate);
        // 범위에 속하는 보고서 개수를 세서 페이징
        int total = reportDAO.count(pageRequest.getKeyword(), writerId, searchType, approvalStatus, startDate, endDate);

        return new PageResult<>(reports, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    // 결재할 보고서 검색
    @Override
    public PageResult<Report> toApproveSearchReports(PageRequest pageRequest, String approverId, Integer searchType, String approvalStatus, String startDate, String endDate) {
        // 보여줄 리스트의 범위를 지정
        int offset = pageRequest.getPage() * pageRequest.getSize();
        // 범위에 속하는 보고서를 검색함
        List<Report> reports = reportDAO.toApproveSearch(pageRequest.getKeyword(), pageRequest.getSize(), offset, approverId, searchType, approvalStatus, startDate, endDate);
        // 범위에 속하는 보고서 개수를 세서 페이징
        int total = reportDAO.toApproveCount(pageRequest.getKeyword(), approverId, searchType, approvalStatus, startDate, endDate);

        return new PageResult<>(reports, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 보고서 통계 조회
    public List<ReportStat> getReportStats(LocalDate startDate, LocalDate endDate, List<String> writerIdList) {
        return reportDAO.getReportStats(startDate, endDate, writerIdList);
    }

    @Override // 보고서 통계 조회
    public List<ReportStat> getReportStats(LocalDate startDate, LocalDate endDate, String writerId) {
        return reportDAO.getReportStats(startDate, endDate, writerId);
    }
//=====================================================조회 메소드======================================================
//=====================================================수정 메소드======================================================
    @Transactional
    @Override // 보고서 수정
    public void updateReport(Report report, List<MultipartFile> toUploadFileList, List<Integer> userSelectedFileIdList) {
        // 수정 권한 및 Report 유무 확인
        checkReportAuthorization(report.getReportId());

        UserSessionInfo userSessionInfo = new UserSessionInfo();
        List<Integer> createdReportIdList = new ArrayList<>();
        List<Integer> existingFileIdList = reportFileService.getFileIdsByReportId(report.getReportId());

        // 결재자 수에 따른 처리
        if (report.getIdList().size() > 1) {
            // 결재자 수가 여러명으로 바뀐 경우
            report.setCreatedDate(userSessionInfo.getNow()); // 현재시간 설정
            createdReportIdList = createReport(report); // 보고서 생성 후 reportId 반환
            reportFileService.updateReportFile(report, toUploadFileList, userSelectedFileIdList, existingFileIdList, createdReportIdList);
            deleteReport(report.getReportId());
        } else {
            report.setModifiedDate(userSessionInfo.getNow()); // 현재시간 설정
            report.setApproverId(report.getIdList().get(0));
            report.setApproverName(report.getNameList().get(0));
            reportDAO.updateReport(report);
            createdReportIdList.add(report.getReportId()); // 조인테이블 수정
            reportFileService.updateReportFile(report, toUploadFileList, userSelectedFileIdList, existingFileIdList, createdReportIdList);
        }
    }
    @Override // 보고서 결재 처리
    public String updateApprovalStatus(int reportId, String status, String rejectionReason) {
        // report 객체 설정
        Report report = new Report();
        report.setReportId(reportId);
        report.setStatus(status);
        report.setRejectReason(rejectionReason);

        reportDAO.updateApprovalStatus(report);
        return "보고서 결재가 완료되었습니다.";
    }
//=====================================================수정 메소드======================================================
//=====================================================삭제 메소드======================================================
    @Transactional
    @Override // 보고서 삭제
    public void deleteReport(int reportId) {
        // 삭제 권한 및 Report 유무 확인
        checkReportAuthorization(reportId);

        // shared_trash 테이블에 삭제될 데이터들 삽입
        reportDAO.insertReportIntoSharedTrash(reportId);

        // 보고서 삭제
        reportDAO.deleteReport(reportId);

        // 요청 된 보고서라면 requset테이블의 reportId 삭제
        if (requestService.getRequestByReportId(reportId) > 0){
            requestService.deleteReportId(reportId);
        }

        // 조인테이블 서비스로 reportId를 보내줌 -> 파일 삭제
        reportFileService.deleteReportFileByReportId(reportId);
    }
//=====================================================삭제 메소드======================================================
//=====================================================기타 메소드======================================================
    // 보고서에 대한 접근 권한, 보고서가 있는지 확인
    public Report checkReportAuthorization(int reportId) {
        // 현재 유저의 employeeId를 가져옴
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentEmployeeId = userSessionInfo.getCurrentEmployeeId();

        // Report를 가져옴
        Report report = reportDAO.getReportById(reportId);

        if (report == null) {
            throw new IllegalArgumentException("해당 Report를 찾을 수 없습니다. \nReport ID : " + reportId);
        }
        // 작성자 혹은 결재자와 로그인한 사용자가 동일하지 않으면 권한 오류 발생
        if (!report.getApproverId().equals(currentEmployeeId) && !report.getWriterId().equals(currentEmployeeId)) {
                throw new SecurityException("접근 권한이 없습니다.");
        }
        return report; // 요청 정보 반환
    }
//=====================================================기타 메소드======================================================

}



