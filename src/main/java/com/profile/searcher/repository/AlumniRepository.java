package com.profile.searcher.repository;

import com.profile.searcher.entity.AlumniEntity;
import com.profile.searcher.model.response.AlumniVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AlumniRepository extends JpaRepository<AlumniEntity, UUID> {

    @Query("SELECT new com.profile.searcher.model.response.AlumniVO(" +
            "a.id, a.title, a.fullName, u.name, a.profileHeadLine, a.location, a.connectionDegree, a.company, " +
            "a.profileUrl, a.companyUrl, a.passedOutYear) FROM AlumniEntity a JOIN UniversityEntity u ON a.university = u "
            + "where a.passedOutYear <= :graduationYear AND a.title = :currentDesignation AND u.name = :university")
    List<AlumniVO> findAllAlumni(String currentDesignation, String university, Integer graduationYear);

    @Query("SELECT new com.profile.searcher.model.response.AlumniVO(" +
            "a.id, a.title, a.fullName, u.name, a.profileHeadLine, a.location, a.connectionDegree, a.company, " +
            "a.profileUrl, a.companyUrl, a.passedOutYear) FROM AlumniEntity a JOIN UniversityEntity u ON a.university = u "
            + "where a.title = :currentDesignation AND u.name = :university")
    List<AlumniVO> findAllAlumniWithoutGraduationYear(String currentDesignation, String university);

    @Query("SELECT new com.profile.searcher.model.response.AlumniVO(" +
            "a.id, a.title, a.fullName, u.name, a.profileHeadLine, a.location, a.connectionDegree, a.company, " +
            "a.profileUrl, a.companyUrl, a.passedOutYear) FROM AlumniEntity a JOIN UniversityEntity u ON a.university = u ")
    Page<AlumniVO> findAllAlumni(Pageable pageable);
}
