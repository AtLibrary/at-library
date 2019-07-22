/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p style="color: green; font-size: 1.5em">
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p style="color: green; font-size: 1.5em">
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.cucumber.utils;

import java.lang.reflect.Field;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * Реализация механизма рефлексии для доступа к аннотациям классов
 * </h1>
 * <p style="color: green; font-size: 1.5em">
 * Необходимо для сбора списка страниц, на которых будет производиться тестирование и для сбора элементов с этих страниц
 * </p>
 */
public final class Reflection {

    private Reflection() {

    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Получение поля класса с помощью механизма рефлексии</p>
     */
    public static Object extractFieldValue(Field field, Object owner) {
        field.setAccessible(true);
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }
}
