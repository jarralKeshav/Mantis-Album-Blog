package com.bishnah.springrestdemo.payload.auth.album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private Long id;
    private String title;
    private String description;
//    private String originalFileName;
    private String fileName;
    private String download_link;
}
