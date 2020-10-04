package ru.comearth.russianpost.services;

import org.springframework.web.multipart.MultipartFile;

public interface UploadXlsService {
    void uploadXls (MultipartFile file) throws Exception;
}
