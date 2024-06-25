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
    private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public void connect() {
        System.out.println(Cursor.SHOW_CURSOR);
        ipAssign();
        startClient();
        startServer();
    }

    private void ipAssign() {
        System.out.print("Enter the IP: ");
        boolean valid = false;
        while (!valid) {
            ipAddress = readLine();
            valid = ipValidation();
            if (valid) return;
            System.out.print("Invalid IP, try again: ");
        }
    }

    private boolean ipValidation() {
        String numRange = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        String dot = "\\.";
        String regex = "^" + numRange + dot + numRange + dot + numRange + dot + numRange + "$";
        if (ipAddress.matches(regex) || ipAddress.isEmpty()) {
            return true;
        }
        return false;
    }

    private void startServer() {
        Thread thread = new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(PORT);
                while (loop) {
                    try {
                        Socket client = server.accept();
                        PrintWriter output = new PrintWriter(client.getOutputStream(), true);
                        String line = readLine();
                        if (!keywordDetected(line.replaceAll("\\s", ""))) {
                            output.println(line);
                        }
                        client.close();
                        threadSleep(100);
                    }
                    catch (Exception e) {
                        System.err.println("Error. " + e.getMessage() + ".");
                    }
                }
                close(server);
            }
            catch (IOException e) {
                System.err.println("Couldn't connect to client. " + e.getMessage() + ".");
            }
        });
        thread.start();
    }

    private boolean keywordDetected(String line) {
        switch (line.toLowerCase()) {
            case "exit":
                loop = false;
                break;
        
            default:
                return false;
        }
        return true;
    }

    private void startClient() {
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
                    if (connected) System.err.println("Couldn't connect to server. " + e.getMessage() + ".");
                    else if (!loadingAnimationStarted) startLoadingAnimation();
                }
            }
        });
        thread.start();
    }

    private String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage() + ".");
            return "";
        }
    }

    private void close(ServerSocket server) {
        try {
            server.close();
            input.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + ".");
        }
        System.out.println(Cursor.SHOW_CURSOR);
        System.exit(0);
    }

    private void startLoadingAnimation() {
        loadingAnimationStarted = true;
        Thread loadingAnimation = new Thread(() -> {
            int index = 0;
            System.out.print(Cursor.HIDE_CURSOR);
            while (!connected) {
                System.out.print(Cursor.CHANGE_COLUMN);
                if (index > 3) index = 0;
                System.out.print("Couldn't connect to server. Retrying" + ".".repeat(index));
                System.out.print(Cursor.CLEAR_LINE_AFTER_CURSOR);
                index++;
                threadSleep(300);
            }
            System.out.print(Cursor.SHOW_CURSOR);
        });
        loadingAnimation.start();
    }

    private void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage() + ".");
        }
    }
}
