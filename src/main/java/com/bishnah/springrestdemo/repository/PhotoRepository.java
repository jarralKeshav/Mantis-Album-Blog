package com.bishnah.springrestdemo.repository;

import com.bishnah.springrestdemo.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByAlbum_id(Long id);

}
