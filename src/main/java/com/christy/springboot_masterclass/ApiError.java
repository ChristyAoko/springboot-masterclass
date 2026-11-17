package com.christy.springboot_masterclass;

import java.util.Map;

public class ApiError {

    public int status;
    public String message;
    public Map<String, String> errors;

    // Constructor for general errors, such as 404 Not Found
    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.errors = null;
    }

    // Constructor for validation errors
    public ApiError(int status, String message,
                    Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}