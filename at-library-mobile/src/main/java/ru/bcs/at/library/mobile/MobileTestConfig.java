package ru.bcs.at.library.mobile;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadPropertyInt;

public class MobileTestConfig {
    public static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10);
}
