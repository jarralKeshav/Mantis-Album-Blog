package com.bishnah.springrestdemo.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProfileDTO {

    private Long id;

    private String email;

    private String authorities;
}
