package com.cloud.utils.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;

public class HttpStatusCodeHelperTest {
    @Test
    public void testIsRedirect() throws Exception {
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_MULTIPLE_CHOICES), equalTo(true));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_MOVED_PERMANENTLY), equalTo(true));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_MOVED_TEMPORARILY), equalTo(true));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_SEE_OTHER), equalTo(true));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_NOT_MODIFIED), equalTo(true));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_TEMPORARY_REDIRECT), equalTo(true));
    }

    @Test
    public void testItIsNotRedirect() throws Exception {
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_RESET_CONTENT), equalTo(false));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_PARTIAL_CONTENT), equalTo(false));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_MULTI_STATUS), equalTo(false));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_BAD_REQUEST), equalTo(false));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_UNAUTHORIZED), equalTo(false));
        assertThat(HttpStatusCodeHelper.isRedirect(HttpStatus.SC_PAYMENT_REQUIRED), equalTo(false));
    }

    @Test
    public void testIsUnauthorized() throws Exception {
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_UNAUTHORIZED), equalTo(true));
    }

    @Test
    public void testItIsNotUnauthorized() throws Exception {
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_PARTIAL_CONTENT), equalTo(false));
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_MULTI_STATUS), equalTo(false));
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_BAD_REQUEST), equalTo(false));
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_PAYMENT_REQUIRED), equalTo(false));
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_FORBIDDEN), equalTo(false));
    }

    @Test
    public void testIsOk() throws Exception {
        assertThat(HttpStatusCodeHelper.isOk(HttpStatus.SC_OK), equalTo(true));
    }

    @Test
    public void testIsNotOk() throws Exception {
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_PARTIAL_CONTENT), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_MULTI_STATUS), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_BAD_REQUEST), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_PAYMENT_REQUIRED), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_FORBIDDEN), equalTo(true));
    }
}
