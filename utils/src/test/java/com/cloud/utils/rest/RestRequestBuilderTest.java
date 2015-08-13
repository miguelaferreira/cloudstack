package com.cloud.utils.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import com.google.common.base.Optional;

public class RestRequestBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithNullMethod() throws Exception {
        new RestRequestBuilder().host("localhost").path("/path").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithNullHost() throws Exception {
        new RestRequestBuilder().method(HttpMethod.GET).path("/path").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithEmptyHost() throws Exception {
        new RestRequestBuilder().method(HttpMethod.GET).host("").path("/path").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithNullPath() throws Exception {
        new RestRequestBuilder().method(HttpMethod.GET).host("localhost").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithEmptyPath() throws Exception {
        new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithIlegalPath() throws Exception {
        new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("path").build();
    }

    @Test
    public void testBuildSimpleRequest() throws Exception {
        final HttpUriRequest request = new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("/path").build();

        assertThat(request, notNullValue());
        assertThat(request.getURI().toString(), equalTo("https://localhost/path"));
        assertThat(request.getMethod(), equalTo(HttpGet.METHOD_NAME));
    }

    @Test
    public void testBuildRequestWithParameters() throws Exception {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("key1", "value1");
        final HttpUriRequest request = new RestRequestBuilder()
        .method(HttpMethod.GET)
        .host("localhost")
        .path("/path")
        .parameters(parameters)
        .build();

        assertThat(request, notNullValue());
        assertThat(request.getURI().toString(), equalTo("https://localhost/path?key1=value1"));
        assertThat(request.getMethod(), equalTo(HttpGet.METHOD_NAME));
    }

    @Test
    public void testBuildRequestWithJsonPayload() throws Exception {
        final HttpUriRequest request = new RestRequestBuilder()
        .method(HttpMethod.GET)
        .host("localhost")
        .path("/path")
        .jsonPayload(Optional.of("{'key1':'value1'}"))
        .build();

        assertThat(request, notNullValue());
        assertThat(request.getURI().toString(), equalTo("https://localhost/path"));
        assertThat(request.getMethod(), equalTo(HttpGet.METHOD_NAME));
        assertThat(request.containsHeader(HttpConstants.CONTENT_TYPE), equalTo(true));
        assertThat(request.getFirstHeader(HttpConstants.CONTENT_TYPE).getValue(), equalTo(HttpConstants.JSON_CONTENT_TYPE));
    }

}
