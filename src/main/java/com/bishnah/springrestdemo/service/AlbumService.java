package com.bishnah.springrestdemo.service;

import com.bishnah.springrestdemo.model.Album;
import com.bishnah.springrestdemo.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
        }

    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    public List<Album> findByAccount_id(Long id){
        return albumRepository.findByAccount_id(id);
    }

    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    public void deletAlbum(Album album) {
        albumRepository.delete(album);
    }
}
