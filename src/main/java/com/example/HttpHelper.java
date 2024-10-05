package com.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class HttpHelper {

    public static HttpResponse post(String url, List<NameValuePair> params) throws IOException {
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(20000) // Timeout per stabilire una connessione
            .setSocketTimeout(20000) // Timeout per attendere dati
            .build();

        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
            if (executionCount >= 3) {
                // Non tentare più di tre volte
                return false;
            }
            if (exception instanceof SocketTimeoutException) {
                // Riprova in caso di timeout del socket
                return true;
            }
            return false;
        };

        HttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(config)
            .setRetryHandler(retryHandler)
            .build();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpClient.execute(httpPost);
        return response;
    }

    public static void main(String[] args) {
        try {
            String url = "https://example.com/post";
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("key", "value"));
            params.add(new BasicNameValuePair("anotherKey", "anotherValue"));

            HttpResponse response = post(url, params);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Response: " + responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
