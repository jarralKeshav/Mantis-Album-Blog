package com.bishnah.springrestdemo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Album
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName ="id", nullable = false)
    private Account account;


}
