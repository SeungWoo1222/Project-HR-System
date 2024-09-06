package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.dao.ReportFileDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.file.dao.FileDAO;
import com.woosan.hr_system.file.model.File;
import com.woosan.hr_system.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override // reportId를 이용한 보고서 조회
    public List<Integer> getFileIdsByReportId(int reportId) {
        return reportFileDAO.getFileIdsByReportId(reportId);
    }

    @Override // fileId를 이용한 보고서 조회
    public int getReportIdByFileId(int fileId) {
        return reportFileDAO.getReportIdByFileId(fileId);
    }

    @Override // 파일 수정
    public void updateReportFile(Report report, List<MultipartFile> toUploadFileList, List<Integer> registeredFileIdList, List<Integer> reportIdList) {
        // 조인테이블에 연결할 fileIdList
        List<Integer> newFileIdList = new ArrayList<>();

        // ↓ 파일 처리 ↓
        // 기존의 파일이 없다면 업로드파일을 업로드 후 조인테이블에 fileId 삽입
        if (registeredFileIdList == null || registeredFileIdList.isEmpty()) {
            for (MultipartFile file : toUploadFileList) {
                int fileId = fileService.uploadingFile(file, "report");
                newFileIdList.add(fileId);
            }
            // 업로드할 파일이 있다면 기존의 파일과 같은지 비교함 - 같다면 조인테이블만 수정
        } else if (toUploadFileList != null && !toUploadFileList.isEmpty()) {
            newFileIdList = handleFilesForReport(toUploadFileList, registeredFileIdList, report.getReportId());
        }
        // ↑ 파일 처리 ↑
        insertJoinTable(reportIdList, newFileIdList);
    }

    // 기존의 파일과 업로드할 파일이 같은지 비교함
    private List<Integer> handleFilesForReport(List<MultipartFile> toUploadFileList, List<Integer> fileIdList, int reportId) {
        List<Integer> toLinkFileIdList = new ArrayList<>();
        Map<Integer, Boolean> deleteFileMap = new HashMap<>(); // 삭제 할 파일들

        // 초기 deleteFileMap 설정
        for (Integer fileId : fileIdList) {
            deleteFileMap.put(fileId, true);
        }

        for (Integer fileId : fileIdList) {
            // ↓ 두 파일이 같은 파일인지 비교 시작 ↓
            File registeredFile = fileService.getFileInfo(fileId);

            for (MultipartFile file : toUploadFileList) {
                if (toLinkFileIdList.size() != toUploadFileList.size()) { // 파일을 모두 비교했는지 확인
                    // 파일이 같은지 다른지 비교하기 위한 변수
                    boolean isFileMatch = false;

                    // 두 파일이 같은지 비교
                    if (registeredFile.getFileSize() == file.getSize() &&
                            registeredFile.getOriginalFileName().equals(file.getOriginalFilename())) {
                        isFileMatch = true;
                    } else {
                        isFileMatch = false;
                    }

                    // 두 파일이 같다면 fileId를 조인테이블에 연결할 fildIdList에 넣어줌
                    if (isFileMatch) {
                        toLinkFileIdList.add(fileId);
                        deleteFileMap.put(fileId, false); // 파일이 같다면 삭제하지 않도록 설정

                        // 다르다면 파일 업로드 후 fildId를 반환하여 조인테이블에 연결할 fildIdList에 넣어줌
                        // deleteFileMap에서는 true로 설정하여 삭제하도록 함
                    } else {
                        int uploadingFileId = fileService.uploadingFile(file, "report");
                        toLinkFileIdList.add(uploadingFileId);
                    }
                } else {
                    break; // 업로드할 파일 비교 완료 시
                }

            }
        }

        // deleteFileMap에서 true로 설정된 파일들만 삭제
        deleteFileMap.forEach((fileId, deleteTrue) -> {
            if (deleteTrue) {
                deleteReportFile(reportId, fileId);
            }
        });

        return toLinkFileIdList;
    }

    // fileId와 reportId를 받아서 조인테이블 데이터 생성
    private void insertJoinTable(List<Integer> reportIdList, List<Integer> fileIdList) {

        List<Integer> jointableReportIdList = new ArrayList<>(reportIdList);
        List<Integer> jointableFileIdList = new ArrayList<>(fileIdList);

        for (Integer reportId : jointableReportIdList) {
            List<Integer> existingFileIdList = getFileIdsByReportId(reportId);

            // 이미 존재하는 파일 ID를 제외한 파일만 추가
            jointableFileIdList.stream()
                    .filter(fileId -> !existingFileIdList.contains(fileId))
                    .forEach(fileId -> createReportFile(reportId, fileId))
            ;
        }

    }

    @Override // 보고서 파일 삭제
    public void deleteReportFileByReportId(int reportId) {
        // 조인 테이블에서 reportId에 해당하는 fileId 리스트를 가져옴
        List<Integer> fileIdList = reportFileDAO.getFileIdsByReportId(reportId);

        for (Integer fileId : fileIdList) {
            // report의 file 개수만큼 조인테이블 삭제
            deleteReportFile(fileId, reportId);
        }
    }

    @Override
    // 파일 삭제 - ReportId는 파일 아카이브 테이블에 등록하기 위함
    public void deleteReportFile(int reportId, int fileId) {
        // 조인 테이블에서 파일Id와 reportId에 해당하는 레코드를 삭제
        reportFileDAO.deleteReportFile(reportId, fileId);

        // ↓ 파일 아카이브에 삽입하는 로직 ↓
        // 파일 정보를 가져옴
        File file = fileService.getFileInfo(fileId);

        // 파일아카이브 삽입
        reportFileDAO.createReportFileArchive(file, reportId);
        // ↑ 파일 아카이브에 삽입하는 로직 ↑

        log.debug("fileService.deleteFile 하기 전 fileId 확인 : {}", fileId);

        fileService.deleteFile(fileId);
    }

}
