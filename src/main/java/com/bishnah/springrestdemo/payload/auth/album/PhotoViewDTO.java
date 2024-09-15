package com.bishnah.springrestdemo.payload.auth.album;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhotoViewDTO {
    private Long id;
    private String title;
    private String description;
}
