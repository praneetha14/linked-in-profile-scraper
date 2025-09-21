package com.profile.searcher.model;

import com.profile.searcher.model.phantom.buster.LinkedInProfileExportAgentResponse;
import com.profile.searcher.model.phantom.buster.LinkedInProfileScrapResponse;
import com.profile.searcher.model.phantom.buster.PhantomLaunchResponse;
import com.profile.searcher.model.phantom.buster.PhantomPayload;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

public class ModelPojoTest {

    @Test
    void testPojoMethodsForModelClasses() {
        Class<?>[] classesUnderTest = {LinkedInProfileSearchDTO.class, LinkedInProfileScrapResponse.class,
                LinkedInProfileExportAgentResponse.class, PhantomLaunchResponse.class, PhantomLaunchResponse.class,
                PhantomPayload.PhantomBusterInput.class, PhantomPayload.class};
        for (Class<?> clazz : classesUnderTest) {
            assertPojoMethodsFor(clazz).testing(Method.CONSTRUCTOR, Method.GETTER, Method.SETTER)
                    .areWellImplemented();
        }
    }
}
