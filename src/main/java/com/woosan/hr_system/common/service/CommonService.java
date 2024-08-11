package com.woosan.hr_system.common.service;

import java.util.Set;

public interface CommonService {
    <T> void verifyChanges(T original, T updated, Set<String> fieldsToCompare);
}
