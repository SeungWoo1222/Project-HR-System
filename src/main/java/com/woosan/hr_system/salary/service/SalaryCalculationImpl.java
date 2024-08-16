package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.dao.RatioDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SalaryCalculationImpl implements SalaryCalculation {
    @Autowired
    private RatioDAO ratioDAO;

    @Override// 근로소득세 계산
    public int calculateIncomeTax(int taxableSalary) {
        int numDependents = 4; // 부양 가족 수
        int numChildren = 3; // 8세 이상 20세 이하 자녀 수

        Map<String , Object> map = new HashMap<>();
        map.put("numDependents", numDependents);
        map.put("taxableSalary", taxableSalary / 1000);

        log.debug("numDependents : {}", map.get("numDependents"));
        log.debug("taxableSalary : {}", map.get("taxableSalary"));

        // 간이세액표에서 해당하는 근로소득세 조회
        int income = ratioDAO.selectIncomeTax(map);
        log.debug("income : {}", income);

        // 근로소득세에서 가족 중 8세 이상 20세 이하 자녀 수만큼 공제
//        income -= calculateChildTaxDeduction(numChildren);
        int minus = calculateChildTaxDeduction(numChildren);
        log.debug("minus : {}", minus);
        income -= minus;

        // 공제한 금액이 음수인 경우의 세액은 0원
        int result = Math.max(income, 0);
        log.debug("최종 근로소득세 : {}", result);
        return result;
    }

    // 가족 중 8세 이상 20세 이하 자녀 수만큼 공제한 금액 계산
    private int calculateChildTaxDeduction(int numChildren) {
        if (numChildren >= 0 && numChildren <= 11) {
            return switch (numChildren) {
                case 0 -> 0;
                case 1 -> 12500;
                case 2 -> 29160;
                default -> 29160 + (25000 * (numChildren - 2));
            };
        } else {
            throw new IllegalArgumentException("자녀 수가 잘못되었습니다.");
        }
    }
}
