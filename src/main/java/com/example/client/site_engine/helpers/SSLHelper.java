package com.example.client.site_engine.helpers;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SSLHelper {
    static public Connection getConnection(String url){
        Connection conn = Jsoup.connect(url).sslSocketFactory(SSLHelper.socketFactory()).timeout(5000);
        conn.header ("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,video/mp4,*/*;q=0.8");
        conn.header ("accept-encoding", "gzip, deflate, sdch");
        conn.header ("Accept-language", "zh-cn,zh;q=0.8");
        conn.header ("user-agent", "mozilla/5.0 (Windows NT 10.0  WOW64) applewebkit/537.36 (khtml like Gecko) chrome/55.0.2883.87 safari/537.36");
        return Objects.requireNonNull(conn);
    }

    static public SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{(TrustManager) new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                //return null;
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
    static public SSLContext getSSLContext() {
        TrustManager[] trustAllCerts = new TrustManager[]{(TrustManager) new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
               // return null;
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            //SSLSocketFactory result = sslContext.getSocketFactory();
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}
