package ru.bcs.at.library.core.core.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.XmlParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.bcs.at.library.core.core.helpers.TextFormat.*;

public class Utils {

    /**
     * Проверяет, является ли переданная в качестве аргумента строка соответствующей переданному формату, или,
     * если формат не передан, определяет формат
     *
     * @param checkingValueString - строка для валидации
     * @param expectedTextFormat  - формат из множества
     * @return Определенный формат
     */
    public static TextFormat defineOrCheckDataFormat(String checkingValueString, TextFormat expectedTextFormat) {
        if (expectedTextFormat != null) {
            switch (expectedTextFormat) {
                case JSON:
                    assertTrue(
                            "Неверный формат данных:" +
                                    "\nожидаемое: " + JSON +
                                    "\nреальное: " + checkingValueString +
                                    "\n",
                            isJSONValid(checkingValueString));
                    break;
                case XML:
                    assertTrue(
                            "Неверный формат данных:" +
                                    "\nожидаемое: " + XML +
                                    "\nреальное: " + checkingValueString +
                                    "\n",
                            isXMLValid(checkingValueString));
                    break;
                case PARAMS:
                    assertTrue(
                            "Неверный формат данных:" +
                                    "\nожидаемое: " + PARAMS +
                                    "\nреальное: " + checkingValueString +
                                    "\n",
                            isParamsValid(checkingValueString));
                    break;
            }
        } else if (isJSONValid(checkingValueString)) {
            expectedTextFormat = JSON;
        } else if (isXMLValid(checkingValueString)) {
            expectedTextFormat = XML;
        } else if (isParamsValid(checkingValueString)) {
            expectedTextFormat = PARAMS;
        } else {
            fail("Нечитаемый формат данных");
        }
        return expectedTextFormat;
    }

    /**
     * @param jsonInString - строка для валидации
     * @return Проверяет, является ли переданная в качестве аргумента строка валидным JSON
     */
    public static boolean isJSONValid(String jsonInString) {
        if (jsonInString.isEmpty()) {
            return false;
        }
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @param xmlInString - строка для валидации
     * @return Проверяет, является ли переданная в качестве аргумента строка валидным XML
     */
    public static boolean isXMLValid(String xmlInString) {
        try {
            final XmlParser parser = new XmlParser();
            parser.parseText(xmlInString);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return false;
        }
        return true;
    }

    /**
     * @param paramsInString - строка для валидации
     * @return Проверяет, является ли переданная в качестве аргумента строка валидным URL Params (param1=value1&param2=value2)
     */
    public static boolean isParamsValid(String paramsInString) {

        return paramsInString.matches("((.+=.*)&)*(.+=.*)");
    }

    /**
     * @param xmlin - xml-поток для преобразования
     * @return Преобразует InputStream, содержащий xml, в объектное представление
     */
    public static Document readXml(InputStream xmlin) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(xmlin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param doc       - объектное представление xml
     * @param xpathExpr - xpath для поиска
     * @return Производит поиск в xml по переданному xpath, возвращает найденный узел
     */
    public static NodeList filterNodesByXPath(Document doc, String xpathExpr) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpr);
            Object eval = expr.evaluate(doc, XPathConstants.NODESET);
            return (NodeList) eval;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param inputString - строка для поиска соответствий регулярному выражению
     * @param regex       - регулярное выражение для поиска
     * @return Возращает список соответствий по регулярному выражению
     */
    public static List<String> getMatchesByRegex(String inputString, String regex) {
        List<String> result = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(inputString);
        while (m.find()) {
            result.add(m.group(0));
        }
        return result;
    }

    /**
     * Кодирование UTF-8 строки в URL
     */
    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

}
