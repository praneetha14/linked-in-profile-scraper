package com.profile.searcher.model.phantom.buster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkedInProfileScrapResponse {
    private String id;
    private String status;
    private String retryNumber;
    private String resultObject;
    private int exitCode;
}
