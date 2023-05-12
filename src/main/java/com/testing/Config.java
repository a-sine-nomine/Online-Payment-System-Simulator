package com.testing;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Properties properties;
    private static Reader reader;

    static {
        try {
            properties = new Properties();
            reader = new BufferedReader(new InputStreamReader(Config.class.getClassLoader().getResourceAsStream("configuration/config.properties"), "utf-8"));
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getServerPort() {
        return properties.getProperty("server.port");
    }
}
