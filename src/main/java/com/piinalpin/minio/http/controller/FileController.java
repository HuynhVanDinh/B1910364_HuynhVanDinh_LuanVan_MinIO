package com.piinalpin.minio.http.controller;

import com.piinalpin.minio.http.dto.FileDto;
import com.piinalpin.minio.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

@Slf4j
@RestController
@RequestMapping(value = "/file")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {

    @Autowired
    private MinioService minioService;

    @GetMapping
    public ResponseEntity<Object> getFiles() {
        return ResponseEntity.ok(minioService.getListObjects());
    }

    @GetMapping(value = "/**")
    public ResponseEntity<Object> getFile(HttpServletRequest request) throws IOException {
        String pattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        String filename = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(IOUtils.toByteArray(minioService.getObject(filename)));
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<FileDto> upload(@ModelAttribute FileDto request) {
        FileDto fileDto = minioService.uploadFile(request);
        return ResponseEntity.ok().body(fileDto);
    }

    @PutMapping(value = "/{filename}")
    public ResponseEntity<Object> updateFile(@PathVariable String filename, @ModelAttribute FileDto request) {
        return ResponseEntity.ok().body(minioService.updateFile(filename, request));
    }

    @DeleteMapping(value = "/{filename}")
    public ResponseEntity<Object> deleteFile(@PathVariable String filename) {
        return ResponseEntity.ok().body(minioService.deleteFile(filename));
    }

}
