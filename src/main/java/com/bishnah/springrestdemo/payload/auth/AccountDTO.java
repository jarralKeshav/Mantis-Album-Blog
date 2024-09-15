package com.bishnah.springrestdemo.payload.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountDTO {

    @Email
    @Schema(description = "Email Address", example = "example@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "password!@#$34", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 6, maxLength = 20)
    private String password;
}
