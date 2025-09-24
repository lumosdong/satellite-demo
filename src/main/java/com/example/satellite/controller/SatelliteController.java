package com.example.satellite.controller;

import com.example.satellite.entity.ImageMeta;
import com.example.satellite.repository.ImageMetaRepository;
import com.example.satellite.service.SatelliteServiceImpl.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class SatelliteController {
    @Autowired
    private FileStorageServiceImpl storage;

    @Autowired
    private ImageMetaRepository repo;

    // 上传图片
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("lat") double lat,
                                    @RequestParam("lng") double lng) throws IOException {
        String filename = storage.store(file);
        ImageMeta meta = new ImageMeta();
        meta.setFilename(filename);
        meta.setLat(lat);
        meta.setLng(lng);

        boolean ok = storage.ensureThumbnail(filename);
        meta.setThumbnailGenerated(ok);
        repo.save(meta);
        return ResponseEntity.ok(meta);
    }

    // 搜索
    // 将左上角和右下角的经纬度传入进行查询
    @GetMapping("/search")
    public List<ImageMeta> search(@RequestParam double minLat,@RequestParam double maxLat,@RequestParam double minLng,@RequestParam double maxLng){
        return repo.findInBox(minLat,maxLat,minLng,maxLng);
    }

    // 下载原图
    @GetMapping("download/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename) throws MalformedURLException{
        Path p = storage.getImagePath(filename);
        Resource r = new UrlResource(p.toUri());
        if (!r.exists()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\""+filename+"\"")
                .body(r);
    }

    @GetMapping("/download/baidu")
    public ResponseEntity<String> downloadFromBaidu(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam String filename) throws IOException {
        String saved =storage.downloadAndStoreFromBaidu(lng, lat, filename);
        return ResponseEntity.ok("下载成功: " + saved);
    }

}
