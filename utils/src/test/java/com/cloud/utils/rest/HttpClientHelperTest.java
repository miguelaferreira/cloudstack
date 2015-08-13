package com.cloud.utils.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

public class HttpClientHelperTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClientWithEmptyUsername() throws Exception {
        HttpClientHelper.createHttpClient("", "pass", 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClientWithNullUsername() throws Exception {
        HttpClientHelper.createHttpClient(null, "pass", 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClientWithEmptyPassword() throws Exception {
        HttpClientHelper.createHttpClient("user", "", 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClientWithNullPassword() throws Exception {
        HttpClientHelper.createHttpClient("user", null, 5);
    }

    @Test
    public void testCreateClient() throws Exception {
        final CloseableHttpClient client = HttpClientHelper.createHttpClient("user", "pass", 5);

        assertThat(client, notNullValue());
    }

}
