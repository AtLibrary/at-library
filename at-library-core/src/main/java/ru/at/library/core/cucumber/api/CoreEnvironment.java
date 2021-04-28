/**
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
package ru.at.library.core.cucumber.api;

import io.cucumber.java.Scenario;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.utils.helpers.ScopedVariables;

import java.util.Arrays;

/**
 * Класс, связанный с CoreScenario, используется для хранения страниц и переменных внутри сценария
 */
@Slf4j
public class CoreEnvironment {

    /**
     * Сценарий (Cucumber.api), с которым связана среда
     */
    @Getter
    private Scenario scenario;

    /**
     * Список веб-страниц, заданных пользователем, доступных для использования в сценариях
     */
    @Getter
    private final Pages pages = new Pages();

    /**
     * Переменные, объявленные пользователем внутри сценария
     * ThreadLocal обеспечивает отсутствие коллизий при многопоточном запуске
     */
    @Getter
    private ThreadLocal<ScopedVariables> variables = new ThreadLocal<>();


    public CoreEnvironment(Scenario scenario) {
        this.scenario = scenario;
        initPages();
        variables.set(new ScopedVariables());
    }


    /**
     * Метод ищет классы, аннотированные "CorePage.Name",
     * добавляя ссылки на эти классы в поле "pages"
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void initPages() {
        new AnnotationScanner().getClassesAnnotatedWith(Name.class)
                .stream()
                .map(classAnnotatedName -> {
                    if (CorePage.class.isAssignableFrom(classAnnotatedName)) {
                        return (Class<? extends CorePage>) classAnnotatedName;
                    } else {
                        throw new IllegalStateException("Класс " + classAnnotatedName.getName() + " должен наследоваться от CorePage");
                    }
                })
                .forEach(classExtendsCorePage ->
                        pages.put(getClassAnnotationValue(classExtendsCorePage), classExtendsCorePage));
    }

    /**
     * Вспомогательный метод, получает значение аннотации "CorePage.Name" для класса
     *
     * @param classAnnotatedName класс, который должен быть аннотирован "CorePage.Name"
     * @return значение аннотации "CorePage.Name" для класса
     */
    private String getClassAnnotationValue(Class<?> classAnnotatedName) {
        return Arrays.stream(classAnnotatedName.getAnnotationsByType(Name.class))
                .findAny()
                .map(Name::value)
                .orElseThrow(() ->
                        new IllegalStateException("Не найдены аннотации CorePage.Name в классе " + classAnnotatedName.getClass().getName()));
    }


    public Object getVar(String name) {
        return getVariables().get().get(name);
    }

    public void setVar(String name, Object object) {
        getVariables().get().put(name, object);
    }

    public CorePage getPage(String name) {
        return pages.get(name);
    }

    public <T extends CorePage> T getPage(Class<T> clazz, String name) {
        return pages.get(clazz, name);
    }

    public String replaceVariables(String textToReplaceIn) {
        return getVariables().get().replaceVariables(textToReplaceIn);
    }

}
