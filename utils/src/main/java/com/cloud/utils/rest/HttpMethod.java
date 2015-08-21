package com.cloud.utils.rest;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public enum HttpMethod {

    GET(HttpGet.METHOD_NAME), POST(HttpPost.METHOD_NAME), PUT(HttpPut.METHOD_NAME), DELETE(HttpDelete.METHOD_NAME);

    private final String name;

    private HttpMethod(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
