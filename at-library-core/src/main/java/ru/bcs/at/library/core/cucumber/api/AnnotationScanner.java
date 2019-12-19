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
package ru.bcs.at.library.core.cucumber.api;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * <h1>Reflections</h1>
 * <p>Для поиска классов с заданной аннотацией среди всех классов в проекте на основе механизма рефлексии</p>
 */
public class AnnotationScanner {

    private static Reflections reflection;

    static {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addUrls(ClasspathHelper.forPackage(""));
        reflection = new Reflections(builder);
    }

    public Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotation) {
        return reflection.getTypesAnnotatedWith(annotation);
    }

}