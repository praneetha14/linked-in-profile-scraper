package com.profile.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "alumni")
public class AlumniEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "profile_head_line")
    private String profileHeadLine;

    @Column(name = "location")
    private String location;

    @Column(name = "connection_degree")
    private String connectionDegree;

    @Column(name = "current_organization")
    private String company;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "company_url")
    private String companyUrl;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private UniversityEntity university;

    @Column(name = "passed_out_year")
    private Integer passedOutYear;

}
