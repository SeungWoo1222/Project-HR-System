package com.woosan.hr_system.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private List<T> data;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
