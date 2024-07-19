package com.woosan.hr_system.Search;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SearchService<T> {
    List<T> search(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);
    int count(@Param("keyword") String keyword);
}
