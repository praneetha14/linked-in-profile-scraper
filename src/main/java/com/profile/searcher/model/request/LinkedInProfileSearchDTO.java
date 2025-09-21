package com.profile.searcher.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LinkedInProfileSearchDTO {

    @NotBlank
    private String university;

    @NotBlank
    private String currentDesignation;

    private Integer graduationYear;
}
