package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.file.model.File;

import java.util.List;

public interface ReportFileDAO {
    void createReportFile(int reportId, int fileId);
    List<Integer> getFileIdsByReportId(int reportId);
    List<Integer> getReportIdsByFileId(int fileId);
    void deleteReportFileByReportId(int reportId);
    void deleteReportFile(int reportId, int fileId);
    int countOtherReportConnect(int fileId);
    void createReportFileArchive(File file, int reportId);
}
