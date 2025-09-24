package com.example.satellite.service.SatelliteServiceImpl;

import com.example.satellite.entity.ImageMeta;
import com.example.satellite.repository.ImageMetaRepository;
import com.example.satellite.service.FileStorageService;
import jakarta.annotation.Resource;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileStorageServiceImpl {
    @Resource
    private ImageMetaRepository imageMetaRepository;

    @Value("${baidu.map.ak}")
    private String baiduMapAk;

    private final String basePath;

    public FileStorageServiceImpl(@Value("${file.storage.base-path}") String path) {
        this.basePath = path;
        try {
            init(); // 启动时自动执行
        } catch (IOException e) {
            throw new RuntimeException("无法初始化存储目录: " + basePath, e);
        }
    }


    //获取原图路径
    public Path getImagePath(String filename){
        return Paths.get(basePath).resolve(filename);
    }
    //获取缩略图路径
    public Path getThumbPath(String filename){
        return Paths.get(basePath).resolve("thumbnails").resolve(filename);
    }

    //初始化文件目录
    public void init() throws IOException {
        Files.createDirectories(Paths.get(basePath));
        Files.createDirectories(Paths.get(basePath,"thumbnails"));
    }

    //存储上传文件
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("存储失败，文件为空: " + filename);
            }
            Path destinationFile = getImagePath(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("存储文件失败: " + filename, e);
        }

//        try (InputStream in = file.getInputStream()) {
//            Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
//            return filename;
//        } catch (IOException e) {
//            throw new RuntimeException("存储文件失败: " + filename, e);
//        }
    }


    //保证缩略图存在
    public boolean ensureThumbnail(String filename) throws IOException {
        Path img = getImagePath(filename);
        Path thumb = getThumbPath(filename);

        if (Files.exists(thumb)) return true;
        if (!Files.exists(img)) return false;

        // 用流方式避免 Windows 锁文件
        try (InputStream in = Files.newInputStream(img);
             OutputStream out = Files.newOutputStream(thumb)) {
            Thumbnails.of(in).size(200, 200).toOutputStream(out);
        }
        return true;
    }


    public Path load(String filename) {
        return getImagePath(filename);
    }

    public String downloadAndStoreFromBaidu(double lng,double lat,String filename) throws IOException {
        try {
            String url = String.format("https://api.map.baidu.com/staticimage/v2?ak=%s&center=%f,%f&zoom=12&width=800&height=600&baselayer=satellite",
                    baiduMapAk, lat, lng
            );

            Path destinationFile = getImagePath(filename);
            try(InputStream in = new URL(url).openStream()){
                Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            ensureThumbnail(filename);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("下载并保存百度地图图片失败: " + filename, e);
        }
    }

//    @Override
//    public List<ImageMeta> getAll() {
//        return imageMetaRepository.findAll();
//    }
//
//    @Override
//    public ImageMeta getById(Long id) {
//        return imageMetaRepository.findById(id).orElse(null);
//    }
}
