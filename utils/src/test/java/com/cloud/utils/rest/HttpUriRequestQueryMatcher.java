package com.cloud.utils.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class HttpUriRequestQueryMatcher extends FeatureMatcher<HttpUriRequest, String> {

    public static HttpUriRequestQueryMatcher hasQuery(final String query) {
        return new HttpUriRequestQueryMatcher(equalTo(query), "query", "query");
    }

    public static HttpUriRequestQueryMatcher containsSubQuery(final String query) {
        return new HttpUriRequestQueryMatcher(containsString(query), "query", "query");
    }

    public HttpUriRequestQueryMatcher(final Matcher<? super String> subMatcher, final String featureDescription, final String featureName) {
        super(subMatcher, featureDescription, featureName);
    }

    @Override
    protected String featureValueOf(final HttpUriRequest actual) {
        return actual.getURI().getQuery();
    }
}
