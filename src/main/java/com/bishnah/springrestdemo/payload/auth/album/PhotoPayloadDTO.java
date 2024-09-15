package com.bishnah.springrestdemo.payload.auth.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhotoPayloadDTO {

    @NotBlank
    @Schema(description = "Photo name", example = "Seflie", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description= "Description of the Photo", example = "Description", requiredMode =
            Schema.RequiredMode.REQUIRED)
    private  String  description;

}
