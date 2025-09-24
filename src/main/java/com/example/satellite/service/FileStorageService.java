package com.example.satellite.service;

import com.example.satellite.entity.ImageMeta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileStorageService {

    List<ImageMeta> getAll();

    ImageMeta getById(Long id);

}
