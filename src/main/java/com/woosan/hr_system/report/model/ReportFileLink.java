package com.woosan.hr_system.report.model;

import java.time.LocalDate;

public class ReportFileLink {
    private int fileId;
    private Long reportId;

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

}
