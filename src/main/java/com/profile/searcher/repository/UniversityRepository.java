package com.profile.searcher.repository;

import com.profile.searcher.entity.UniversityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UniversityRepository extends JpaRepository<UniversityEntity, UUID> {

    @Query("SELECT DISTINCT u FROM UniversityEntity u JOIN FETCH u.alumniEntities WHERE u.name = :universityName")
    UniversityEntity findByName(String universityName);
}
