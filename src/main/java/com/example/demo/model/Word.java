package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table (name = "words")
@NoArgsConstructor
public class Word {

    @Id
    @Column
    private Long id;

    @Column
    private String turkishWord;

    @Column
    private String russianWord;
}
