package com.logpolice.infrastructure.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static final int MAX_SOCKET_TIMEOUT = 10000;
    private static final int MAX_CONNECT_TIMEOUT = 10000;

    /**
     * REST GET请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(MAX_SOCKET_TIMEOUT)
                .setConnectTimeout(MAX_CONNECT_TIMEOUT)
                .build();
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        String responseString = null;
        try {
            response = HttpClients.createDefault().execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.warn("response is not closed");
                e.printStackTrace();
            }
        }
        return responseString;
    }

    /**
     * REST POST请求
     *
     * @param url
     * @param json
     * @return
     */
    public static String post(String url, String json) {
        return post(url, json, null);

    }

    public static String toTrueJSON(String message) {
        String[] jsons = message.split("\":");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < jsons.length; i++) {
            if (!jsons[i].trim().startsWith("{") && !jsons[i].trim().startsWith("[") && !jsons[i].trim().startsWith("\"")) {
                jsons[i] = "\"" + jsons[i].split(",")[0] + "\"" + "," + jsons[i].split(",")[1];
                sb.append("\":" + jsons[i]);
            } else {
                if (i == 0) {
                    sb.append(jsons[i]);
                } else {
                    sb.append("\":" + jsons[i]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * REST POST请求
     *
     * @param url
     * @param json
     * @return
     */
    public static String post(String url, String json, Map<String, Object> headers) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(MAX_SOCKET_TIMEOUT)
                .setConnectTimeout(MAX_CONNECT_TIMEOUT)
                .build();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httppost.addHeader(entry.getKey(), entry.getValue() + "");
            }
        }
        httppost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        String responseString = null;
        try {
            StringEntity se = new StringEntity(json, "utf-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            httppost.setEntity(se);
            response = HttpClients.createDefault().execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.warn("response is not closed");
                e.printStackTrace();
            }
        }
        return responseString;

    }

}

