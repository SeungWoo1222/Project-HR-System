package com.woosan.hr_system.config;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.TimeZone;

@Component
public class TimezoneConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
