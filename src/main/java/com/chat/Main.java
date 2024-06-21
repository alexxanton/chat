package com.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private final int PORT = 3000;
    private String ipAddress = "";
    private boolean loop = true;
    private Screen screen = new Screen();
    
    public static void main(String[] args) {
        // Linux: 172.17.24.193
        // Windows: 192.168.1.114
        Main chat = new Main();
        chat.handleScreenResize();
        chat.ipAssign();
        chat.startServer();
        chat.startClient();
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
                    threadSleep();
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
            try {
                while (loop) {
                    Socket socket = new Socket(ipAddress, PORT);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverResponse = input.readLine();
                    System.out.println(serverResponse);
                    socket.close();
                    threadSleep();
                }
            }
            catch (Exception e) {
                System.err.println("Couldnt connect to server. " + e.getMessage() + ".");
            }
        });
        thread.start();
    }

    public String readLine() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            return input.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void ipAssign() {
        System.out.print("Enter the IP: ");
        ipAddress = readLine();
    }

    public void handleScreenResize() {
        Thread thread = new Thread(() -> {
            while (loop) {
                if (screen.resized()) {
                    screen.resetSize();
                }
                threadSleep();
            }
        });
        thread.start();
    }

    private void threadSleep() {
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
