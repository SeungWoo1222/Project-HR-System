package com.woosan.hr_system.common.service;

import com.woosan.hr_system.exception.employee.NoChangesDetectedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

@Service
public class CommonServiceImpl implements CommonService {
    @Override // 변경사항 확인 return NoChangesDetectedException
    public <T> void processFieldChanges(T original, T updated, Set<String> fieldsToCompare) {
        if (!compareFields(original, updated, fieldsToCompare)) {
            throw new NoChangesDetectedException();
        }
    }

    @Override // 변경사항 확인 return boolean
    public <T> boolean hasFieldChanges(T original, T updated, Set<String> fieldsToCompare) {
        if (!compareFields(original, updated, fieldsToCompare)) {
            return false;
        }
        return true;
    }

    // 두 객체의 필드가 동일한지 비교 확인하는 메소드
    private <T> boolean compareFields (T original, T updated, Set<String> fieldsToCompare) {
        try {
            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true); // 비공개 필드 접근 가능
                if (fieldsToCompare.contains(field.getName())) {
                    Object originalValue = field.get(original);
                    Object updatedValue = field.get(updated);
                    if (!Objects.equals(originalValue, updatedValue)) {
                        return true; // 변경 사항 있음
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("변경사항 필드 확인 중 오류가 발생했습니다.", e);
        }
        return false; // 변경 사항 없음
    }
}
