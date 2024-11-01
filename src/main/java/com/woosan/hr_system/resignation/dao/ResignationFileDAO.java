package com.woosan.hr_system.resignation.dao;

import com.woosan.hr_system.resignation.model.ResignationFile;
import com.woosan.hr_system.file.model.File;
import java.util.List;

public interface ResignationFileDAO {
    List<File> selectAllFileInfo(String employeeId);
    List<Integer> selectFileIdsByResignationId(String employeeId);
    int countFilesByResignationId(String employeeId);
    void insertResignationFile(ResignationFile resignationFile);
    void deleteResignationFile(ResignationFile resignationFile);
    void deleteAllByResignationId(String employeeId);
}
