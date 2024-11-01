package com.woosan.hr_system.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
    private int page;
    private int size;
    private String keyword;

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
