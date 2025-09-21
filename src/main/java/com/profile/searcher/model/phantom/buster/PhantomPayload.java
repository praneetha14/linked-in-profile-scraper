package com.profile.searcher.model.phantom.buster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhantomPayload {
    private String id;
    private String output = "first-result-object";
    private PhantomBusterInput argument;

    @Getter
    @Setter
    public static class PhantomBusterInput {
        private String search;
        private String keywords;
        private String sessionCookie;
        private int numberOfResultsPerLaunch = 100;
        private int numberOfResultsPerSearch = 100;
        private String searchType = "keywords";
    }
}

