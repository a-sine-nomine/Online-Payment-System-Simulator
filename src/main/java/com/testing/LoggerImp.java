package com.testing;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.FileInputStream;
import java.io.IOException;

public class LoggerImp {

    private String log4jConfigFile = "configuration/log4j2.xml";
    private ConfigurationSource source;

    {
        try {
            source = new ConfigurationSource(new FileInputStream(log4jConfigFile));
            Configurator.initialize(null, source);
        } catch (IOException e) {
            throw new RuntimeException("LoggerImpExeption");
        }

    }
    private static final Logger logger = LogManager.getLogger("com.testing");

    public static synchronized void logDebug(String message) {
        logger.debug(message);
    }

    public static synchronized void logInfo(String message) {
        logger.info(message);
    }

    public static synchronized void logError(String message) {
        logger.error(message);
    }
}