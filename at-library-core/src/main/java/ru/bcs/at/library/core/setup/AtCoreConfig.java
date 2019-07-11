package ru.bcs.at.library.core.setup;

public class AtCoreConfig {

    private static AtCoreConfig instance;

    public static String platformName = System.getProperty("platformName", "iOS");
    public static String deviceName = System.getProperty("deviceName", "iPhone 6s");
    public static String platformVersion = System.getProperty("platformVersion", "12.2");
    public static String app = System.getProperty("app", "ru.admitadteam.Cooking-eggs");
    public static boolean debugCore = Boolean.getBoolean(System.getProperty("debug.core", "false"));

    private AtCoreConfig() {
    }

    public static synchronized AtCoreConfig getInstance() {
        if (instance == null) {
            instance = new AtCoreConfig();
        }
        return instance;
    }
}


