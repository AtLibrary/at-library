/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.cucumber.api;

import com.codeborne.selenide.Selenide;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Предназначен для хранения страниц, используемых при прогоне тестов
 */
public final class Pages {

    /**
     * Страницы, на которых будет производится тестирование < Имя, Страница >
     */
    private Map<String, CorePage> pages;

    /**
     * Страница, на которой в текущий момент производится тестирование
     */
    private CorePage currentPage;

    public Pages() {
        pages = Maps.newHashMap();
    }


    /**
     * Возвращает текущую страницу, на которой в текущий момент производится тестирование
     */
    public CorePage getCurrentPage() {
        if (currentPage == null) throw new IllegalStateException("Текущая страница не задана");
        return currentPage;
    }

    /**
     * Задает текущую страницу по ее имени
     */
    public void setCurrentPage(CorePage page) {
        this.currentPage = page;
    }

    /**
     * @param clazz                   класс страницы
     * @param checkIfElementsAppeared проверка всех не помеченных "@Optional" элементов
     * @author Anton Pavlov
     * Реализация анонимных методов со страницей в качестве аргумента
     */
    public static <T extends CorePage> void withPage(Class<T> clazz, boolean checkIfElementsAppeared, Consumer<T> consumer) {
        T page = getPage(clazz, checkIfElementsAppeared);
        consumer.accept(page);
    }

    /**
     * Получение страницы из "ru.bcs.at.library.core.pages" по имени
     */
    public CorePage get(String pageName) {
        return getPageMapInstanceInternal().get(pageName);
    }

    /**
     * Получение страницы по классу
     */
    @SuppressWarnings("unchecked")
    public <T extends CorePage> T get(Class<T> clazz, String name) {
        CorePage page = getPageMapInstanceInternal().get(name);
        if (!clazz.isInstance(page)) {
            throw new IllegalStateException(name + " page is not a instance of " + clazz + ". Named page is a " + page);
        }
        return (T) page;
    }

    private Map<String, ? extends CorePage> getPageMapInstanceInternal() {
        return pages;
    }

    /**
     * Добавление инстанциированной страницы в "ru.bcs.at.library.core.pages" с проверкой на NULL
     */
    public <T extends CorePage> void put(String pageName, T page) throws IllegalArgumentException {
        if (page == null)
            throw new IllegalArgumentException("Была передана пустая страница");
        pages.put(pageName, page);
    }

    /**
     * Получение страницы по классу с возможностью выполнить проверку элементов страницы
     */
    public static <T extends CorePage> T getPage(Class<T> clazz, boolean checkIfElementsAppeared) {
        T page = Selenide.page(clazz);
        if (checkIfElementsAppeared) {
            page.isAppeared();
        }
        return page;
    }

    /**
     * Добавление страницы в "ru.bcs.at.library.core.pages" по классу
     */
    public void put(String pageName, Class<? extends CorePage> clazz) {
        pages.put(pageName, Selenide.page(clazz).initialize());
    }
}
