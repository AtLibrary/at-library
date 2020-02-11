package ru.bcs.at.library.core.setup;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

public class AtCoreConfig {

    public static String platformName = tryLoadProperty("platformName");
    public static String deviceName = tryLoadProperty("deviceName");
    public static String platformVersion = tryLoadProperty("platformVersion");
    public static String version = tryLoadProperty("version");
    public static String app = tryLoadProperty("app");
    public static String udid = tryLoadProperty("udid");
    public static String appPackageName = tryLoadProperty("appPackageName");
    public static boolean debugCore = Boolean.parseBoolean(tryLoadProperty("debug.core"));
    public static boolean isAppeared = Boolean.parseBoolean(tryLoadProperty("isAppeared"));
    public static String wdaLocalPort = tryLoadProperty("wdaLocalPort");
    public static boolean showLogCode = Boolean.parseBoolean(tryLoadProperty("showLogCode"));
    public static boolean clearSystemFiles = Boolean.parseBoolean(tryLoadProperty("clearSystemFiles"));
    public static String automationName = tryLoadProperty("automationName");
    public static String webDriverAgentUrl = tryLoadProperty("webDriverAgentUrl");
    public static String deviceList = tryLoadProperty("deviceList");
}


