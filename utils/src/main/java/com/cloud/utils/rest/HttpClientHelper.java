package com.cloud.utils.rest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.util.Assert;

public class HttpClientHelper {

    public static CloseableHttpClient createHttpClient(final String username, final String password, final int maxRedirects) {
        validate(username, password);
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        return HttpClientBuilder.create()
                        .setConnectionManager(new PoolingHttpClientConnectionManager())
                        .setRedirectStrategy(new LaxRedirectStrategy())
                        .setDefaultRequestConfig(RequestConfig.custom().setMaxRedirects(maxRedirects).build())
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .build();
    }

    private static void validate(final String username, final String password) {
        Assert.notNull(username, "Username cannot be null");
        Assert.hasLength(username, "Username cannot be empy");
        Assert.notNull(password, "Password cannot be null");
        Assert.hasLength(password, "Password cannot be empy");
    }

}
