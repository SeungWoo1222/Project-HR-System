package com.woosan.hr_system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.woosan.hr_system.employee.dao")
public class MyBatisConfig {
    // 추가 설정 필요 없어서 비워둠
}
