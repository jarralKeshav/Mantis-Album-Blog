package com.bishnah.springrestdemo.payload.auth.album;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPayloadDTO {

    @NotBlank
    @Schema(description = "Album name", example = "Travel", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description= "Description of the album", example = "Description", requiredMode =
            Schema.RequiredMode.REQUIRED)
    private  String  description;
}
