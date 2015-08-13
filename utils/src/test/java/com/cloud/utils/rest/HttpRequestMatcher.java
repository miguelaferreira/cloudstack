package com.cloud.utils.rest;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

final class HttpRequestMatcher extends BaseMatcher<HttpUriRequest> {

    public static HttpRequestMatcher matchesHttpRequest(final String method, final String payloadContent) {
        return new HttpRequestMatcher(method, payloadContent);
    }

    public static HttpRequestMatcher matchesHttpRequest(final String method) {
        return new HttpRequestMatcher(method, null);
    }

    private final String method;
    private final String payloadContent;

    private String actualMethod;
    private String actualPayloadContent;

    private HttpRequestMatcher(final String method, final String payloadContent) {
        this.method = method;
        this.payloadContent = payloadContent;
    }

    @Override
    public boolean matches(final Object item) {
        actualMethod = getMethod(item);
        if (item instanceof HttpEntityEnclosingRequest) {
            actualPayloadContent = getPayloadContent(item);
        }
        final boolean methodMatchCheck = actualMethod.equals(method);
        final boolean payloadContentMatchCheck = checkPayloadContent();
        System.err.println("methodMatchCheck = " + methodMatchCheck);
        System.err.println("payloadContentMatchCheck = " + payloadContentMatchCheck);
        return methodMatchCheck && payloadContentMatchCheck;
    }

    private static String getMethod(final Object item) {
        return ((HttpUriRequest) item).getMethod();
    }

    private static String getPayloadContent(final Object item) {
        try {
            return EntityUtils.toString(((HttpEntityEnclosingRequest) item).getEntity());
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkPayloadContent() {
        return payloadContent != null ? payloadContent.equals(actualPayloadContent) : true;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(createMessage());
    }

    private String createMessage() {
        final StringBuilder sb = new StringBuilder();
        sb.append("expected\nmethod = ").append(method).append(" and payload content = ").append(payloadContent);
        sb.append("\n    but got\nmethod = ").append(actualMethod).append(" and payload content = ").append(actualPayloadContent);
        return sb.toString();
    }

}