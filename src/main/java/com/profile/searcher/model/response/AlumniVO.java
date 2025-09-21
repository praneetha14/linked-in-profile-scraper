package com.profile.searcher.model.response;

import java.util.UUID;

/***
 * AlumniVO is the response class representing an alumni profile with key details such as educational background,
 * professional information, and connection metadata.
 * @param id The unique identifier for the alumni.
 * @param title The title associated with the alumni's name.
 * @param fullName The full name of alumni.
 * @param universityName The name of university alumni attended.
 * @param profileHeadLine A short headline or tagline from the alumni's profile.
 * @param location The location of the alumni.
 * @param connectionDegree The degree of connection like 1st, 2nd, 3rd between user and alumni.
 * @param company The current or most recent company that alumni is associated.
 * @param profileUrl The URL to view the alumni's profile.
 * @param companyUrl The URL of the company associated with the alumni.
 * @param passedOutYear The year of alumni graduated or completed studies.
 */
public record AlumniVO(UUID id, String title, String fullName, String universityName, String profileHeadLine,
                       String location, String connectionDegree, String company, String profileUrl, String companyUrl,
                       Integer passedOutYear) {

}

