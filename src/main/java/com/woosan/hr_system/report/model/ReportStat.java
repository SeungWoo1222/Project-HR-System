package com.woosan.hr_system.report.model;

public class ReportStat {
    private String month;
    private int total;
    private int finished;
    private int unfinished;

    // getters and setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(int unfinished) {
        this.unfinished = unfinished;
    }
}
