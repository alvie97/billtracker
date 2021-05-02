package com.billtracker.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@Data
public class SimpleResponse extends RepresentationModel<SimpleResponse> {
    private String message;
}
