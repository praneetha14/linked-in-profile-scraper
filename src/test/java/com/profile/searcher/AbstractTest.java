package com.profile.searcher;

import com.profile.searcher.entity.AlumniEntity;
import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.entity.UniversityEntity;
import com.profile.searcher.model.enums.PhantomAgentTaskStatus;
import com.profile.searcher.repository.PhantomAgentTaskRepository;
import com.profile.searcher.repository.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {LinkedinProfileSearcherApplication.class, LinkedProfileSearcherAutoConfiguration.class,
        AbstractTest.TestConfig.class})
@ActiveProfiles("test")
public class AbstractTest {

    protected static final String ASSERT_MESSAGE = "Assertion must be true";

    @Autowired
    protected PhantomAgentTaskRepository phantomAgentTaskRepository;

    @Autowired
    protected UniversityRepository universityRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected PhantomAgentTaskEntity createPhantomAgentTaskEntity() {
        PhantomAgentTaskEntity phantomAgentTaskEntity = new PhantomAgentTaskEntity();
        phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.AGENT_LAUNCHED);
        phantomAgentTaskEntity.setUniversity("University");
        phantomAgentTaskEntity.setCurrentDesignation("software engineer");
        phantomAgentTaskEntity.setPassedOutYear(2023);
        return phantomAgentTaskEntity;
    }

    protected void createTestData() {
        UniversityEntity university = new UniversityEntity();
        university.setName("University");
        List<AlumniEntity> alumniEntityList = createAlumniEntities(university);
        university.setAlumniEntities(alumniEntityList);
        universityRepository.save(university);
    }

    private List<AlumniEntity> createAlumniEntities(UniversityEntity universityEntity) {
        List<AlumniEntity> alumniEntityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AlumniEntity alumniEntity = new AlumniEntity();
            alumniEntity.setLocation("location" + i);
            alumniEntity.setCompany("company" + i);
            alumniEntity.setFullName("fullName" + i);
            alumniEntity.setTitle("software engineer");
            alumniEntity.setProfileUrl("profileUrl");
            alumniEntity.setCompanyUrl("companyUrl" + i);
            alumniEntity.setConnectionDegree("2+");
            alumniEntity.setProfileHeadLine("profileHeadLine" + i);
            alumniEntity.setPassedOutYear(2023);
            alumniEntity.setUniversity(universityEntity);
            alumniEntityList.add(alumniEntity);
        }
        return alumniEntityList;
    }

    @AfterEach
    public void cleanup() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'",
                String.class
        );

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        }

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @TestConfiguration
    @EntityScan(basePackages = {"com.profile.searcher.entity"})
    public static class TestConfig {

    }
}
