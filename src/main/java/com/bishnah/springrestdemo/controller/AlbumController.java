package com.bishnah.springrestdemo.controller;

import com.bishnah.springrestdemo.model.Account;
import com.bishnah.springrestdemo.model.Album;
import com.bishnah.springrestdemo.model.Photo;
import com.bishnah.springrestdemo.payload.auth.album.*;
import com.bishnah.springrestdemo.service.AccountService;
import com.bishnah.springrestdemo.service.AlbumService;
import com.bishnah.springrestdemo.service.PhotoService;
import com.bishnah.springrestdemo.util.AppUtils.AppUtil;
import com.bishnah.springrestdemo.util.constants.AlbumError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RequestMapping("/api/v1")
@Tag(name = "Album Controller", description = "Controller for Album and Photo Management")
@Slf4j
public class AlbumController {

    static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_MAX_SIZE = 300;

    private final AccountService accountService;
    private final AlbumService albumService;
    private final PhotoService photoService;

    public AlbumController(AccountService accountService, AlbumService albumService, PhotoService photoService) {
        this.accountService = accountService;
        this.albumService = albumService;
        this.photoService = photoService;
    }


    @PostMapping(value = "/albums/add", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED) //optional
    @ApiResponse(responseCode = "400", description = "Please enter a valid name and description")
    @ApiResponse(responseCode = "200", description = "Album added")
    @Operation(summary = "Add an Album")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<AlbumViewDTO> addAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO, Authentication authentication) {
        try {
            Album album = new Album();
            album.setTitle(albumPayloadDTO.getTitle());
            album.setDescription(albumPayloadDTO.getDescription());
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();
            album.setAccount(account);
            album = albumService.createAlbum(album);
            AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getTitle(), album.getDescription(),null);
            return ResponseEntity.ok(albumViewDTO);

        } catch (Exception e) {
            log.debug(AlbumError.ADD_ALBUM_ERROR + " " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(value = "/albums/{album_id}/update", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED) //optional
    @ApiResponse(responseCode = "400", description = "Please enter a valid name and description")
    @ApiResponse(responseCode = "200", description = "Album added")
    @Operation(summary = "UPdate an Album")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<AlbumViewDTO> update_album(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO,
                                          @PathVariable Long album_id,
                                          Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            album.setTitle(albumPayloadDTO.getTitle());
            album.setDescription(albumPayloadDTO.getDescription());
            albumService.createAlbum(album);
            List<PhotoDTO> photoDTOList = new ArrayList<>();
            for(Photo photo: photoService.findByAlbumId(album.getId())){
                String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download-photo";
                photoDTOList.add(new PhotoDTO(photo.getId(),photo.getTitle(),photo.getDescription(),
                        photo.getFileName(),link));
            }

            AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getTitle(), album.getDescription(),photoDTOList);
            return ResponseEntity.ok(albumViewDTO);
        } catch (Exception e) {
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString() + " " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    @PutMapping(value = "/albums/{album_id}/photos/{photo_id}/update", produces = "application/json", consumes =
            "application/json")
    @ResponseStatus(HttpStatus.CREATED) //optional
    @ApiResponse(responseCode = "400", description = "Please enter a valid name and description")
    @ApiResponse(responseCode = "200", description = "Album added")
    @Operation(summary = "Update photo")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<PhotoViewDTO> update_photo(@Valid @RequestBody PhotoPayloadDTO photoPayloadDTO,
                                                     @PathVariable Long album_id,
                                                     @PathVariable Long photo_id,
                                                     Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Optional<Photo> optionalPhoto = photoService.findById(photo_id);
            if (optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if(photo.getAlbum().getId()!=album_id){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                photo.setTitle(photoPayloadDTO.getTitle());
                photo.setDescription(photoPayloadDTO.getDescription());
                photoService.save(photo);
                PhotoViewDTO photoViewDTO = new PhotoViewDTO(photo.getId(),photo.getTitle(),photo.getDescription());
                return ResponseEntity.ok(photoViewDTO);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

            }


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @DeleteMapping(value = "/albums/{album_id}/delete")
    @ResponseStatus(HttpStatus.CREATED) //optional
//    @ApiResponse(responseCode = "400", description = "Please enter a valid name and description")
    @ApiResponse(responseCode = "202", description = "Album Deleted")
    @Operation(summary = "Delete an Album")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<String> delete_album(@PathVariable Long album_id,Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }

            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            for (Photo photo: photoService.findByAlbumId(album_id)) {
                AppUtil.delelet_photo_from_path(photo.getFileName(),PHOTOS_FOLDER_NAME,album_id);
                AppUtil.delelet_photo_from_path(photo.getFileName(),THUMBNAIL_FOLDER_NAME,album_id);
                photoService.delete(photo);
            }
            albumService.deletAlbum(album);
            return  ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////







    @DeleteMapping(value = "/albums/{album_id}/photos/{photo_id}/delete")
    @ResponseStatus(HttpStatus.CREATED) //optional
    @ApiResponse(responseCode = "202", description = "Photo deleted")
    @Operation(summary = "delete a  photo")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<String> delete_photo(@PathVariable Long album_id, @PathVariable Long photo_id, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();
            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Optional<Photo> optionalPhoto = photoService.findById(photo_id);
            if (optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if (photo.getAlbum().getId()!=album_id){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }

                AppUtil.delelet_photo_from_path(photo.getFileName(),PHOTOS_FOLDER_NAME,album_id);
                AppUtil.delelet_photo_from_path(photo.getFileName(),THUMBNAIL_FOLDER_NAME,album_id);
                photoService.delete(photo);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);

            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////



    @GetMapping(value = "/albums", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of Albums")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "List album Api")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public List<AlbumViewDTO> albums(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        List<AlbumViewDTO> albumViewDTOList = new ArrayList<>();
        for (Album album : albumService.findByAccount_id(account.getId())) {

            List<PhotoDTO> photoDTOList = new ArrayList<>();
            for (Photo photo: photoService.findByAlbumId(album.getId())) {

                String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download-photo";
                photoDTOList.add(new PhotoDTO(photo.getId(),photo.getTitle(),photo.getDescription(),
                        photo.getFileName(),link));

            }
            albumViewDTOList.add(new AlbumViewDTO(album.getId(), album.getTitle(), album.getDescription(),photoDTOList));


        }
        return albumViewDTOList;
    }


    @GetMapping(value = "/albums/{album_id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of Albums")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "List album by album id")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<AlbumViewDTO> albums_by_id(@PathVariable Long album_id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;

        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (!album.getAccount().getId().equals(account.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<PhotoDTO> photoDTOList = new ArrayList<>();
        for (Photo photo: photoService.findByAlbumId(album.getId())) {
            String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download-photo";
            photoDTOList.add(new PhotoDTO(photo.getId(),photo.getTitle(),photo.getDescription(),
                    photo.getFileName(),link));

        }
        AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getTitle(), album.getDescription(),photoDTOList);
        return ResponseEntity.ok(albumViewDTO);



    }

    @PostMapping(value = "/albums/{album_id}/upload-photos", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload Photo in Album")
    @ApiResponse(responseCode = "400", description = "Please check payload or token")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<List<HashMap<String,List<?>>>> photos(@RequestPart() MultipartFile[] files,
                                                                @PathVariable Long album_id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        List<PhotoViewDTO> fileNamesWithSuccess = new ArrayList<>();
        List<String> fileNamesWithError = new ArrayList<>();

        Arrays.asList(files).forEach(file -> {
            String contentType = file.getContentType();
            if (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg")) {
//                fileNamesWithSuccess.add(file.getOriginalFilename());

                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = true;

                try {
                    String fileName = file.getOriginalFilename();
                    String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
                    String finalPhotoName = generatedString + fileName;
                    String absolute_fileLocation = AppUtil.get_photo_upload_path(finalPhotoName, PHOTOS_FOLDER_NAME, album_id);
                    Path path = Paths.get(absolute_fileLocation);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    Photo photo = new Photo();
                    photo.setTitle(fileName);
//                    photo.setDescription();
                    photo.setFileName(finalPhotoName);
                    photo.setOriginalFileName(fileName);
                    photo.setAlbum(album);
                    photoService.save(photo);

                    PhotoViewDTO photoViewDTO = new PhotoViewDTO(photo.getId(),photo.getTitle(),photo.getDescription());
                    fileNamesWithSuccess.add(photoViewDTO);

                    BufferedImage thumbImg = AppUtil.getThumbnail(file, THUMBNAIL_MAX_SIZE);
                    File thumbnail_location = new File(AppUtil.get_photo_upload_path(finalPhotoName,
                            THUMBNAIL_FOLDER_NAME, album_id));

                    ImageIO.write(thumbImg, file.getContentType().split("/")[1], thumbnail_location);


                } catch (Exception e) {
                    log.debug(AlbumError.PHOTO_UPLOAD_ERROR.toString() + " " + e.getMessage());
                    fileNamesWithError.add(file.getOriginalFilename());

                }
            } else {
                fileNamesWithError.add(file.getOriginalFilename());
            }
        });

        HashMap<String, List<?>> result = new HashMap<>();
        result.put("Success", fileNamesWithSuccess);
        result.put("Error", fileNamesWithError);

        List<HashMap<String, List<?>>> response = new ArrayList<>();
        response.add(result);

        return ResponseEntity.ok(response);

    }

    @GetMapping("albums/{album_id}/photos/{photo_id}/download-photo")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") Long album_id,
                                           @PathVariable("photo_id") Long photo_id,
                                           Authentication authentication) {
        return downloadFile(album_id, photo_id, PHOTOS_FOLDER_NAME, authentication);

    }

    @GetMapping("albums/{album_id}/photos/{photo_id}/download-thumbnail")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<?> downloadThumbnail(@PathVariable("album_id") Long album_id,
                                           @PathVariable("photo_id") Long photo_id,
                                           Authentication authentication) {
        return downloadFile(album_id, photo_id, THUMBNAIL_FOLDER_NAME, authentication);

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        public ResponseEntity<?> downloadFile(Long album_id, Long photo_id, String folder, Authentication authentication) {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Optional<Photo> optionalPhoto = photoService.findById(photo_id);
            if (optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if(photo.getAlbum().getId()!=album_id){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                Resource resource = null;
                try {
                    resource = AppUtil.getFileAsResource(album_id,folder,photo.getFileName());

                } catch (IOException e) {
                    return ResponseEntity.internalServerError().build();
                }
                if (resource == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                String contentType = "application/octet-sream";
                String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

        }


        //////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////



}






















