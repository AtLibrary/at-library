/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.at.library.core.core.helpers;

import com.google.common.base.Strings;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для получения свойств
 */
@Log4j2
public class PropertyLoader {

    public static String PROPERTIES_FILE = "/" + System.getProperty("properties", "application.properties");
    private static final Properties PROPERTIES = getPropertiesInstance();
    private static final Properties PROFILE_PROPERTIES = getProfilePropertiesInstance();

    private PropertyLoader() {
    }

    /**
     * Возвращает значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства по названию или значение по умолчанию
     */
    public static String loadSystemPropertyOrDefault(String propertyName, String defaultValue) {
        String propValue = System.getProperty(propertyName);
        return propValue != null ? propValue : defaultValue;
    }

    /**
     * Возвращает Integer значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue Integer значение по умолчанию
     * @return Integer значение свойства по названию или значение по умолчанию
     */
    public static Integer loadSystemPropertyOrDefault(String propertyName, Integer defaultValue) {
        try {
            return Integer.valueOf(System.getProperty(propertyName, defaultValue.toString()).trim());
        } catch (NumberFormatException ex) {
            log.error("Could not parse value to Integer ", ex.getMessage());
            return defaultValue;
        }
    }

    /**
     * Возвращает Boolean значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue Boolean значение по умолчанию
     * @return Integer значение свойства по названию или значение по умолчанию
     */
    public static Boolean loadSystemPropertyOrDefault(String propertyName, Boolean defaultValue) {
        String def = defaultValue.toString();
        String property = loadProperty(propertyName, def);
        return Boolean.parseBoolean(property.trim());
    }

    /**
     * Возвращает свойство по его названию из property-файла
     *
     * @param propertyName название свойства
     * @return значение свойства, в случае, если значение не найдено,
     * будет выброшено исключение
     */
    public static String loadProperty(String propertyName) {
        String value = tryLoadProperty(propertyName);
        if (null == value) {
            throw new IllegalArgumentException("В файле properties не найдено значение по ключу: " + propertyName);
        }
        return value;
    }

    /**
     * Возвращает значение свойства из property-файла по его названию,
     * если значение не найдено, возвращает это же значение в качестве значения по умолчанию
     *
     * @param propertyNameOrValue название свойства/значение по умолчанию
     * @return значение по ключу value, если значение не найдено,
     * вернется value
     */
    public static String getPropertyOrValue(String propertyNameOrValue) {
        return loadProperty(propertyNameOrValue, propertyNameOrValue);
    }

    /**
     * Возвращает значение свойства из property-файла по его названию,
     * Если ничего не найдено, возвращает значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства
     */
    public static String loadProperty(String propertyName, String defaultValue) {
        String value = tryLoadProperty(propertyName);
        return value != null ? value : defaultValue;
    }

    /**
     * Возвращает значение свойства типа Integer из property-файла по названию,
     * если ничего не найдено, возвращает значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства типа Integer или значение по умолчанию
     */
    public static Integer loadPropertyInt(String propertyName, Integer defaultValue) {
        String value = tryLoadProperty(propertyName);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Вспомогательный метод, возвращает значение свойства по имени.
     * Сначала поиск в System переменным,
     * затем в property-файле, если указано системное свойство "profile"
     * Если ничего не найдено, поиск в properties
     *
     * @param propertyName название свойства
     * @return значение свойства
     */
    public static String tryLoadProperty(String propertyName) {
        String value = null;
        if (!Strings.isNullOrEmpty(propertyName)) {
            String systemProperty = loadSystemPropertyOrDefault(propertyName, propertyName);
            if (!propertyName.equals(systemProperty)) return systemProperty;

            value = PROFILE_PROPERTIES.getProperty(propertyName);
            if (null == value) {
                value = PROPERTIES.getProperty(propertyName);
            }
        }
        return value;
    }


    /**
     * Вспомогательный метод, возвращает значение свойства по имени.
     */
    public static String loadValueFromFileOrVariableOrDefault(String valueToFind) {
        String pathAsString = StringUtils.EMPTY;
        try {
            Path path = Paths.get(System.getProperty("user.dir") + valueToFind);
            pathAsString = path.toString();
            String fileValue = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            log.trace("Значение из файла " + valueToFind + " = " + fileValue);
            return fileValue;
        } catch (IOException | InvalidPathException e) {
            log.trace("Значение не найдено по пути " + pathAsString);
        }
        if (CoreScenario.getInstance().tryGetVar(valueToFind) != null) {
            Object var = CoreScenario.getInstance().getVar(valueToFind);
            //TODO нужно зарефакторить
            if (var instanceof Response) {
                return ((Response) var).getBody().asString();
            }
            return (String) var;
        }
        log.trace("Значение не найдено в хранилище. Будет исользовано значение по умолчанию " + valueToFind);
        return valueToFind;
    }

    /**
     * Получает значение из properties, файла по переданному пути, значение из хранилища переменных или как String аргумент
     * Используется для получение body.json api шагах, либо для получения script.js в ui шагах
     *
     * @param valueToFind - ключ к значению в properties, путь к файлу c нужным значением, значение как String
     * @return значение как String
     */
    public static String loadValueFromFileOrPropertyOrVariableOrDefault(String valueToFind) {
        String pathAsString = StringUtils.EMPTY;
        String propertyValue = tryLoadProperty(valueToFind);
        if (StringUtils.isNotBlank(propertyValue)) {
            log.trace("Значение переменной: " + valueToFind + " из " + PROPERTIES_FILE + " = " + propertyValue);
            return propertyValue;
        }
        try {
            Path path = Paths.get(System.getProperty("user.dir") + valueToFind);
            pathAsString = path.toString();
            String fileValue = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            log.trace("Значение из файла " + valueToFind + " = " + fileValue);
            return fileValue;
        } catch (IOException | InvalidPathException e) {
            log.trace("Значение не найдено по пути: " + pathAsString);
        }
        if (CoreScenario.getInstance().tryGetVar(valueToFind) != null) {
            Object var = CoreScenario.getInstance().getVar(valueToFind);
            //TODO нужно зарефакторить
            if (var instanceof Response) {
                return ((Response) var).getBody().asString();
            }
            return (String) var;
        }
        log.trace("Значение не найдено в хранилище. Будет исользовано значение по умолчанию " + valueToFind);
        return valueToFind;
    }

    /**
     * Циклически подставляет параметры из properties, содержимое файла по переданному пути,
     * значение из хранилища переменных или как String аргумент
     *
     * @param processingValue - строка, содержащая в фигурных скобках ключи к значению в properties, переменные сценариев,
     *                        названия путей к файлам c нужным значением, значения как строки. Пример:
     *                        123{var_name} 456{prop_name} 789{file_path_from_project_root}
     * @return значение как String после всевозможных замен
     */
    public static String cycleSubstitutionFromFileOrPropertyOrVariable(String processingValue) {
        String savedValue;
        do {
            savedValue = processingValue;
            List<String> matches = getMatchesByRegex(processingValue, "\\{[^\\s{}]+}");
            if (matches.size() == 0) {
                return processingValue;
            }
            for (String match : matches) {
                String oldValue = match.substring(1, match.length() - 1);
                String newValue = loadValueFromFileOrPropertyOrVariableOrDefault(oldValue);
                if (!oldValue.equals(newValue)) {
                    processingValue = processingValue.replace(match, newValue);
                }
            }
        } while (!processingValue.equals(savedValue));

        return processingValue;
    }

    /**
     * Вспомогательный метод, возвращает свойства из файла properties
     *
     * @return свойства из файла properties
     */
    @SneakyThrows(IOException.class)
    private static Properties getPropertiesInstance() {
        Properties instance = new Properties();
        try (
                InputStream resourceStream = PropertyLoader.class.getResourceAsStream(PROPERTIES_FILE);
                InputStreamReader inputStream = new InputStreamReader(resourceStream, StandardCharsets.UTF_8)
        ) {
            instance.load(inputStream);
        }
        return instance;
    }

    /**
     * Вспомогательный метод, возвращает свойства из кастомного properties по пути
     * из системного свойства "profile"
     *
     * @return прочитанные свойства из кастомного файла properties, если свойство "profile" указано, иначе пустой объект
     */
    @SneakyThrows(IOException.class)
    private static Properties getProfilePropertiesInstance() {
        Properties instance = new Properties();
        String profile = System.getProperty("profile", "");
        if (!Strings.isNullOrEmpty(profile)) {
            String path = Paths.get(profile, PROPERTIES_FILE).toString();
            URL url = PropertyLoader.class.getClassLoader().getResource(path);
            try (
                    InputStream resourceStream = url.openStream();
                    InputStreamReader inputStream = new InputStreamReader(resourceStream, StandardCharsets.UTF_8)
            ) {
                instance.load(inputStream);
            }
        }
        return instance;
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

}

