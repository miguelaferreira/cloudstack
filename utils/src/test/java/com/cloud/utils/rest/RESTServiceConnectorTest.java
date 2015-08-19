//
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//

package com.cloud.utils.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RESTServiceConnectorTest {
    private static final BasicStatusLine HTTP_200_STATUS_LINE = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private static final Map<String, String> DEFAULT_TEST_PARAMETERS = new HashMap<String, String>();
    static {
        DEFAULT_TEST_PARAMETERS.put("agr1", "val1");
        DEFAULT_TEST_PARAMETERS.put("agr2", "val2");
    }

    @Test
    public void testExecuteUpdateObject() throws Exception {
        final TestPojo newObject = new TestPojo();
        newObject.setField("newValue");
        final String newObjectJson = gson.toJson(newObject);
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeUpdateObject(newObject, "/somepath");

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("PUT")));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestPayloadMatcher.hasPayload(newObjectJson)));
    }

    @Test
    public void testExecuteUpdateObjectWithParameters() throws Exception {
        final TestPojo newObject = new TestPojo();
        newObject.setField("newValue");
        final String newObjectJson = gson.toJson(newObject);
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");

        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeUpdateObject(newObject, "/somepath", DEFAULT_TEST_PARAMETERS);

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("PUT")));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestPayloadMatcher.hasPayload(newObjectJson)));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestQueryMatcher.hasQuery("agr2=val2&agr1=val1")));
    }

    @Test
    public void testExecuteCreateObject() throws Exception {
        final TestPojo newObject = new TestPojo();
        newObject.setField("newValue");
        final String newObjectJson = gson.toJson(newObject);
        final HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(newObjectJson.getBytes(StandardCharsets.UTF_8)));
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeCreateObject(newObject, "/somepath");

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("POST")));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestPayloadMatcher.hasPayload(newObjectJson)));
        verify(response).close();
    }

    @Test
    public void testExecuteCreateObjectWithParameters() throws Exception {
        final TestPojo newObject = new TestPojo();
        newObject.setField("newValue");
        final String newObjectJson = gson.toJson(newObject);
        final HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(newObjectJson.getBytes(StandardCharsets.UTF_8)));
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeCreateObject(newObject, "/somepath", DEFAULT_TEST_PARAMETERS);

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("POST")));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestPayloadMatcher.hasPayload(newObjectJson)));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestQueryMatcher.hasQuery("agr2=val2&agr1=val1")));
        verify(response).close();
    }

    @Test
    public void testExecuteDeleteObject() throws Exception {
        final HttpEntity entity = mock(HttpEntity.class);
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeDeleteObject("/somepath");

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("DELETE")));
        verify(response).close();
    }

    @Test
    public void testExecuteRetrieveObject() throws Exception {
        final HttpEntity entity = mock(HttpEntity.class);
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeRetrieveObject(TestPojo.class, "/somepath");

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("GET")));
        verify(response).close();
    }

    @Test
    public void testExecuteRetrieveObjectWithParameters() throws Exception {
        final HttpEntity entity = mock(HttpEntity.class);
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(HTTP_200_STATUS_LINE);
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute((HttpHost) any(), (HttpUriRequest) any())).thenReturn(response);
        final RestClient restClient = new BasicRestClient(httpClient, "localhost");
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().client(restClient).build();

        connector.executeRetrieveObject(TestPojo.class, "/somepath", DEFAULT_TEST_PARAMETERS);

        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestMethodMatcher.hasMethod("GET")));
        verify(httpClient).execute((HttpHost) any(), argThat(HttpUriRequestQueryMatcher.hasQuery("agr2=val2&agr1=val1")));
        verify(response).close();
    }

    class TestPojo {
        private String field;

        public String getField() {
            return field;
        }

        public void setField(final String field) {
            this.field = field;
        }

    }
}
