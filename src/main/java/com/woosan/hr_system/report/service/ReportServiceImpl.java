package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.controller.ReportController;
import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.dao.ReportFileDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportFileLink;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDAO reportDAO;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private FileService fileService;
    @Autowired
    private ReportFileDAO reportFileDAO;
    @Autowired
    private ReportFileService reportFileService;


    //=====================================================생성 메소드======================================================
    @Override // 보고서
    public List<Integer> createReport(Report report) {
        log.info("createReport ServiceImpl 도착 완료");

        List<Integer> reportIdList = new ArrayList<>();

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
            int reportId = reportDAO.createReport(params); // 생성된 reportId 가져옴
            reportIdList.add(reportId);
        }
        log.info("createReport ServiceImpl 반환 완료");
        return reportIdList;
    }

    @Override // 보고서 + 파일 생성
    public void createReportWithFile(Report report, List<MultipartFile> reportDocuments) {
        UserSessionInfo userSessionInfo = new UserSessionInfo(); //로그인한 사용자 id, 현재시간 설정

        List<Integer> fileIdlist = new ArrayList<>();
        List<Integer> reportIdlist = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("writerId", userSessionInfo.getCurrentEmployeeId());
        params.put("title", report.getTitle());
        params.put("content", report.getContent());
        params.put("createdDate", userSessionInfo.getNow());
        params.put("status", "미처리");
        params.put("completeDate", report.getCompleteDate());

        for (int i = 0; i < report.getNameList().size(); i++) {
            params.put("approverId", report.getIdList().get(i));
            params.put("approverName", report.getNameList().get(i));
            int reportId = reportDAO.createReport(params); // 생성된 reportId 가져옴
            reportIdlist.add(reportId);
        }

        if (reportDocuments != null) {
            // 파일들 체크 후 DB에 저장할 파일명 반환
            for (MultipartFile reportdocument : reportDocuments) {
                int fileId = fileService.uploadingFile(reportdocument, "report"); // 생성된 fileId 가져옴
                fileIdlist.add(fileId);
            }
        }

        // reportId와 fileId를 모두 순회하며 조인테이블 삽입
        for (int reportId : reportIdlist) {
            for (int fileId : fileIdlist) {
                reportFileDAO.createReportFile(reportId, fileId);
            }
        }
    }

    @Override // 요청 들어온 보고서 작성
    public int createReportFromRequest(Report report, String approverId) {
//    public void createReport(Report report, MultipartFile file) {
        LocalDateTime createdDate = LocalDateTime.now(); // 현재 기준 생성시간 설정
        report.setCreatedDate(createdDate);
        report.setStatus("미처리"); // 결재상태 설정

        // approverName 설정
        Employee employee = employeeDAO.getEmployeeById(approverId);
        String approverName = employee.getName();

        Map<String, Object> params = new HashMap<>();
        params.put("writerId", report.getWriterId());
        params.put("approverId", approverId);
        params.put("approverName", approverName);
        params.put("title", report.getTitle());
        params.put("content", report.getContent());
        params.put("createdDate", report.getCreatedDate());
        params.put("status", report.getStatus());
        params.put("completeDate", report.getCompleteDate());

        reportDAO.createReport(params);
        return report.getReportId();
    }

//=====================================================생성 메소드======================================================
//=====================================================조회 메소드======================================================
    @Override // 모든 보고서 조회
    public List<Report> getAllReports(String reportStart, String reportEnd, String employeeId) {
        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (reportStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(reportStart, formatter);
        }

        if (reportEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(reportEnd, formatter);
        }
        return reportDAO.getAllReports(employeeId, startYearMonth, endYearMonth);
    }

    @Override // 특정 보고서 조회
    public Report getReportById(int reportId) {
        return reportDAO.getReportById(reportId);
    }



    @Override // 날짜범위 내 결재할 보고서 조회
    public List<Report> getPendingApprovalReports(String approverId, String approvalStart, String approvalEnd) {
        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (approvalStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(approvalStart, formatter);
        }

        if (approvalEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(approvalEnd, formatter);
        }

        // 파싱된 날짜를 DAO로 전달하여 호출
        return reportDAO.getPendingApprovalReports(approverId, startYearMonth, endYearMonth);
    }

    @Override // 최근 5개 보고서 조회
    public List<Report> getRecentReports(String writerId) {
        return reportDAO.getRecentReports(writerId);
    }

    @Override // 보고서 검색
    public PageResult<Report> searchReports(PageRequest pageRequest, String writerId, int searchType, String reportStart, String reportEnd) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Report> reports = reportDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset, writerId, searchType, reportStart, reportEnd);
        int total = reportDAO.count(pageRequest.getKeyword(), writerId, searchType, reportStart, reportEnd);

        return new PageResult<>(reports, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 보고서 통계 조회
    public List<ReportStat> getReportStats(String statisticStart, String statisticEnd, List<String> writerIdList) {

        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (statisticStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(statisticStart, formatter);
        }

        if (statisticEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(statisticEnd, formatter);
        }

        return reportDAO.getReportStats(startYearMonth, endYearMonth, writerIdList);
    }
//=====================================================조회 메소드======================================================
//=====================================================수정 메소드======================================================

    @Override // 보고서 수정
    public void updateReport(Report report) {
        report.setApproverId(report.getIdList().get(0));
        report.setApproverName(report.getNameList().get(0));
        log.info("ReportServiceImpl report 변환 후 형태 {}", report);
        reportDAO.updateReport(report);
    }
//=====================================================수정 메소드======================================================
//=====================================================삭제 메소드======================================================
    @Transactional
    @Override // 보고서 삭제
    public void deleteReport(int reportId) {
        log.info("보고서 삭제 과정 중 ReportServiceImpl deleteReport도착완료");
        // shared_trash 테이블에 삭제될 데이터들 삽입
        reportDAO.insertReportIntoSharedTrash(reportId);
        // 보고서 삭제
        reportDAO.deleteReport(reportId);

        // 조인테이블 서비스로 reportId를 보내줌 -> 파일 삭제
        reportFileService.deleteReportFile(reportId);
    }

//=====================================================삭제 메소드======================================================
}



