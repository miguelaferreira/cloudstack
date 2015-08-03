package com.cloud.utils.rest;

import org.apache.commons.httpclient.HttpStatus;

public class HttpStatusCodeHelper {
    public static boolean isRedirect(final int statusCode) {
        return statusCode >= HttpStatus.SC_MULTIPLE_CHOICES && statusCode <= HttpStatus.SC_TEMPORARY_REDIRECT;
    }

    public static boolean isUnauthorized(final int statusCode) {
        return statusCode == HttpStatus.SC_UNAUTHORIZED;
    }

    public static boolean isOk(final int statusCode) {
        return statusCode == HttpStatus.SC_OK;
    }

    public static boolean isNotOk(final int statusCode) {
        return !isOk(statusCode);
    }
}
