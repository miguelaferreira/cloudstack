package com.cloud.utils.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;

public class HttpStatusCodeHelperTest {
    @Test
    public void testIsUnauthorized() throws Exception {
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_TEMPORARY_REDIRECT), equalTo(false));
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_BAD_REQUEST), equalTo(false));

        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_UNAUTHORIZED), equalTo(true));

        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_PAYMENT_REQUIRED), equalTo(false));
        assertThat(HttpStatusCodeHelper.isUnauthorized(HttpStatus.SC_FORBIDDEN), equalTo(false));
    }

    @Test
    public void testIsOk() throws Exception {
        assertThat(HttpStatusCodeHelper.isOk(HttpStatus.SC_SWITCHING_PROTOCOLS), equalTo(false));
        assertThat(HttpStatusCodeHelper.isOk(HttpStatus.SC_PROCESSING), equalTo(false));

        assertThat(HttpStatusCodeHelper.isOk(HttpStatus.SC_OK), equalTo(true));

        assertThat(HttpStatusCodeHelper.isOk(HttpStatus.SC_CREATED), equalTo(false));
        assertThat(HttpStatusCodeHelper.isOk(HttpStatus.SC_ACCEPTED), equalTo(false));
    }

    @Test
    public void testIsNotOk() throws Exception {
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_SWITCHING_PROTOCOLS), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_PROCESSING), equalTo(true));

        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_OK), equalTo(false));

        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_CREATED), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotOk(HttpStatus.SC_ACCEPTED), equalTo(true));
    }

    @Test
    public void testIsCreated() throws Exception {
        assertThat(HttpStatusCodeHelper.isCreated(HttpStatus.SC_PROCESSING), equalTo(false));
        assertThat(HttpStatusCodeHelper.isCreated(HttpStatus.SC_OK), equalTo(false));

        assertThat(HttpStatusCodeHelper.isCreated(HttpStatus.SC_CREATED), equalTo(true));

        assertThat(HttpStatusCodeHelper.isCreated(HttpStatus.SC_ACCEPTED), equalTo(false));
        assertThat(HttpStatusCodeHelper.isCreated(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION), equalTo(false));
    }

    @Test
    public void testIsNotCreated() throws Exception {
        assertThat(HttpStatusCodeHelper.isNotCreated(HttpStatus.SC_PROCESSING), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotCreated(HttpStatus.SC_OK), equalTo(true));

        assertThat(HttpStatusCodeHelper.isNotCreated(HttpStatus.SC_CREATED), equalTo(false));

        assertThat(HttpStatusCodeHelper.isNotCreated(HttpStatus.SC_ACCEPTED), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotCreated(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION), equalTo(true));
    }

    @Test
    public void testIsNoContent() throws Exception {
        assertThat(HttpStatusCodeHelper.isNoContent(HttpStatus.SC_ACCEPTED), equalTo(false));
        assertThat(HttpStatusCodeHelper.isNoContent(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION), equalTo(false));

        assertThat(HttpStatusCodeHelper.isNoContent(HttpStatus.SC_NO_CONTENT), equalTo(true));

        assertThat(HttpStatusCodeHelper.isNoContent(HttpStatus.SC_RESET_CONTENT), equalTo(false));
        assertThat(HttpStatusCodeHelper.isNoContent(HttpStatus.SC_PARTIAL_CONTENT), equalTo(false));
    }

    @Test
    public void testIsNotNoContent() throws Exception {
        assertThat(HttpStatusCodeHelper.isNotNoContent(HttpStatus.SC_ACCEPTED), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotNoContent(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION), equalTo(true));

        assertThat(HttpStatusCodeHelper.isNotNoContent(HttpStatus.SC_NO_CONTENT), equalTo(false));

        assertThat(HttpStatusCodeHelper.isNotNoContent(HttpStatus.SC_RESET_CONTENT), equalTo(true));
        assertThat(HttpStatusCodeHelper.isNotNoContent(HttpStatus.SC_PARTIAL_CONTENT), equalTo(true));
    }
}
