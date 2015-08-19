package com.cloud.utils.rest;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

public class BasicRestClient implements RestClient {

    private static final Logger s_logger = Logger.getLogger(BasicRestClient.class);

    private static final String HTTPS = HttpConstants.HTTPS;
    private static final int HTTPS_PORT = HttpConstants.HTTPS_PORT;

    private final CloseableHttpClient client;
    private HttpHost host;

    private BasicRestClient(final Builder<?> builder) {
        client = builder.client;
        host = buildHttpHost(builder.host);
    }

    protected BasicRestClient(final CloseableHttpClient client, final String host) {
        this.client = client;
        this.host = buildHttpHost(host);
    }

    public void setHots(final String host) {
        this.host = buildHttpHost(host);
    }

    private static HttpHost buildHttpHost(final String host) {
        return new HttpHost(host, HTTPS_PORT, HTTPS);
    }

    @SuppressWarnings("rawtypes")
    public static Builder create() {
        return new Builder();
    }

    @Override
    public CloseableHttpResponse execute(final HttpUriRequest request) throws CloudstackRESTException {
        s_logger.debug("Executig request: " + request);
        try {
            return client.execute(host, request);
        } catch (final IOException e) {
            throw new CloudstackRESTException("Could not execute request " + request, e);
        }
    }

    @Override
    public void closeResponse(final HttpUriRequest request, final CloseableHttpResponse response) throws CloudstackRESTException {
        try {
            response.close();
        } catch (final IOException e) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to close response object for request.\nReqest: ").append(request).append("\nResponse: ").append(response);
            throw new CloudstackRESTException(sb.toString(), e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static class Builder<T extends Builder> {
        private CloseableHttpClient client;
        private String host;

        public T client(final CloseableHttpClient client) {
            this.client = client;
            return (T) this;
        }

        public T host(final String host) {
            this.host = host;
            return (T) this;
        }

        public BasicRestClient build() {
            return new BasicRestClient(this);
        }
    }

}
