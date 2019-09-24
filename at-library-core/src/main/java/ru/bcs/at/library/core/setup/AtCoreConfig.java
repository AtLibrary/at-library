package ru.bcs.at.library.core.setup;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

public class AtCoreConfig {

    public static String platformName = tryLoadProperty("platformName");
    public static String deviceName = tryLoadProperty("deviceName");
    public static String platformVersion = tryLoadProperty("platformVersion");
    public static String app = tryLoadProperty("app");
    public static String appPackageName = tryLoadProperty("appPackageName");
    public static boolean debugCore = Boolean.getBoolean(tryLoadProperty("debug.core"));
    public static boolean isAppeared = Boolean.getBoolean(tryLoadProperty("isAppeared"));
}


