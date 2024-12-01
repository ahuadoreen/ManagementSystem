package com.example.tools.utils;

import java.util.Properties;

public class YamlConfigureUtil {
    private static Properties ymlProperties;
    public YamlConfigureUtil(Properties properties) {
        ymlProperties = properties;
    }
    public static String getStrYmlVal(String key) {
        return ymlProperties.getProperty(key);
    }
    public static Integer getIntegerYmlVal(String key) {
        return Integer.valueOf(ymlProperties.getProperty(key));
    }
}
