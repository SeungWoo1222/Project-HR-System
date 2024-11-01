package com.woosan.hr_system.exception.file;

public class FileInfoNotFoundException extends RuntimeException {
    public FileInfoNotFoundException(int fileId) {
        super("해당 파일을 찾을 수 없습니다.\n파일 ID : " + fileId);
    }
}