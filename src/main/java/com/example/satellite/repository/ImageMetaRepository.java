package com.example.satellite.repository;

import com.example.satellite.entity.ImageMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageMetaRepository extends JpaRepository<ImageMeta,Long> {
    @Query("select i from ImageMeta i where i.lat between :minLat and :maxLat and i.lng between :minLng and :maxLng")
    List<ImageMeta> findInBox(double minLat, double maxLat, double minLng, double maxLng);
}
