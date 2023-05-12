package com.testing;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    static Scanner consoleIn = new Scanner(System.in);
    public static void startServer() {
        String serverPort = Config.getServerPort();
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(serverPort))) {
            LoggerImp.logInfo("Starting server at port: " + serverPort);
            while (true)
            {
                if (System.in.available() > 0)
                {
                    String input = consoleIn.next();
                    if("stop".equals(input))
                        break;
                }
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket).start();
            }
        } catch (Exception e) {
            LoggerImp.logError(e.getMessage());
            LoggerImp.logDebug(Arrays.toString(e.getStackTrace()));
        }
    }
}
