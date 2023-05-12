package com.testing;

import org.jpos.iso.ISOException;

import java.io.*;
import java.net.Socket;


public class ServerThread extends Thread {
    private final Socket socket;
    private InputStream in;
    private OutputStream out;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            if (socket.isConnected()) {
                LoggerImp.logInfo("Client connected from: " + socket.getRemoteSocketAddress());
            }
            in = socket.getInputStream();
            out = socket.getOutputStream();
            ISOParserv2.startTesting(in, out);
            LoggerImp.logInfo("Test passed successfully");
        } catch (IOException e) {
            LoggerImp.logError(e.getMessage());
            e.printStackTrace();
        } catch (ScenarioException e) {
            LoggerImp.logInfo("Test failed: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                LoggerImp.logError("Failed to close connection");
            }

        }
    }
}
