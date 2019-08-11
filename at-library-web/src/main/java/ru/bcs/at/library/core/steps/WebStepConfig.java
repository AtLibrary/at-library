package ru.bcs.at.library.core.steps;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadPropertyInt;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * WEB шаги
 * </h1>
 *
 * <p style="color: green; font-size: 1.5em">
 * В coreScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем CorePage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.</p>
 */
public class WebStepConfig {
    public static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);
}
