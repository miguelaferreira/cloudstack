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

package com.cloud.network.nicira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.http.HttpHost;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cloud.utils.rest.HttpClientHelper;

public class NiciraNvpApiTest {
    private static final StatusLine HTTP_200_REPSONSE = new BasicStatusLine(new ProtocolVersion("HTTPS", 1, 1), HttpStatus.SC_OK, "OK");

    protected static final String UUID = "aaaa";
    protected static final String UUID2 = "bbbb";
    protected static final String UUID_SEC_PROFILE_URI = NiciraConstants.SEC_PROFILE_URI_PREFIX + "/aaaa";
    protected static final String SCHEMA = "myTestSchema";
    protected static final String SCHEMA2 = "myTestSchema2";
    protected static final String HREF = "myTestHref";
    protected static final String HREF2 = "myTestHref2";
    protected static final String SEC_PROFILE_JSON_RESPONSE =
                    "{\"uuid\" : \"aaaa\","
                                    + "\"display_name\" : \"myTestName\","
                                    + "\"href\" : \"myTestHref\","
                                    + "\"schema\" : \"myTestSchema\"}";

    protected static final String SEC_PROFILE_LIST_JSON_RESPONSE = "{\"results\" : [{\"uuid\" : \"aaaa\","
                    + "\"display_name\" : \"myTestName\","
                    + "\"href\" : \"myTestHref\","
                    + "\"schema\" : \"myTestSchema\"},"
                    + "{ \"uuid\" : \"bbbb\","
                    + "\"display_name\" : \"myTestName2\","
                    + "\"href\" : \"myTestHref2\","
                    + "\"schema\" : \"myTestSchema2\"}],"
                    + "\"result_count\": 2}";

    HttpClient client = mock(HttpClient.class);
    HttpMethod method;
    String type;
    String uri;

    @Before
    public void setUp() throws Exception {
        final HttpClientParams hmp = mock(HttpClientParams.class);
        when(client.getParams()).thenReturn(hmp);
    }

    private static NiciraNvpApi buildApi(final CloseableHttpClient httpClient) {
        return NiciraNvpApi.create()
            .host("localhost")
            .username("admin")
            .password("adminpassword")
            .httpClient(httpClient)
            .build();
    }

    @Test
    public void testFindSecurityProfile() throws Exception {
        final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(HTTP_200_REPSONSE);
        when(response.getEntity()).thenReturn(new StringEntity(SEC_PROFILE_LIST_JSON_RESPONSE));
        final CloseableHttpClient httpClient = spy(HttpClientHelper.createHttpClient(2));
        doReturn(response).when(httpClient).execute((HttpHost) any(), (HttpUriRequest) any());
        final NiciraNvpApi api = buildApi(httpClient);

        when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(method.getResponseBodyAsString()).thenReturn(SEC_PROFILE_LIST_JSON_RESPONSE);
        final NameValuePair[] queryString = new NameValuePair[] { new NameValuePair("fields", "*") };

        // Execute
        final NiciraNvpList<SecurityProfile> actualProfiles = api.findSecurityProfile();

        // Assert
        verify(response, times(1)).close();
        verify(method, times(1)).setQueryString(queryString);
        assertEquals("Wrong Uuid in the newly created SecurityProfile", UUID, actualProfiles.getResults().get(0).getUuid());
        assertEquals("Wrong Uuid in the newly created SecurityProfile", HREF, actualProfiles.getResults().get(0).getHref());
        assertEquals("Wrong Schema in the newly created SecurityProfile", SCHEMA, actualProfiles.getResults().get(0).getSchema());
        assertEquals("Wrong Uuid in the newly created SecurityProfile", UUID2, actualProfiles.getResults().get(1).getUuid());
        assertEquals("Wrong Uuid in the newly created SecurityProfile", HREF2, actualProfiles.getResults().get(1).getHref());
        assertEquals("Wrong Schema in the newly created SecurityProfile", SCHEMA2, actualProfiles.getResults().get(1).getSchema());
        assertEquals("Wrong Schema in the newly created SecurityProfile", 2, actualProfiles.getResultCount());
        assertEquals("Wrong URI for SecurityProfile creation REST service", NiciraConstants.SEC_PROFILE_URI_PREFIX, uri);
        assertEquals("Wrong URI for SecurityProfile creation REST service", com.cloud.utils.rest.HttpMethod.GET, type);
    }

    @Test
    public void testFindSecurityProfileByUuid() throws NiciraNvpApiException, IOException {
        // Prepare
        method = mock(GetMethod.class);
        when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(method.getResponseBodyAsString()).thenReturn(SEC_PROFILE_LIST_JSON_RESPONSE);
        final NameValuePair[] queryString = new NameValuePair[] {
                        new NameValuePair("uuid", UUID),
                        new NameValuePair("fields", "*")
        };
        final List<NameValuePair> queryStringNvps = new ArrayList<>();
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final NameValuePair[] arguments = (NameValuePair[]) invocation.getArguments()[0];
                queryStringNvps.addAll(Arrays.asList(arguments));
                return null;
            }
        }).when(method).setQueryString(any(NameValuePair[].class));
        final NiciraNvpApi api = buildApi(httpClient);

        // Execute
        final NiciraNvpList<SecurityProfile> actualProfiles = api.findSecurityProfile(UUID);

        // Assert
        verify(method, times(1)).releaseConnection();
        assertTrue(queryStringNvps.containsAll(Arrays.asList(queryString)));
        assertEquals(queryString.length, queryStringNvps.size());
        assertEquals("Wrong Uuid in the newly created SecurityProfile",
                        UUID, actualProfiles.getResults().get(0).getUuid());
        assertEquals("Wrong Uuid in the newly created SecurityProfile",
                        HREF, actualProfiles.getResults().get(0).getHref());
        assertEquals("Wrong Schema in the newly created SecurityProfile",
                        SCHEMA, actualProfiles.getResults().get(0).getSchema());
        assertEquals("Wrong Uuid in the newly created SecurityProfile",
                        UUID2, actualProfiles.getResults().get(1).getUuid());
        assertEquals("Wrong Uuid in the newly created SecurityProfile",
                        HREF2, actualProfiles.getResults().get(1).getHref());
        assertEquals("Wrong Schema in the newly created SecurityProfile",
                        SCHEMA2, actualProfiles.getResults().get(1).getSchema());
        assertEquals("Wrong Schema in the newly created SecurityProfile",
                        2, actualProfiles.getResultCount());
        assertEquals("Wrong URI for SecurityProfile creation REST service",
                        NiciraConstants.SEC_PROFILE_URI_PREFIX, uri);
        assertEquals("Wrong HTTP method for SecurityProfile creation REST service",
                        com.cloud.utils.rest.HttpMethod.GET, type);
    }

    @Test
    public void testCreateSecurityProfile() throws NiciraNvpApiException, IOException {
        // Prepare
        final SecurityProfile inputSecProfile = new SecurityProfile();
        method = mock(PostMethod.class);
        when(method.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
        when(method.getResponseBodyAsString()).thenReturn(SEC_PROFILE_JSON_RESPONSE);

        // Execute
        final SecurityProfile actualSecProfile = api.createSecurityProfile(inputSecProfile);

        // Assert
        verify(method, times(1)).releaseConnection();
        assertEquals("Wrong Uuid in the newly created SecurityProfile",
                        UUID, actualSecProfile.getUuid());
        assertEquals("Wrong Uuid in the newly created SecurityProfile",
                        HREF, actualSecProfile.getHref());
        assertEquals("Wrong Schema in the newly created SecurityProfile",
                        SCHEMA, actualSecProfile.getSchema());
        assertEquals("Wrong URI for SecurityProfile creation REST service",
                        NiciraConstants.SEC_PROFILE_URI_PREFIX, uri);
        assertEquals("Wrong HTTP method for SecurityProfile creation REST service",
                        com.cloud.utils.rest.HttpMethod.POST, type);
    }

    @Test
    public void testUpdateSecurityProfile() throws NiciraNvpApiException, IOException {
        // Prepare
        final SecurityProfile inputSecProfile = new SecurityProfile();
        method = mock(PutMethod.class);
        when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        // Execute
        api.updateSecurityProfile(inputSecProfile, UUID);

        // Assert
        verify(method, times(1)).releaseConnection();
        assertEquals("Wrong URI for SecurityProfile creation REST service",
                        UUID_SEC_PROFILE_URI, uri);
        assertEquals("Wrong HTTP method for SecurityProfile creation REST service",
                        com.cloud.utils.rest.HttpMethod.PUT, type);
    }

    @Test
    public void testDeleteSecurityProfile() throws NiciraNvpApiException, IOException {
        // Prepare
        method = mock(DeleteMethod.class);
        when(method.getStatusCode()).thenReturn(HttpStatus.SC_NO_CONTENT);

        // Execute
        api.deleteSecurityProfile(UUID);

        // Assert
        verify(method, times(1)).releaseConnection();
        assertEquals("Wrong URI for SecurityProfile deletion REST service", UUID_SEC_PROFILE_URI, uri);
        assertEquals("Wrong HTTP method for SecurityProfile deletion REST service", com.cloud.utils.rest.HttpMethod.DELETE, type);
    }

    // @Test(expected = JsonParseException.class)
    // public void testRoutingConfigAdapterWrongType() throws NiciraNvpApiException, IOException {
    // // Prepare
    // final NiciraNvpApi api = new NiciraNvpApi();
    // final Gson gson = api.restConnector.getGson();
    //
    // // Execute
    // gson.fromJson("{type : \"WrongType\"}", RoutingConfig.class);
    //
    // // Assert: JsonParseException should be thrown
    // }
    //
    // @Test()
    // public void testRoutingConfigAdapter() throws NiciraNvpApiException, IOException {
    // // Prepare
    // final NiciraNvpApi api = new NiciraNvpApi();
    // final Gson gson = api.restConnector.getGson();
    //
    // // Execute
    // final SingleDefaultRouteImplicitRoutingConfig singleDefaultRouteImplicitRoutingConfig =
    // (SingleDefaultRouteImplicitRoutingConfig) gson.fromJson("{type : \"SingleDefaultRouteImplicitRoutingConfig\"}", RoutingConfig.class);
    //
    // // Assert: JsonParseException should be thrown
    // assertEquals("", SingleDefaultRouteImplicitRoutingConfig.class, singleDefaultRouteImplicitRoutingConfig.getClass());
    // }
    //
    // @Test(expected = JsonParseException.class)
    // public void testNatRuleAdapterNoType() throws NiciraNvpApiException, IOException {
    // // Prepare
    // final NiciraNvpApi api = new NiciraNvpApi();
    // final Gson gson = api.restConnector.getGson();
    //
    // // Execute
    // gson.fromJson("{}", NatRule.class);
    //
    // // Assert: JsonParseException should be thrown
    // }
    //
    // @Test(expected = JsonParseException.class)
    // public void testNatRuleAdapterWrongType() throws NiciraNvpApiException, IOException {
    // // Prepare
    // final NiciraNvpApi api = new NiciraNvpApi();
    // final Gson gson = api.restConnector.getGson();
    //
    // // Execute
    // gson.fromJson("{type : \"WrongType\"}", NatRule.class);
    //
    // // Assert: JsonParseException should be thrown
    // }
    //
    // @Test()
    // public void testRoutingConfigAdapterWithSourceNatRule() throws NiciraNvpApiException, IOException {
    // // Prepare
    // final NiciraNvpApi api = new NiciraNvpApi();
    // final Gson gson = api.restConnector.getGson();
    //
    // // Execute
    // final SourceNatRule sourceNatRule =
    // (SourceNatRule) gson.fromJson("{type : \"SourceNatRule\"}", NatRule.class);
    //
    // // Assert: JsonParseException should be thrown
    // assertEquals("", SourceNatRule.class, sourceNatRule.getClass());
    // }
    //
    // @Test()
    // public void testRoutingConfigAdapterWithDestinationNatRule() throws NiciraNvpApiException, IOException {
    // // Prepare
    // final NiciraNvpApi api = new NiciraNvpApi();
    // final Gson gson = api.restConnector.getGson();
    //
    // // Execute
    // final DestinationNatRule destinationNatRule =
    // (DestinationNatRule) gson.fromJson("{type : \"DestinationNatRule\"}", NatRule.class);
    //
    // // Assert: JsonParseException should be thrown
    // assertEquals("", DestinationNatRule.class, destinationNatRule.getClass());
    // }

}
