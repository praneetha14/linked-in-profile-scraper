package com.profile.searcher.model.response;

import lombok.Data;

@Data
public class SuccessResponseVO<T> {
    private final String message;
    private final T data;


    public static <T> SuccessResponseVO<T> of(String message, T data) {
        return new SuccessResponseVO<T>(message, data);
    }
}
