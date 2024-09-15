package com.bishnah.springrestdemo.service;

import com.bishnah.springrestdemo.model.Photo;
import com.bishnah.springrestdemo.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Photo save(Photo photo){
        return photoRepository.save(photo);
    }
    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    public List<Photo> findByAlbumId(Long id) {
        return photoRepository.findByAlbum_id(id);
    }


    public void delete(Photo photo) {
        photoRepository.delete(photo);
    }
}

