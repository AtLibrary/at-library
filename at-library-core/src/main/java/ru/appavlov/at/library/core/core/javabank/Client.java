package ru.appavlov.at.library.core.core.javabank;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONArray;
import org.json.simple.parser.ParseException;
import ru.appavlov.at.library.core.core.javabank.http.imposters.Imposter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Client {

    static final String DEFAULT_BASE_URL = "http://localhost:2525";

    protected String baseUrl;

    public Client() {
        this(DEFAULT_BASE_URL);
    }

    public Client(String baseUrl) {
        this.baseUrl = baseUrl;
        Unirest.setHttpClient(makeClient());
    }

    public Client(String host, int port) {
        this.baseUrl = String.format("%s:%d", host, port);
        Unirest.setHttpClient(makeClient());
    }

    public static HttpClient makeClient() {
        SSLContextBuilder builder = new SSLContextBuilder();
        CloseableHttpClient httpclient = null;
        try {
            // builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            builder.loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            httpclient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
            System.out.println("custom httpclient called");
            System.out.println(httpclient);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return httpclient;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isMountebankRunning() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(baseUrl).asJson();
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            return false;
        }
    }

    public boolean isMountebankAllowingInjection() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/config").asJson();
            return response.getBody().getObject().getJSONObject("options").getBoolean("allowInjection");
        } catch (UnirestException e) {
            return false;
        }
    }

    public int createImposter(Imposter imposter) {
        try {
            HttpResponse<JsonNode> response = Unirest.post(baseUrl + "/imposters").body(imposter.toString()).asJson();
            return response.getStatus();
        } catch (UnirestException e) {
            return 500;
        }
    }

    public String deleteImposter(int port) {
        try {
            HttpResponse<JsonNode> response = Unirest.delete(baseUrl + "/imposters/" + port).asJson();
            return response.getBody().toString();
        } catch (UnirestException e) {
            return null;
        }
    }

    public int getImposterCount() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/imposters").asJson();
            return ((JSONArray) response.getBody().getObject().get("imposters")).length();
        } catch (UnirestException e) {
            return -1;
        }
    }

    public int deleteAllImposters() {
        try {
            HttpResponse<JsonNode> response = Unirest.delete(baseUrl + "/imposters").asJson();
            return response.getStatus();
        } catch (UnirestException e) {
            return 500;
        }
    }

    public Imposter getImposter(int port) throws ParseException {
        try {
            HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/imposters/" + port).asJson();
            String responseJson = response.getBody().toString();

            return ImposterParser.parse(responseJson);
        } catch (UnirestException e) {
            return null;
        }
    }
}
