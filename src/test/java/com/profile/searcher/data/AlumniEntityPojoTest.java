package com.profile.searcher.data;

import com.profile.searcher.entity.AlumniEntity;
import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

public class AlumniEntityPojoTest {

    @Test
    void testPojoMethodsForAlumniEntity() {
        Class<?> classUnderTest = AlumniEntity.class;
        assertPojoMethodsFor(classUnderTest).testing(Method.CONSTRUCTOR, Method.GETTER, Method.SETTER)
                .areWellImplemented();
    }
}
