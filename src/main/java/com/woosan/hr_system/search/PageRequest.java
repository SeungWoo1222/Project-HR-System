package com.woosan.hr_system.search;

public class PageRequest {
    private int page;
    private int size;
    private String keyword;

    public PageRequest() {
        this.page = 0;
        this.size = 10;
    }

    public PageRequest(int page, int size, String keyword) {
        this.page = page;
        this.size = size;
        this.keyword = keyword;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
