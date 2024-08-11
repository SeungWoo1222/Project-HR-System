package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportFileLink;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.upload.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDAO reportDAO;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private FileService fileService;



    //=====================================================생성 메소드======================================================
    @Override // 보고서 생성
    public void createReport(Report report, List<MultipartFile> reportDocuments) {
        UserSessionInfo userSessionInfo = new UserSessionInfo(); //로그인한 사용자 id, 현재시간 설정

        List<Integer> fileIds = new ArrayList<>();
        List<Long> reportIds = new ArrayList<>();

        if (reportDocuments != null) {
            // 파일들 체크 후 DB에 저장할 파일명 반환
            for (MultipartFile reportdocument : reportDocuments) {
                int fileId = fileService.uploadingFile(reportdocument, "보고서"); // 생성된 fileId 가져옴
                fileIds.add(fileId);
            }
        }

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
            Long reportId = reportDAO.createReport(params); // 생성된 reportId 가져옴
            reportIds.add(reportId);
        }

        Iterator<Long> reportIterater = reportIds.iterator();
        Iterator<Integer> fileIterater = fileIds.iterator();

        reportDAO.insertReportFileMapping(reportIterater.next(), fileIterater.next());
    }

    @Override // 요청 들어온 보고서 작성
    public Long createReportFromRequest(Report report, String approverId) {
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
    public Report getReportById(Long reportId) {
        return reportDAO.getReportById(reportId);
    }

    @Override
    public List<Integer> getFileIdsByReportId(Long reportId) { return reportDAO.getFileIdsByReportId(reportId); }


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
        LocalDateTime modified_date = LocalDateTime.now(); // 현재 기준 수정 시간 설정
        report.setModifiedDate(modified_date);

        Map<String, Object> params = new HashMap<>();
        params.put("reportId", report.getReportId());
        params.put("title", report.getTitle());
        params.put("content", report.getContent());
        params.put("approverId", report.getIdList().get(0));
        params.put("approverName", report.getNameList().get(0));
        params.put("completeDate", report.getCompleteDate());
        params.put("modifiedDate", report.getModifiedDate());

        reportDAO.updateReport(params);
    }
//=====================================================수정 메소드======================================================
//=====================================================삭제 메소드======================================================

    @Override // 보고서 삭제
    public void deleteReport(Long reportId) {
        // shared_trash 테이블에 삭제될 데이터들 삽입
        reportDAO.insertReportIntoSharedTrash(reportId);
        reportDAO.deleteReport(reportId);
    }

//=====================================================삭제 메소드======================================================
}



