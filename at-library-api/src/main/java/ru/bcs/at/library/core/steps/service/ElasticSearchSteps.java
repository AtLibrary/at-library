package ru.bcs.at.library.core.steps.service;

import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import ru.bcs.at.library.core.core.helpers.PropertyLoader;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;
import static ru.bcs.at.library.core.core.helpers.Utils.urlEncodeUTF8;

/**
 * Шаги для работы с ElasticSearch
 */
@Log4j2
public class ElasticSearchSteps {

    private static final String ELASTIC_SCHEME = System.getProperty("elastic.scheme", tryLoadProperty("elastic.scheme"));
    private static final String ELASTIC_HOST = System.getProperty("elastic.host", tryLoadProperty("elastic.host"));
    private static final Integer ELASTIC_PORT = Integer.parseInt(System.getProperty("elastic.port", tryLoadProperty("elastic.port")));
    private static final String ELASTIC_USER = System.getProperty("elastic.user", tryLoadProperty("elastic.user"));
    private static final String ELASTIC_PASS = System.getProperty("elastic.pass", tryLoadProperty("elastic.pass"));

    private static final String TRUSTSTORE_PATH = System.getProperty("https.truststore.path", tryLoadProperty("https.truststore.path"));
    private static final String TRUSTSTORE_PASSWORD = System.getProperty("https.truststore.password", tryLoadProperty("https.truststore.password"));

    private static RestHighLevelClient client;

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Отправка запроса на поиск в базе ElasticSearch
     *
     * @param requestString строка запроса в Search API
     */
    @И("^выполнен поиск по запросу \"([^\"]*)\" в ElasticSearch$")
    public void simpleSearchRequest(String requestString) {
        simpleSearchRequest(requestString, null);
    }

    /**
     * Отправка запроса на поиск в базе ElasticSearch
     *
     * @param requestString строка запроса в Search API
     * @param index         индекс для поиска
     */
    @И("^выполнен поиск по запросу \"([^\"]*)\" в индексе \"([^\"]*)\" ElasticSearch$")
    public void simpleSearchRequest(String requestString, String index) {
        checkClient();
        String requestStringFinally = cycleSubstitutionFromFileOrPropertyOrVariable(requestString);

        try {
            RestClient restClient = client.getLowLevelClient();
            StringBuilder endpoint = new StringBuilder();
            if (index != null && !index.isEmpty()) {
                endpoint.append(urlEncodeUTF8(index)).append("/");
            }
            endpoint.append("_search?q=").append(urlEncodeUTF8(requestStringFinally));

            Request request = new Request("GET", endpoint.toString());
            log.debug("REQUEST method='{}' endpoint='{}'", request.getMethod(),
                    request.getEndpoint());
            if (request.getEntity() != null) {
                log.debug("request_body='{}'", EntityUtils.toString(request.getEntity()));
            }
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            log.debug("RESPONSE status_line='{}' request_line='{}' response_body='{}'", response.getStatusLine(),
                    response.getRequestLine(), responseBody);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Код ответа от ElasticSearch: " + response.getStatusLine().getStatusCode());
            }

            coreScenario.setVar(CoreScenario.CURRENT, responseBody);
        } catch (Exception e) {
            throw new RuntimeException("Error while request. Request : " + requestStringFinally, e);
        }
    }

    /**
     * Отправка запроса на поиск в базе ElasticSearch
     *
     * @param dataTable таблица с параметрами поиска вида: | ключ | значение |
     */
    @И("^выполнен поиск в ElasticSearch по таблице:$")
    public void searchRequest(DataTable dataTable) throws IOException {
        searchRequest(null, dataTable);
    }

    /**
     * Отправка запроса на поиск в базе ElasticSearch
     *
     * @param index     индекс для поиска
     * @param dataTable таблица с параметрами поиска вида: | ключ | значение |
     */
    @И("^выполнен поиск в индексе \"([^\"]*)\" ElasticSearch по таблице:$")
    public void searchRequest(String index, DataTable dataTable) throws IOException {
        checkClient();

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        for (List<String> queryParam : dataTable.asLists()) {
            qb.must(QueryBuilders.matchQuery(
                    PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable(queryParam.get(0)),
                    PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable(queryParam.get(1))
            ));
        }
        if (index != null && !index.isEmpty()) {
            qb.must(QueryBuilders.matchQuery("_index", index));
        }

        searchSourceBuilder.query(qb);
        searchRequest.source(searchSourceBuilder);

        log.debug("REQUEST request='{}'", searchRequest.toString());
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        log.debug("RESPONCE response='{}'", searchResponse.toString());

        coreScenario.setVar(CoreScenario.CURRENT, searchResponse.toString());
    }

    /**
     * Проверка или создание клиента ElasticSearch
     */
    private static void checkClient() {
        if (client == null) {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(ELASTIC_HOST, ELASTIC_PORT, ELASTIC_SCHEME))
                            .setHttpClientConfigCallback(httpClientBuilder -> {

                                if (ELASTIC_USER != null && !ELASTIC_USER.isEmpty()) {
                                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                    credentialsProvider.setCredentials(AuthScope.ANY,
                                            new UsernamePasswordCredentials(ELASTIC_USER, ELASTIC_PASS));

                                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                }

                                if (ELASTIC_SCHEME.equals("https")) {
                                    URL url = PropertyLoader.class.getClassLoader().getResource(TRUSTSTORE_PATH);

                                    try {
                                        SSLContextBuilder sslBuilder = SSLContexts.custom().loadTrustMaterial(url, TRUSTSTORE_PASSWORD.toCharArray()).setSecureRandom(new SecureRandom());

                                        httpClientBuilder.disableAuthCaching();
                                        httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                                        httpClientBuilder.setSSLContext(sslBuilder.build());
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                return httpClientBuilder;
                            })
            );
        }
    }

}
