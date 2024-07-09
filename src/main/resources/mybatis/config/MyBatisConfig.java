package com.woosan.hr_system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
@Configuration
@MapperScan("com.woosan.hr_system.employee.dao")
@MapperScan("com.woosan.hr_system.report.dao") // MyBatis Mapper 인터페이스 패키지 지정 추가함
public class MyBatisConfig {
    // 추가 설정 필요 없어서 비워둠
}
