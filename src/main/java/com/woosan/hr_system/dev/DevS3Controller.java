// src/main/java/.../dev/DevS3Controller.java
package com.woosan.hr_system.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/s3")
public class DevS3Controller {
    private final S3Client s3Client;
    private final String bucket = "haruharubucket";

    @PostMapping("/put")
    public ResponseEntity<String> put(@RequestParam String key, @RequestParam String text) {
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(),
                RequestBody.fromString(text));
        return ResponseEntity.ok("put:" + key);
    }

    @GetMapping("/get")
    public ResponseEntity<String> get(@RequestParam String key) {
        var obj = s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(key).build());
        return ResponseEntity.ok(obj.asUtf8String());
    }
}
