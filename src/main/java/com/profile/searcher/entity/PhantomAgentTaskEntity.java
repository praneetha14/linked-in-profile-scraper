package com.profile.searcher.entity;

import com.profile.searcher.model.enums.PhantomAgentTaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "phantom_agent_task")
public class PhantomAgentTaskEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "container_id")
    private String containerId;

    @Column(name = "university")
    private String university;

    @Column(name = "current_designation")
    private String currentDesignation;

    @Column(name = "passed_out_year")
    private Integer passedOutYear;

    @Column(name = "task_status")
    @Enumerated(EnumType.STRING)
    private PhantomAgentTaskStatus phantomAgentTaskStatus;

    @Column(name = "retry_count")
    private int retryCount;
}
