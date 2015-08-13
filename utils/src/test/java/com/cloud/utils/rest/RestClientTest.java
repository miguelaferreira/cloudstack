package com.cloud.utils.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

public class RestClientTest {

    @Test
    public void testExecuteRequest() throws Exception {
        final HttpUriRequest request = new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("/path").build();
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTPS", 1, 1), 200, "OK"));
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpUriRequest) any())).thenReturn(response);
        final RestClient restHttpClient = new RestClient(httpClient);

        final HttpEntity entity = restHttpClient.executeRequest(request);

        assertThat(entity, nullValue());
        verify(response).close();
    }

    @Test(expected = CloudstackRESTException.class)
    public void testExecuteRequestStatusCodeIsNotOk() throws Exception {
        final HttpUriRequest request = new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("/path").build();
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTPS", 1, 1), 503, "Service unavailable"));
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpUriRequest) any())).thenReturn(response);
        final RestClient restHttpClient = new RestClient(httpClient);

        final HttpEntity entity = restHttpClient.executeRequest(request);

        assertThat(entity, nullValue());
        verify(response).close();
    }

    @Test(expected = CloudstackRESTException.class)
    public void testExecuteRequestWhenClientThrowsIOException() throws Exception {
        final HttpUriRequest request = new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("/path").build();
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpUriRequest) any())).thenThrow(new IOException());
        new RestClient(httpClient).executeRequest(request);
    }

    @Test
    public void testExecuteRequestStatusCodeIsNotOkAndResponseMessageIsOverTheLimit() throws Exception {
        final HttpUriRequest request = new RestRequestBuilder().method(HttpMethod.GET).host("localhost").path("/path").build();
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTPS", 1, 1), 503, "Service unavailable"));
        when(response.containsHeader(HttpConstants.CONTENT_TYPE)).thenReturn(true);
        when(response.getFirstHeader(HttpConstants.CONTENT_TYPE)).thenReturn(new BasicHeader(HttpConstants.CONTENT_TYPE, HttpConstants.TEXT_HTML_CONTENT_TYPE));
        when(response.getEntity()).thenReturn(new ByteArrayEntity("Some very long string!".getBytes()));
        doNothing().when(response).close();
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpUriRequest) any())).thenReturn(response);
        final RestClient restHttpClient = new RestClient(httpClient);
        restHttpClient.setMaxResponseErrorMesageLength(5);

        try {
            restHttpClient.executeRequest(request);
            fail("Expected CloudstackRESTException exception");
        } catch (final CloudstackRESTException e) {
            assertThat(e.getMessage(), endsWith("Some "));
        }
    }
}
