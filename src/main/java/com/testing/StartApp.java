package com.testing;

public class StartApp {
    public static void main(String[] args) {
        LoggerImp.logInfo("Starting Application");

        try {
            Server.startServer();
        } catch (Exception e) {
            LoggerImp.logError(e.getMessage());
        }
    }
}
