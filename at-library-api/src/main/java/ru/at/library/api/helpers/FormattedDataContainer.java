package ru.at.library.api.helpers;

import com.google.common.base.Splitter;
import com.jayway.jsonpath.JsonPath;
import com.sun.istack.NotNull;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import ru.at.library.core.core.helpers.TextFormat;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

/**
 * Класс-композиция для универсализации работы с форматированным текстом (json/xml/params)
 */
public class FormattedDataContainer {

    private final TextFormat dataFormat;
    private JSONObject jsonObject;
    private Document xmlDocument;
    private Map<String, String> paramsMap;

    /**
     * Инициализация
     */
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
                xmlDocument = Utils.readXml(new ByteArrayInputStream(formattingValue.getBytes()));
                break;
            case PARAMS:
                paramsMap = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(formattingValue);
                break;
        }
    }

    /**
     * Чтение содержимого форматированного текста по path
     *
     * @param path путь к необходимому параметру
     * @return значение по переданному пути
     */
    public String readValue(String path) {
        switch (dataFormat) {
            case JSON:
                return String.valueOf((Object) JsonPath.read(jsonObject, path));
            case XML:
                NodeList valueList = Utils.filterNodesByXPath(xmlDocument, path);
                if (valueList != null && valueList.getLength() > 0) {
                    return valueList.item(0).getTextContent();
                }
            case PARAMS:
                try {
                    String value = paramsMap.get(path);
                    if (value != null) {
                        return URLDecoder.decode(value, "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

}
