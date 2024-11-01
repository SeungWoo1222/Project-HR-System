package com.woosan.hr_system.utils;

import kr.pe.bab2min.Kiwi;
import kr.pe.bab2min.KiwiBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class KiwiSingleton {
    /*
     * 통계를 불러올 때마다 매번 KiwiBuilder를 생성하고 인스턴스르 새로 생성하니 페이지 로드 시간이 너무 오래 걸려
     * 애플리케이션 실행 시 싱글톤 패턴으로 한 번만 생성되도록 변경
     */

    private final Kiwi kiwi;

    public KiwiSingleton() {
        try(KiwiBuilder builder = new KiwiBuilder("path_to_kiwi_model")) {
            this.kiwi = builder.build(KiwiBuilder.basicTypoSet, 2.0f);
            log.info("Kiwi instance created");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
