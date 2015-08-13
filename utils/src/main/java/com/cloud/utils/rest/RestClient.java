package com.cloud.utils.rest;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class RestClient {

    private static final Logger s_logger = Logger.getLogger(RestClient.class);

    private static final String CONTENT_TYPE = HttpConstants.CONTENT_TYPE;
    private static final String TEXT_HTML_CONTENT_TYPE = HttpConstants.TEXT_HTML_CONTENT_TYPE;

    private static final int BODY_RESP_MAX_LEN = 1024;

    private final CloseableHttpClient client;

    private int maxResponseErrorMesageLength = -1;

    public RestClient(final CloseableHttpClient client) {
        this.client = client;
    }

    public HttpEntity executeRequest(final HttpUriRequest request) throws CloudstackRESTException {
        CloseableHttpResponse response = null;
        try {
            s_logger.debug("Executig request: " + request);
            response = client.execute(request);
            s_logger.debug("Response status: " + response.getStatusLine().toString());

            final int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatusCodeHelper.isNotOk(statusCode)) {
                final String errorMessage = responseToErrorMessage(response);
                throw new CloudstackRESTException("Response to request was not success: " + errorMessage);
            }

            return response.getEntity();
        } catch (final IOException e) {
            throw new CloudstackRESTException("Could not execute request " + request, e);
        } finally {
            closeResponse(request, response);
        }
    }

    private String responseToErrorMessage(final CloseableHttpResponse response) {
        if (response.containsHeader(CONTENT_TYPE) && TEXT_HTML_CONTENT_TYPE.equals(response.getFirstHeader(CONTENT_TYPE).getValue())) {
            try {
                final HttpEntity entity = response.getEntity();
                final String respobnseBody = EntityUtils.toString(entity);
                return respobnseBody.subSequence(0, getMaxMessageLength()).toString();
            } catch (final IOException e) {
                s_logger.debug("Could not read repsonse body. Response: " + response, e);
            }
        }

        return response.getStatusLine().toString();
    }

    private int getMaxMessageLength() {
        return maxResponseErrorMesageLength != -1 ? maxResponseErrorMesageLength : BODY_RESP_MAX_LEN;
    }

    private static void closeResponse(final HttpUriRequest request, final CloseableHttpResponse response) throws CloudstackRESTException {
        if (response != null) {
            try {
                response.close();
            } catch (final IOException e) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Failed to close response object for request.\nReqest: ").append(request).append("\nResponse: ").append(response);
                throw new CloudstackRESTException(sb.toString(), e);
            }
        }
    }

    public void setMaxResponseErrorMesageLength(final int maxResponseErrorMesageLength) {
        this.maxResponseErrorMesageLength = maxResponseErrorMesageLength;
    }
}
