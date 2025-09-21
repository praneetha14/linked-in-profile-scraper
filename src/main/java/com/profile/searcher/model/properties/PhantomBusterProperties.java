package com.profile.searcher.model.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.phantom.buster")
public class PhantomBusterProperties {

    private String phantomId;
    private String phantomBusterApiKey;
    private String linkedInSessionCookie;
}
