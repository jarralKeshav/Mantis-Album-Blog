package com.bishnah.springrestdemo.payload.auth.album;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumViewDTO {

    @Id
    private Long id;

    @NotBlank
    @Schema(description = "Album name", example = "Travel", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description= "Description of the album", example = "Description", requiredMode =
            Schema.RequiredMode.REQUIRED)
    private  String  description;

    private List<PhotoDTO> photos;
}
