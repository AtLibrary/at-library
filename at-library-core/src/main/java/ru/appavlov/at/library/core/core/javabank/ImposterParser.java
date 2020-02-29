package ru.appavlov.at.library.core.core.javabank;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.appavlov.at.library.core.core.javabank.http.imposters.Imposter;

public class ImposterParser {

    public static Imposter parse(String json) throws ParseException {
        JSONObject parsedJson = (JSONObject) new JSONParser().parse(json);

        return Imposter.fromJSON(parsedJson);
    }
}
