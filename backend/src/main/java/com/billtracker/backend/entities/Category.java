package com.billtracker.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Category extends RepresentationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tag;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "deleted_on")
    private Date deletedOn;

    public Category(String tag) {
        this.tag = tag;
        this.createdOn = new Date();
        this.deletedOn = null;
    }
}
