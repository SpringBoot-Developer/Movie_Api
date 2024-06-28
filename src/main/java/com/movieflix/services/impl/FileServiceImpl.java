package com.movieflix.services.impl;

import com.movieflix.services.FileService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get name of the file
        String fileName = file.getOriginalFilename();
        // to get the file path
        String filePath = path + File.separator + fileName;
        // create file object
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                log.info("Directory created successfully.");
            } else {
                log.error("Failed to create directory.");
            }
        } else {
            log.info("Directory already exists.");
        }

        // copy the file or upload file to the path
        // Files.copy(file.getInputStream(), Paths.get(filePath),
        // StandardCopyOption.REPLACE_EXISTING);
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;
        return new FileInputStream(filePath);
    }
}
