package com.bishnah.springrestdemo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    private String description;

    private String originalFileName;

    private String fileName;


    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = false)
    private Album album;

}
