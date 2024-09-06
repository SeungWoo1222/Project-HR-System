package com.woosan.hr_system.file.controller;

import com.woosan.hr_system.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;

    @GetMapping("/downloadFile")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @RequestParam("fileId") int fileId,
            @RequestParam("originalFileName") String originalFileName) {

        // S3에서 파일 다운로드
        byte[] fileData = fileService.downloadFile(fileId);

        // 파일 이름을 UTF-8로 인코딩
        String encodedFileName = null;

        try {
            encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(new ByteArrayResource(fileData));
    }
}
