package ru.bcs.at.library.core.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.И;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.Arrays;
import java.util.Collections;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * <h1>Шаги по работе с BizTalk</h1>
 */
public class BizTalkSteps {

    private static final String BIZTALK_SERVICE_ADDRESS = System.getProperty("bizTalkServiceAddress", tryLoadProperty("bizTalkServiceAddress"));
    private CoreScenario coreScenario = CoreScenario.getInstance();
    private RequestSteps requestSteps = RequestSteps.getInstance();

    /**
     * Отправка запроса в BizTalk</p>
     */
    @И("^отправлен запрос \"([^\"]*)\" в BizTalk$")
    public void sendRequestToBizTalk(String dataToRequest) {
        dataToRequest = loadValueFromFileOrPropertyOrVariableOrDefault(dataToRequest);

        requestSteps.sendHttpRequestSaveResponse(
                "POST",
                BIZTALK_SERVICE_ADDRESS,
                CoreScenario.TEMP_RESPONSE,
                DataTable.create(Collections.singletonList(Arrays.asList("BODY", "", dataToRequest)))
        );

        Response response = (Response) coreScenario.getVar(CoreScenario.TEMP_RESPONSE);

        response.then().assertThat().body(Matchers.not(Matchers.contains("<state>Rejected</state>")));
    }

}
