/**
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
package ru.bcs.at.library.core.cucumber.api;

import cucumber.api.Scenario;
import lombok.extern.log4j.Log4j2;
import ru.bcs.at.library.core.cucumber.ScopedVariables;
import ru.bcs.at.library.core.cucumber.annotations.Name;

import java.util.Arrays;

/**
 * <h1>Класс, связанный с CoreScenario, используется для хранения страниц и переменных внутри сценария</h1>
 */
@Log4j2
public class CoreEnvironment {

    /**
     * Сценарий (Cucumber.api), с которым связана среда
     */
    private Scenario scenario;
    /**
     * Переменные, объявленные пользователем внутри сценария
     * ThreadLocal обеспечивает отсутствие коллизий при многопоточном запуске
     */
    private ThreadLocal<ScopedVariables> variables = new ThreadLocal<>();
    /**
     * Список веб-страниц, заданных пользователем, доступных для использования в сценариях
     */
    private Pages pages = new Pages();

    public CoreEnvironment(Scenario scenario, boolean webTest) {
        this.scenario = scenario;
        if (webTest) {
            initPages();
        }
    }

    public CoreEnvironment() {
        initPages();
    }

    /**
     * Метод ищет классы, аннотированные "CorePage.Name",
     * добавляя ссылки на эти классы в поле "ru.bcs.at.library.core.pages"
     */
    @SuppressWarnings("unchecked")
    private void initPages() {
        new AnnotationScanner().getClassesAnnotatedWith(Name.class)
                .stream()
                .map(it -> {
                    if (CorePage.class.isAssignableFrom(it)) {
                        return (Class<? extends CorePage>) it;
                    } else {
                        throw new IllegalStateException("Класс " + it.getName() + " должен наследоваться от CorePage");
                    }
                })
                .forEach(clazz -> pages.put(getClassAnnotationValue(clazz), clazz));
    }

    /**
     * @param c класс, который должен быть аннотирован "CorePage.Name"
     * @return значение аннотации "CorePage.Name" для класса<h1 style="color: green; font-size: 2.2em">
     * Вспомогательный метод, получает значение аннотации "CorePage.Name" для класса
     */
    private String getClassAnnotationValue(Class<?> c) {
        return Arrays.stream(c.getAnnotationsByType(Name.class))
                .findAny()
                .map(Name::value)
                .orElseThrow(() -> new AssertionError("Не найдены аннотации CorePage.Name в класса " + c.getClass().getName()));
    }

    /**
     * Выводит дополнительный информационный текст в отчет (уровень логирования INFO)
     */
    public void write(Object object) {
        log.info(String.valueOf(object));
        scenario.write(String.valueOf(object));
    }

    public ScopedVariables getVars() {
        return getVariables();
    }

    public Object getVar(String name) {
        return getVariables().get(name);
    }

    public void setVar(String name, Object object) {
        getVariables().put(name, object);
    }

    public Scenario getScenario() {
        return scenario;
    }

    public Pages getPages() {
        return pages;
    }

    public CorePage getPage(String name) {
        return pages.get(name);
    }

    public <T extends CorePage> T getPage(Class<T> clazz, String name) {
        return pages.get(clazz, name);
    }

    public String replaceVariables(String textToReplaceIn) {
        return getVariables().replaceVariables(textToReplaceIn);
    }

    private ScopedVariables getVariables() {
        if (variables.get() == null) {
            variables.set(new ScopedVariables());
        }
        return variables.get();
    }
}
