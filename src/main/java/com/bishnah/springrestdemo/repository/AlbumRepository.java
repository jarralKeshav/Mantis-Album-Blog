package com.bishnah.springrestdemo.repository;


import com.bishnah.springrestdemo.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByAccount_id(Long id);
}
