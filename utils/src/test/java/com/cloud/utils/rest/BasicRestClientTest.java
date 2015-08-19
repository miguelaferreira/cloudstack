package com.cloud.utils.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.apache.http.HttpHost;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicRestClientTest {

    private static final String LOCALHOST = "localhost";
    private static final String HTTPS = "HTTPS";

    private static final StatusLine HTTP_200_REPSONSE = new BasicStatusLine(new ProtocolVersion(HTTPS, 1, 1), 200, "OK");
    private static final StatusLine HTTP_503_STATUSLINE = new BasicStatusLine(new ProtocolVersion(HTTPS, 1, 1), 503, "Service unavailable");

    private static final CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

    private static CloseableHttpClient httpClient;
    private static HttpUriRequest request;

    @BeforeClass
    public static void setupClass() throws Exception {
        request = HttpUriRequestBuilder.create()
            .method(HttpMethod.GET)
            .path("/path")
            .build();
        httpClient = spy(HttpClientHelper.createHttpClient(2));
    }

    @Test
    public void testExecuteRequest() throws Exception {
        when(mockResponse.getStatusLine()).thenReturn(HTTP_200_REPSONSE);
        doReturn(mockResponse).when(httpClient).execute(any(HttpHost.class), HttpRequestMatcher.eq(request));
        final BasicRestClient restClient = BasicRestClient.create()
            .host(LOCALHOST)
            .client(httpClient)
            .build();

        final CloseableHttpResponse response = restClient.execute(request);

        assertThat(response, notNullValue());
        assertThat(response, sameInstance(mockResponse));
        assertThat(response.getStatusLine(), sameInstance(HTTP_200_REPSONSE));
    }

    @Test
    public void testExecuteRequestStatusCodeIsNotOk() throws Exception {
        when(mockResponse.getStatusLine()).thenReturn(HTTP_503_STATUSLINE);
        doReturn(mockResponse).when(httpClient).execute(any(HttpHost.class), HttpRequestMatcher.eq(request));
        final BasicRestClient restClient = BasicRestClient.create()
            .host(LOCALHOST)
            .client(httpClient)
            .build();

        final CloseableHttpResponse response = restClient.execute(request);

        assertThat(response, notNullValue());
        assertThat(response, sameInstance(mockResponse));
        assertThat(response.getStatusLine(), sameInstance(HTTP_503_STATUSLINE));
    }

    @Test(expected = CloudstackRESTException.class)
    public void testExecuteRequestWhenClientThrowsIOException() throws Exception {
        final BasicRestClient restClient = BasicRestClient.create()
            .host(LOCALHOST)
            .client(HttpClientHelper.createHttpClient(5))
            .build();

        restClient.execute(request);
    }

}
