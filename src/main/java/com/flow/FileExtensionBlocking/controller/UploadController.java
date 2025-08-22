// com.flow.FileExtensionBlocking.controller.UploadController
package com.flow.FileExtensionBlocking.controller;

import com.flow.FileExtensionBlocking.service.ExtensionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final ExtensionService service;

    public UploadController(ExtensionService service) {
        this.service = service;
    }

    /**
    파일 업로드 시 확장자 차단 검사
     실제 업로드 수행 x
     **/
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        if (service.isBlocked(filename)) {
            return ResponseEntity.badRequest().body("blocked"); // 차단
        }
        return ResponseEntity.ok("ok");
    }
}
