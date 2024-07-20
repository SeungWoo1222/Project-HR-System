package com.woosan.hr_system.upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.woosan.hr_system.employee.controller.EmployeeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private AmazonS3 amazonS3;

    private String bucketName = "haruharu-hrsystem-bucket";

    public String checkFile(MultipartFile file) {
        String message;
        if (file.isEmpty()) {
            return "empty";
        }

        try {
            uploadFile(file);
            return file.getOriginalFilename();
        } catch (IOException e) {
            logger.debug("‼️File Upload Error : {} ‼️",e.getMessage());
            e.printStackTrace();
            return "fail";
        }
    }

    public void uploadFile(MultipartFile file) throws IOException {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = System.currentTimeMillis() + "." + file.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
        logger.debug("File put to S3 with name: {}", fileName);
        convertedFile.delete();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
