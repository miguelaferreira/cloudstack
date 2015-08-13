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
import static org.mockito.Mockito.withSettings;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RESTServiceConnectorTest {
    final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    @Test(expected = IllegalArgumentException.class)
    public void testBuildConnectorWithNullHostname() {
        new RESTServiceConnector.Builder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildConnectorWithEmptyHostname() {
        new RESTServiceConnector.Builder().host("").build();
    }

    @Test
    public void testExecuteUpdateObject() throws Exception {
        final TestPojo newObject = new TestPojo();
        newObject.setField("newValue");
        final String newObjectJson = gson.toJson(newObject);
        final RestClient client = mock(RestClient.class, withSettings().verboseLogging());
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().host("localhost").client(client).build();

        connector.executeUpdateObject(newObject, "/somepath");
        verify(client).executeRequest(argThat(HttpRequestMatcher.matchesHttpRequest("PUT", newObjectJson)));
    }

    @Test
    public void testExecuteCreateObject() throws Exception {
        final TestPojo newObject = new TestPojo();
        newObject.setField("newValue");
        final String newObjectJson = gson.toJson(newObject);
        final HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(newObjectJson.getBytes(StandardCharsets.UTF_8)));
        final RestClient client = mock(RestClient.class, withSettings().verboseLogging());
        when(client.executeRequest((HttpUriRequest) any())).thenReturn(entity);
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().host("localhost").client(client).build();

        connector.executeCreateObject(newObject, "/somepath");
        verify(client).executeRequest(argThat(HttpRequestMatcher.matchesHttpRequest("POST", newObjectJson)));
    }

    @Test
    public void testExecuteDeleteObject() throws Exception {
        final HttpEntity entity = mock(HttpEntity.class);
        final RestClient client = mock(RestClient.class, withSettings().verboseLogging());
        when(client.executeRequest((HttpUriRequest) any())).thenReturn(entity);
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().host("localhost").client(client).build();

        connector.executeDeleteObject("/somepath");
        verify(client).executeRequest(argThat(HttpRequestMatcher.matchesHttpRequest("DELETE")));
    }

    @Test
    public void testExecuteRetrieveObject() throws Exception {
        final HttpEntity entity = mock(HttpEntity.class);
        final RestClient client = mock(RestClient.class, withSettings().verboseLogging());
        when(client.executeRequest((HttpUriRequest) any())).thenReturn(entity);
        final RESTServiceConnector connector = new RESTServiceConnector.Builder().host("localhost").client(client).build();

        connector.executeRetrieveObject(TestPojo.class, "/somepath");
        verify(client).executeRequest(argThat(HttpRequestMatcher.matchesHttpRequest("GET")));
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
