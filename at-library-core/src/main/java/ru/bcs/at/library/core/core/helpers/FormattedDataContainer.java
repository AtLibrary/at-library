package ru.bcs.at.library.core.core.helpers;

import com.google.common.base.Splitter;
import com.jayway.jsonpath.JsonPath;
import com.sun.istack.NotNull;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;
import static ru.bcs.at.library.core.core.helpers.Utils.filterNodesByXPath;
import static ru.bcs.at.library.core.core.helpers.Utils.readXml;

public class FormattedDataContainer {

    private final TextFormat dataFormat;
    private JSONObject jsonObject;
    private Document xmlDocument;
    private Map<String, String> paramsMap;

    public FormattedDataContainer(@NotNull TextFormat format, String formattingValue) {
        dataFormat = format;

        switch (dataFormat) {
            case JSON:
                try {
                    jsonObject = (JSONObject) new JSONParser(DEFAULT_PERMISSIVE_MODE).parse(formattingValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case XML:
                xmlDocument = readXml(new ByteArrayInputStream(formattingValue.getBytes()));
                break;
            case PARAMS:
                paramsMap = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(formattingValue);
                break;
        }
    }

    public String readValue(String path) {
        switch (dataFormat) {
            case JSON:
                return String.valueOf((Object) JsonPath.read(jsonObject, path));
            case XML:
                return filterNodesByXPath(xmlDocument, path).item(0).getTextContent();
            case PARAMS:
                try {
                    return URLDecoder.decode(paramsMap.get(path), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
