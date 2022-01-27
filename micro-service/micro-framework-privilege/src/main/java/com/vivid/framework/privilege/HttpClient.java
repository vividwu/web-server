package com.vivid.framework.privilege;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class HttpClient {

    public static final int TIME_OUT_MILLIS =  50000;
    public static final int CON_TIME_OUT_MILLIS = 3000;
    private static final boolean ENABLE_HTTPS = false;

    private static final String POST = "POST";
    private static final String PUT = "PUT";

    static {
        // limit max redirection
        System.setProperty("http.maxRedirects", "5");
    }

    public static String getPrefix() {
        if (ENABLE_HTTPS) {
            return "https://";
        }

        return "http://";

    }

    public static HttpResult httpGet(String url, List<String> headers, Map<String, String> paramValues, String encoding) {
        return request(url, headers, paramValues, encoding, "GET");
    }
    public static HttpResult httpPostJson(String url, List<String> headers,String jsonStr, String encoding) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            setHeaders(conn, headers, encoding);
            conn.setConnectTimeout(CON_TIME_OUT_MILLIS);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();
            // POST请求
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            //String json = java.net.URLEncoder.encode(jsonStr, "utf-8");
            out.write(jsonStr.getBytes("utf-8"));  //writeBytes(jsonStr.getBytes("utf-8"));
            out.flush();
            out.close();
            // 读取响应
            return getResult(conn);
        }catch (Exception e){
            return new HttpResult(500, e.toString(), Collections.<String, String>emptyMap());
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static HttpResult request(String url, List<String> headers, Map<String, String> paramValues, String encoding, String method) {
        HttpURLConnection conn = null;
        try {
            String encodedContent = encodingParams(paramValues, encoding);
            url += (StringUtils.isEmpty(encodedContent)) ? "" : ("?" + encodedContent);

            conn = (HttpURLConnection) new URL(url).openConnection();

            setHeaders(conn, headers, encoding);
            conn.setConnectTimeout(CON_TIME_OUT_MILLIS);
            conn.setReadTimeout(TIME_OUT_MILLIS);
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            if (POST.equals(method) || PUT.equals(method)) {
                // fix: apache http nio framework must set some content to request body
                byte[] b = encodedContent.getBytes();
                conn.setRequestProperty("Content-Length", String.valueOf(b.length));
                conn.getOutputStream().write(b, 0, b.length);
                conn.getOutputStream().flush();
                conn.getOutputStream().close();
            }
            conn.connect();

            return getResult(conn);
        } catch (Exception e) {
            try {
                if (conn != null) {

                }
            } catch (Exception e1) {
                //ignore
            }

            return new HttpResult(500, e.toString(), Collections.<String, String>emptyMap());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static HttpResult getResult(HttpURLConnection conn) throws IOException {
        int respCode = conn.getResponseCode();

        InputStream inputStream;
        if (HttpURLConnection.HTTP_OK == respCode
                || HttpURLConnection.HTTP_NOT_MODIFIED == respCode
                || 307 == respCode) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        Map<String, String> respHeaders = new HashMap<String, String>(conn.getHeaderFields().size());
        for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            respHeaders.put(entry.getKey(), entry.getValue().get(0));
        }

        String encodingGzip = "gzip";

        if (encodingGzip.equals(respHeaders.get(HttpHeaders.CONTENT_ENCODING))) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new HttpResult(respCode, IoUtils.toString(inputStream, getCharset(conn)), respHeaders);
    }

    private static String getCharset(HttpURLConnection conn) {
        String contentType = conn.getContentType();
        if (StringUtils.isEmpty(contentType)) {
            return "UTF-8";
        }

        String[] values = contentType.split(";");
        if (values.length == 0) {
            return "UTF-8";
        }

        String charset = "UTF-8";
        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        return charset;
    }

    private static void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }

        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset="
                + encoding);
        conn.addRequestProperty("Accept-Charset", encoding);
    }

    private static String encodingParams(Map<String, String> params, String encoding)
            throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) {
            return "";
        }

        params.put("encoding", encoding);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.isEmpty(entry.getValue())) {
                continue;
            }

            sb.append(entry.getKey()).append("=");
            sb.append(URLEncoder.encode(entry.getValue(), encoding));
            sb.append("&");
        }

        if (sb.length() > 0) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static class HttpResult {
        final public int code;
        final public String content;
        final private Map<String, String> respHeaders;

        public HttpResult(int code, String content, Map<String, String> respHeaders) {
            this.code = code;
            this.content = content;
            this.respHeaders = respHeaders;
        }

        public String getHeader(String name) {
            return respHeaders.get(name);
        }
    }
}