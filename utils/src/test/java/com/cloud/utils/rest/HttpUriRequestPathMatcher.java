package com.cloud.utils.rest;

import static org.hamcrest.Matchers.equalTo;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class HttpUriRequestPathMatcher extends FeatureMatcher<HttpUriRequest, String> {

    public static HttpUriRequestPathMatcher hasPath(final String path) {
        return new HttpUriRequestPathMatcher(equalTo(path), "path", "path");
    }

    public HttpUriRequestPathMatcher(final Matcher<? super String> subMatcher, final String featureDescription, final String featureName) {
        super(subMatcher, featureDescription, featureName);
    }

    @Override
    protected String featureValueOf(final HttpUriRequest actual) {
        return actual.getURI().getPath();
    }
}
