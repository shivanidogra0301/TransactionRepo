package com.example.transactions.exceptions;

import jakarta.ws.rs.core.Response;

public class ApiException extends RuntimeException {

    private final int code;

    public ApiException(int code, String detail) {
        super(detail);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return titleFor(code);
    }

    public static String titleFor(int code) {
        Response.Status status = Response.Status.fromStatusCode(code);
        return status != null ? status.name() : "ERROR";
    }
}
