package com.cloud.utils.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.argThat;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class HttpUriRequestQueryMatcher extends FeatureMatcher<HttpUriRequest, String> {

    public static HttpUriRequest hasQuery(final String query) {
        return argThat(new HttpUriRequestQueryMatcher(equalTo(query), "query", "query"));
    }

    public static HttpUriRequest containsSubQuery(final String query) {
        return argThat(new HttpUriRequestQueryMatcher(containsString(query), "query", "query"));
    }

    public HttpUriRequestQueryMatcher(final Matcher<? super String> subMatcher, final String featureDescription, final String featureName) {
        super(subMatcher, featureDescription, featureName);
    }

    @Override
    protected String featureValueOf(final HttpUriRequest actual) {
        return actual.getURI().getQuery();
    }
}
