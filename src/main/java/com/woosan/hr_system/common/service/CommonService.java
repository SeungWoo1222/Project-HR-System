package com.woosan.hr_system.common.service;

import java.util.Set;

public interface CommonService {
    <T> void processFieldChanges(T original, T updated, Set<String> fieldsToCompare);
    <T> boolean hasFieldChanges(T original, T updated, Set<String> fieldsToCompare);
}
