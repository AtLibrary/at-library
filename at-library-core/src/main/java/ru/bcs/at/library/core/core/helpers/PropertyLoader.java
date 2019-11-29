/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.core.helpers;

import com.google.common.base.Strings;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * <h1>Класс для получения свойств</h1>
 */
@Log4j2
public class PropertyLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final Properties PROPERTIES = getPropertiesInstance();
    private static final Properties PROFILE_PROPERTIES = getProfilePropertiesInstance();

    private PropertyLoader() {
    }

    /**
     * <p>Возвращает значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию</p>
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
     * <p>Возвращает Integer значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию</p>
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
     * <p>Возвращает Boolean значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию</p>
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
     * <p>Возвращает свойство по его названию из property-файла</p>
     *
     * @param propertyName название свойства
     * @return значение свойства, в случае, если значение не найдено,
     * будет выброшено исключение
     */
    public static String loadProperty(String propertyName) {
        String value = tryLoadProperty(propertyName);
        if (null == value) {
            throw new IllegalArgumentException("В файле application.properties не найдено значение по ключу: " + propertyName);
        }
        return value;
    }

    /**
     * <p>Возвращает значение свойства из property-файла по его названию,
     * если значение не найдено, возвращает это же значение в качестве значения по умолчанию</p>
     *
     * @param propertyNameOrValue название свойства/значение по умолчанию
     * @return значение по ключу value, если значение не найдено,
     * вернется value
     */
    public static String getPropertyOrValue(String propertyNameOrValue) {
        return loadProperty(propertyNameOrValue, propertyNameOrValue);
    }

    /**
     * <p>Возвращает значение свойства из property-файла по его названию,
     * Если ничего не найдено, возвращает значение по умолчанию</p>
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
     * <p>Возвращает значение свойства типа Integer из property-файла по названию,
     * если ничего не найдено, возвращает значение по умолчанию</p>
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
     * <p>Вспомогательный метод, возвращает значение свойства по имени.
     * Сначала поиск в System переменным,
     * затем в property-файле, если указано системное свойство "profile"
     * Если ничего не найдено, поиск в /application.properties</p>
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
     * <p>
     * Вспомогательный метод, возвращает значение свойства по имени.
     * </p>
     */
    public static String loadValueFromFileOrVariableOrDefault(String valueToFind) {
        String pathAsString = StringUtils.EMPTY;
        try {
            Path path = Paths.get(System.getProperty("user.dir") + valueToFind);
            pathAsString = path.toString();
            String fileValue = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            CoreScenario.getInstance().write("Значение из файла " + valueToFind + " = " + fileValue);
            return fileValue;
        } catch (IOException | InvalidPathException e) {
            CoreScenario.getInstance().write("Значение не найдено по пути " + pathAsString);
        }
        if (CoreScenario.getInstance().tryGetVar(valueToFind) != null) {
            Object var = CoreScenario.getInstance().getVar(valueToFind);
            //TODO нужно зарефакторить
            if (var instanceof Response) {
                return ((Response) var).getBody().asString();
            }
            return (String) var;
        }
        CoreScenario.getInstance().write("Значение не найдено в хранилище. Будет исользовано значение по умолчанию " + valueToFind);
        return valueToFind;
    }

    /**
     * <p>Получает значение из application.properties, файла по переданному пути, значение из хранилища переменных или как String аргумент
     * Используется для получение body.json api шагах, либо для получения script.js в ui шагах</p>
     *
     * @param valueToFind - ключ к значению в application.properties, путь к файлу c нужным значением, значение как String
     * @return значение как String
     */
    public static String loadValueFromFileOrPropertyOrVariableOrDefault(String valueToFind) {
        String pathAsString = StringUtils.EMPTY;
        String propertyValue = tryLoadProperty(valueToFind);
        if (StringUtils.isNotBlank(propertyValue)) {
            CoreScenario.getInstance().write("Значение переменной " + valueToFind + " из application.properties = " + propertyValue);
            return propertyValue;
        }
        try {
            Path path = Paths.get(System.getProperty("user.dir") + valueToFind);
            pathAsString = path.toString();
            String fileValue = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            CoreScenario.getInstance().write("Значение из файла " + valueToFind + " = " + fileValue);
            return fileValue;
        } catch (IOException | InvalidPathException e) {
            CoreScenario.getInstance().write("Значение не найдено по пути " + pathAsString);
        }
        if (CoreScenario.getInstance().tryGetVar(valueToFind) != null) {
            Object var = CoreScenario.getInstance().getVar(valueToFind);
            //TODO нужно зарефакторить
            if (var instanceof Response) {
                return ((Response) var).getBody().asString();
            }
            return (String) var;
        }
        CoreScenario.getInstance().write("Значение не найдено в хранилище. Будет исользовано значение по умолчанию " + valueToFind);
        return valueToFind;
    }

    /**
     * <p>Циклически подставляет параметры из application.properties, содержимое файла по переданному пути,
     * значение из хранилища переменных или как String аргумент</p>
     *
     * @param processingValue - строка, содержащая в фигурных скобках ключи к значению в application.properties, переменные сценариев,
     *                        названия путей к файлам c нужным значением, значения как строки. Пример:
     *                        123{var_name} 456{prop_name} 789{file_path_from_project_root}
     * @return значение как String после всевозможных замен
     */
    public static String cycleSubstitutionFromFileOrPropertyOrVariable(String processingValue) {
        String savedValue;
        do {
            savedValue = processingValue;
            List<String> matches = Utils.getMatchesByRegex(processingValue, "\\{[^\\s{}]+}");
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
     * <p>Вспомогательный метод, возвращает свойства из файла /application.properties</p>
     *
     * @return свойства из файла /application.properties
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
     * <p>Вспомогательный метод, возвращает свойства из кастомного application.properties по пути
     * из системного свойства "profile"</p>
     *
     * @return прочитанные свойства из кастомного файла application.properties, если свойство "profile" указано, иначе пустой объект
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

}

