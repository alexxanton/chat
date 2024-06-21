package com.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Peer {
    private final int PORT = 3000;
    private String ipAddress = "";
    private boolean loop = true;
    private boolean connected = false;
    private boolean loadingAnimationStarted = false;

    public void connect() {
        ipAssign();
        startClient();
        startServer();
    }

    public void startServer() {
        Thread thread = new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(PORT);
                while (loop) {
                    try {
                        Socket client = server.accept();
                        System.out.println(client.getInetAddress());
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        String line = readLine();
                        out.println(line);
                        client.close();
                    }
                    catch (Exception e) {
                        System.err.println("Error. " + e.getMessage() + ".");
                    }
                    threadSleep(100);
                }
                server.close();
            }
            catch (IOException e) {
                System.err.println("Couldnt connect to client. " + e.getMessage() + ".");
            }
        });
        thread.start();
    }

    public void startClient() {
        Thread thread = new Thread(() -> {
            
            while (loop) {
                try {
                    while (loop) {
                        Socket socket = new Socket(ipAddress, PORT);
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String serverResponse = input.readLine();
                        System.out.println(serverResponse);
                        socket.close();
                        connected = true;
                        threadSleep(100);
                    }
                }
                catch (Exception e) {
                    if (connected) System.err.print("Couldnt connect to server. " + e.getMessage() + ".");
                    else if (!loadingAnimationStarted) startLoadingAnimation();
                }
            }
        });
        thread.start();
    }

    private void startLoadingAnimation() {
        loadingAnimationStarted = true;
        Thread loadingAnimation = new Thread(() -> {
            int loadingAnimationIndex = 0;
            System.out.print(Cursor.HIDE_CURSOR);
            while (!connected) {
                System.out.print(Cursor.CHANGE_COLUMN);
                if (loadingAnimationIndex > 3) loadingAnimationIndex = 0;
                System.out.print("Couldnt connect to server. Retrying" + ".".repeat(loadingAnimationIndex));
                System.out.print(Cursor.CLEAR_LINE_AFTER_CURSOR);
                loadingAnimationIndex++;
                threadSleep(300);
            }
            System.out.print(Cursor.SHOW_CURSOR);
        });
        loadingAnimation.start();
    }

    public String readLine() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            return input.readLine();
        }
        catch (IOException e) {
            System.err.println(e.getMessage() + ".");
        }
        return "";
    }

    public void ipAssign() {
        System.out.print("Enter the IP: ");
        ipAddress = readLine();
    }

    private void threadSleep(int time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage() + ".");
        }
    }
}
