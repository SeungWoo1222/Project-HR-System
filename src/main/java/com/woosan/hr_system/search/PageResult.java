package com.woosan.hr_system.search;

import java.util.List;

public class PageResult<T> {
    private List<T> data;
    private int totalPages;
    private long totalElements;
    private int currentPage;

    public PageResult(List<T> data, int totalPages, long totalElements, int currentPage) {
        this.data = data;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
