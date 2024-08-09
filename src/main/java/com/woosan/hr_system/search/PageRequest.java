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

//    public PageRequest() {
//        this.page = 0;
//        this.size = 10;
//    }
}
