package com.cloud.network.nicira;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.cloud.utils.rest.BasicRestClient;
import com.cloud.utils.rest.CloudstackRESTException;
import com.cloud.utils.rest.ExecutionCounter;
import com.cloud.utils.rest.HttpConstants;
import com.cloud.utils.rest.HttpMethod;
import com.cloud.utils.rest.HttpStatusCodeHelper;
import com.cloud.utils.rest.HttpUriRequestBuilder;

public class NiciraRestClient extends BasicRestClient {

    private static final Logger s_logger = Logger.getLogger(NiciraRestClient.class);

    private static final String CONTENT_TYPE = HttpConstants.CONTENT_TYPE;
    private static final String TEXT_HTML_CONTENT_TYPE = HttpConstants.TEXT_HTML_CONTENT_TYPE;

    private static final int DEFAULT_BODY_RESP_MAX_LEN = 1024;
    private static final int DEFAULT_EXECUTION_LIMIT = 5;

    private final ExecutionCounter counter;
    private final int maxResponseErrorMesageLength;
    private final int executionLimit;

    private final String username;
    private final String password;
    private final String loginUrl;

    private NiciraRestClient(final Builder builder) {
        super(builder.client, builder.hostname);
        executionLimit = builder.executionLimit;
        counter = new ExecutionCounter(executionLimit);
        maxResponseErrorMesageLength = builder.maxResponseErrorMesageLength;
        username = builder.username;
        password = builder.password;
        loginUrl = builder.loginUrl;
    }

    public static Builder create() {
        return new Builder();
    }

    @Override
    public CloseableHttpResponse execute(final HttpUriRequest request) throws CloudstackRESTException {
        return execute(request, 0);
    }

    private CloseableHttpResponse execute(final HttpUriRequest request, final int previousStatusCode) throws CloudstackRESTException {
        if (counter.hasReachedExecutionLimit()) {
            throw new CloudstackRESTException("Reached max executions limit of " + executionLimit);
        }
        counter.incrementExecutionCounter();
        s_logger.debug("Executing request " + request + " [execution count = " + counter.getValue() + "]");
        final CloseableHttpResponse response = super.execute(request);

        final int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatusCodeHelper.isUnauthorized(statusCode)) {
            if (HttpStatusCodeHelper.isUnauthorized(previousStatusCode)) {
                s_logger.error(responseToErrorMessage(response));
                throw new CloudstackRESTException("Failed to authenticate against REST server");
            }
            final HttpUriRequest authenticateRequest = createAuthenticationRequest();
            final CloseableHttpResponse loginResponse = execute(authenticateRequest, statusCode);
            return execute(request, loginResponse.getStatusLine().getStatusCode());
        } else if (HttpStatusCodeHelper.isRedirect(statusCode)) {
            if (HttpStatusCodeHelper.isRedirect(previousStatusCode)) {
                s_logger.warn("Got two consecutive redirects for request: " + request);
            }
            redirectClientToNewHost(response);
            return execute(request, statusCode);
        } else if (HttpStatusCodeHelper.isOk(statusCode)) {
            s_logger.info("Successfuly executed request: " + request);
            counter.resetExecutionCounter();
            return response;
        } else {
            throw new CloudstackRESTException("Unexpecetd status code: " + statusCode);
        }
    }

    private void redirectClientToNewHost(final CloseableHttpResponse response) {
        final Header header = response.getFirstHeader("location");
        final String newHost = URI.create(header.getValue()).getHost();
        super.setHots(newHost);
    }

    private HttpUriRequest createAuthenticationRequest() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        return HttpUriRequestBuilder.create()
            .method(HttpMethod.POST)
            .parameters(parameters)
            .path(loginUrl)
            .build();
    }

    private String responseToErrorMessage(final CloseableHttpResponse response) {
        String errorMessage = response.getStatusLine().toString();
        if (response.containsHeader(CONTENT_TYPE) && TEXT_HTML_CONTENT_TYPE.equals(response.getFirstHeader(CONTENT_TYPE).getValue())) {
            try {
                final HttpEntity entity = response.getEntity();
                final String respobnseBody = EntityUtils.toString(entity);
                errorMessage = respobnseBody.subSequence(0, maxResponseErrorMesageLength).toString();
            } catch (final IOException e) {
                s_logger.debug("Could not read repsonse body. Response: " + response, e);
            }
        }

        return errorMessage;
    }

    protected static class Builder extends BasicRestClient.Builder<Builder> {
        private CloseableHttpClient client;
        private String hostname;
        private String username;
        private String password;
        private String loginUrl;
        private int executionLimit = DEFAULT_EXECUTION_LIMIT;
        private int maxResponseErrorMesageLength = DEFAULT_BODY_RESP_MAX_LEN;

        public Builder hostname(final String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder loginUrl(final String loginUrl) {
            this.loginUrl = loginUrl;
            return this;
        }

        @Override
        public Builder client(final CloseableHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder executionLimit(final int executionLimit) {
            this.executionLimit = executionLimit;
            return this;
        }

        public Builder maxResponseErrorMesageLength(final int maxResponseErrorMesageLength) {
            this.maxResponseErrorMesageLength = maxResponseErrorMesageLength;
            return this;
        }

        @Override
        public NiciraRestClient build() {
            return new NiciraRestClient(this);
        }

    }
}