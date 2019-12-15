package ru.bcs.at.library.core.core.log;

import com.google.gson.*;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;


/**
 * Преобразователь документов для просмотра
 */
@Log4j2
public class DocumentFormatter {

    private static final String CONTENT_TYPE_XML = "text/xml";
    private static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * @param input строка, содержащая json
     * @return выровненный для просмотра документ или текст ошибки
     * преобразования
     * преобразование json к "красивому" виду
     */
    private static String createPrettyPrintJson(String input) {
        if (input.isEmpty()) {
            return ""; //**QUIT**
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonElement obj = new JsonParser().parse(input);
            return gson.toJson(obj); //**QUIT**
        } catch (JsonParseException ex) {
            return "Error pretty printing json:\n" + ex.getMessage(); //todo подкрасить?
        }
    }

    /**
     * @param input строка, содержащая xml
     * @return выровненный для просмотра документ или текст ошибки
     * преобразования
     * преобразование xml к "красивому" виду
     */
    private static String createPrettyPrintXml(String input) {
        if (input.isEmpty()) {
            return ""; //**QUIT**
        }
        InputSource src = new InputSource(new StringReader(input));
        try {
            Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(String.valueOf(src)).getDocumentElement();
            Boolean keepDeclaration = input.startsWith("<?xml");

            System.setProperty(DOMImplementationRegistry.PROPERTY,
                    "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

            DOMImplementationRegistry registry = DOMImplementationRegistry
                    .newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry
                    .getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);
            return writer.writeToString(document);
        } catch (Exception e) {
            return "Error pretty printing xml:\n" + e.getMessage(); //todo подкрасить?
        }
    }

    /**
     * @param input    строка, содержащая xml
     * @param contType тип ответа
     * @return выровненный для просмотра документ или текст ошибки
     * преобразования
     * преобразование документа к "красивому" виду
     */
    public static String createPrettyPrint(String input, String contType) {
        if (contType == null) {
            return input;
        }
        String typeOnly = contType.split(";")[0];
        switch (typeOnly) {
            case CONTENT_TYPE_XML:
                return createPrettyPrintXml(input);
            case CONTENT_TYPE_JSON:
                return createPrettyPrintJson(input);
            default:
                return input;
        }
    }
}

