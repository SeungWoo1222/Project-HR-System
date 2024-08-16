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
import com.woosan.hr_system.upload.model.File;
import com.woosan.hr_system.upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        if(report != null) throw new IllegalArgumentException("에러 발생!!");
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
    @Transactional
    @Override // 보고서 수정
    public void updateReport(Report report, List<MultipartFile> reportFileList, List<Integer> registeredFileIdList) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        List<Integer> newFileIdList = new ArrayList<>();
        List<Integer> reportIdList = new ArrayList<>();

        // 파일 처리
        if (reportFileList != null && !reportFileList.isEmpty()) {
            newFileIdList = handleFilesForReport(reportFileList, registeredFileIdList);
        }

        // 결재자 수에 따른 처리
        if (report.getIdList().size() > 1) {
            // 결재자 수가 여러명으로 바뀐 경우
            log.info("reportUpdate 서비스 메소드 실행");
            deleteReport(report.getReportId());

            report.setCreatedDate(userSessionInfo.getNow()); // 현재시간 설정
            reportIdList = createReport(report); // 보고서 생성 후 reportId 반환

            log.info("보고서 생성 후 reportIdList반환 완료 : {}", reportIdList);
        } else {
            log.info("결재자 한명인 경우 실행");
            report.setModifiedDate(userSessionInfo.getNow()); // 현재시간 설정
            report.setApproverId(report.getIdList().get(0));
            report.setApproverName(report.getNameList().get(0));
            reportDAO.updateReport(report);
            reportIdList.add(report.getReportId());
        }

        insertJoinTable(reportIdList, newFileIdList);
    }

    private List<Integer> handleFilesForReport(List<MultipartFile> toUploadFileList, List<Integer> fileIdList) {
        List<Integer> toLinkFileIdList = new ArrayList<>();

        for (Integer fileId : fileIdList) {
            // ↓ 두 파일이 같은 파일인지 비교 시작 ↓
            File registeredFile = fileService.getFileInfo(fileId);

            for (MultipartFile file : toUploadFileList) {
                if (toLinkFileIdList.size() != toUploadFileList.size()) { // 파일을 모두 비교했는지 확인
                    // 파일이 같은지 다른지 비교하기 위한 변수
                    boolean isFileMatch = false;
                    log.info("비교 할 file : {}", registeredFile);
                    log.info("업로드 할 file : {}", file);

                    // 두 파일의 사이즈가 같다면 해싱 비교
                    if (registeredFile.getFileSize() == file.getSize()) {
                        try (
                                InputStream fileInputStream = file.getInputStream();
                                FileInputStream compareFileInputStream = new FileInputStream(String.valueOf(registeredFile))
                        ) {
                            String fileHash = calculateHash(fileInputStream);
                            String compareFileHash = calculateHash(compareFileInputStream);

                            // 두 파일이 같다면
                            if (fileHash.equals(compareFileHash)) {
                                isFileMatch = true;
                            } else {
                                isFileMatch = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // ↑ 두 파일이 같은 파일인지 비교 끝 ↑
                    } else if (registeredFile.getFileSize() != file.getSize()) {
                        isFileMatch = false;
                    }

                    // 두 파일이 같다면 fileId를 조인테이블에 연결할 fildIdList에 넣어줌
                    if (isFileMatch) {
                        toLinkFileIdList.add(fileId);

                        // 다르다면 파일 업로드 후 fildId를 반환하여 조인테이블에 연결할 fildIdList에 넣어줌
                    } else {
                        int uploadingFileId = fileService.uploadingFile(file, "report");
                        toLinkFileIdList.add(uploadingFileId);
                    }
                } else {
                    break; // 업로드할 파일 비교 완료 시
                }

            }
        }
        return toLinkFileIdList;
    }

    // 컨트롤러 상에서 실행하는 순서가 달라서 한곳으로 모으기위함
    private void insertJoinTable(List<Integer> reportIdList, List<Integer> fileIdList) {
        log.info("insertJoinTable 메소드 호출 완료 reportIdList : {} fileIdList : {}", reportIdList, fileIdList);

        List<Integer> jointableReportIdList = new ArrayList<>(reportIdList);
        List<Integer> jointableFileIdList = new ArrayList<>(fileIdList);

        for (Integer reportId : jointableReportIdList) {
            List<Integer> existingFileIdList = reportFileService.getFileIdsByReportId(reportId);
            log.info("reportId에 맞춰 존재하는 파일아이디 리스트 reportId : {} 아이디리스트 : {} ", reportId, existingFileIdList);
            log.info("조인테이블에 넣어야할 fileId : {} 이미 존재하는 fileId : {}", jointableFileIdList, existingFileIdList);

            // 이미 존재하는 파일 ID를 제외한 파일만 추가
            jointableFileIdList.stream()
                    .filter(fileId -> !existingFileIdList.contains(fileId))
                    .forEach(fileId -> reportFileService.createReportFile(reportId, fileId))
            ;
        }
        log.info("insertJoinTable 완료");


    }


    private String calculateHash(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = inputStream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        byte[] bytes = digest.digest();
        return Hex.encodeHexString(bytes);
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



