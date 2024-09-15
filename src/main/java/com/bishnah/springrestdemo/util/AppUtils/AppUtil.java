package com.bishnah.springrestdemo.util.AppUtils;

import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppUtil {

    public static String PATH = "src" + File.separator +"main" + File.separator + "resources" + File.separator +
            "static" + File.separator + "uploads"+ File.separator;

    public static String get_photo_upload_path(
            String fileName,String folder_name, long album_id
    ) throws IOException {
        String path =PATH+ album_id+ File.separator+ folder_name;

        Files.createDirectories(Paths.get(path));
        return new File(path).getAbsolutePath() + File.separator + fileName;


    }

    public static boolean delelet_photo_from_path(String fileName, String folder_name, Long album_id) {
        try {
            File f = new File(PATH+ album_id+ File.separator+ folder_name+ File.separator + fileName);
            if(f.delete()){
                return true;
            }else {
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static BufferedImage getThumbnail(MultipartFile originalFile, Integer width) throws IOException {
        BufferedImage thumbImg = null;
        BufferedImage img = ImageIO.read(originalFile.getInputStream());
        thumbImg = Scalr.resize(img,Scalr.Method.AUTOMATIC,Scalr.Mode.FIT_EXACT,width,Scalr.OP_ANTIALIAS);
        return thumbImg;
    }


    public static Resource getFileAsResource(Long album_id, String folder_name, String file_name) throws IOException {
        String location =
                "src" + File.separator +"main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads"+ File.separator  + album_id+ File.separator+ folder_name + File.separator + file_name;
        File file = new File(location);
        if(file.exists()){
            Path path = Paths.get(file.getAbsolutePath());
            return new UrlResource(path.toUri());
        }
        else{
            return null;
        }
    }

}

//src/main/resources/static/uploads