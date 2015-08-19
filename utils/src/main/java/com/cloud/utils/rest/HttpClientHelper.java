package com.cloud.utils.rest;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

public class HttpClientHelper {

    public static CloseableHttpClient createHttpClient(final int maxRedirects) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = createSocketFactoryConfigration();
        final BasicCookieStore cookieStore = new BasicCookieStore();
        return HttpClientBuilder.create()
            .setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry))
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setDefaultRequestConfig(RequestConfig.custom().setMaxRedirects(maxRedirects).build())
            .setDefaultCookieStore(cookieStore)
            .setRetryHandler(new StandardHttpRequestRetryHandler())
            .build();
    }

    private static Registry<ConnectionSocketFactory> createSocketFactoryConfigration() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        Registry<ConnectionSocketFactory> socketFactoryRegistry;
        final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
        final SSLConnectionSocketFactory cnnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
            .register("https", cnnectionSocketFactory)
            .build();

        return socketFactoryRegistry;
    }

}
