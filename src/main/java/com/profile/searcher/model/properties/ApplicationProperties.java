package com.profile.searcher.model.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.linkedin.searcher")
@Getter
@Setter
public class ApplicationProperties {
    private String baseUrl;
}
