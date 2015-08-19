package com.cloud.utils.rest;

import static org.hamcrest.Matchers.equalTo;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class HttpUriRequestMethodMatcher extends FeatureMatcher<HttpUriRequest, String> {

    public static HttpUriRequestMethodMatcher hasMethod(final String method) {
        return new HttpUriRequestMethodMatcher(equalTo(method), "method", "method");
    }

    public HttpUriRequestMethodMatcher(final Matcher<? super String> subMatcher, final String featureDescription, final String featureName) {
        super(subMatcher, featureDescription, featureName);
    }

    @Override
    protected String featureValueOf(final HttpUriRequest actual) {
        return actual.getMethod();
    }

}
