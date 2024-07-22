package com.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Peer extends TerminalHandler {
    private final int PORT = 3000;
    private String ipAddress = "";
    private boolean searchMode = false;
    private PrintWriter output;
    private BufferedReader input;
    private static ArrayList<String> msgList = new ArrayList<>();


    public Peer() {
        super(msgList);
    }
    
    public void connect() {
        ipAssign();
        startServer();
        handleScreenResize();
        startClient();
    }
    

    // IP ADDRESS

    private void ipAssign() {
        boolean valid = false;
        cursor.moveTo(screenHeight(), 1);
        System.out.print("Enter the IP: ");
        while (!valid) {
            ipAddress = readKeys();
            valid = ipValidation();
            if (!valid) System.out.print("Invalid IP, try again: ");
        }
        ipAssigned = true;
    }

    private boolean ipValidation() {
        ipAddress = ipAddress.replaceAll("^\\s*|\\s*$", "");
        if (ipAddress.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$")) {
            return true;
        }
        return false;
    }


    // SERVER

    private void startServer() {
        Thread thread = new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(PORT);
                while (loop) {
                    try {
                        Socket client = server.accept();
                        output = new PrintWriter(client.getOutputStream(), true);
                        displayArrows();
                        String msg = readKeys();
    
                        if (!keywordDetected(msg)) {
                            if (!searchMode) {
                                msgList.add(msg);
                                cursor.moveTo(msgCount(), screenWidth() / 2);
                                System.out.println(msg);
                                cursor.moveTo(screenHeight(), 1);
                                output.println(msg);
                            }
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
                displayError("Couldn't connect to client. " + e.getMessage());
            }
        });
        thread.start();
    }


    // CLIENT

    private void startClient() {
        Thread thread = new Thread(() -> {
            while (loop) {
                try {
                    Socket socket = new Socket(ipAddress, PORT);
                    InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                    input = new BufferedReader(streamReader);
                    String msg = input.readLine();

                    if (msg != null) {
                        msgList.add(msg);
                        cursor.savePosition();
                        cursor.moveTo(msgCount(), 1);
                        System.out.println(msg);
                        cursor.restorePosition();
                        if (msg.equals("exit")) {
                            loop = false;
                            stopListeningKeys();
                        }
                    }
                    
                    socket.close();
                    threadSleep(100);
                }
                catch (Exception e) {
                    displayError("Couldn't connect to server. " + e.getMessage());
                }
            }
        });
        thread.start();
    }


    // KEYWORDS

    private boolean keywordDetected(String str) {
        String line = str.toLowerCase().replaceAll("^\\s*|\\s*$", "");
        switch (line) {
            case "find" : searchMode = true;   break;
            case "done" : searchMode = false;  break;
            case "count": displayCount();      break;
            case "quit" : closeConnection();   break;
            default:
                if (line.matches("^(goto|up|down|dwn|rm)\\s*\\d*$")) {
                    splitAndExecuteCommand(line);
                    return true;
                }
                return false;
        }
        return true;
    }

    private void splitAndExecuteCommand(String str) {
        String line = str.replaceAll("\\s", "");
        String[] splits = line.split("(?<=\\D)(?=\\d)");
        int num = 1;
        int id = msgCount(); // defaults to last message

        if (splits.length > 1) {
            num = Integer.parseInt(splits[1]);
            id = num;
        }

        if (line.contains("goto")) {
            goTo(id);
        } else if (line.contains("rm")) {
            remove(id);
        } else {
            String keyword = splits[0];
            if (keyword.equals("up")) {
                scrollUpBy(num);
            } else {
                scrollDownBy(num);
            }
        }
    }


    // COMMANDS

    private int msgCount() {
        return msgList.size();
    }

    private void displayCount() {
        String countDisplay = Integer.toString(msgCount());
        cursor.savePosition();
        cursor.moveTo(screenHeight(), screenWidth() - 9 - countDisplay.length());
        System.out.print("Messages: " + msgCount());
        cursor.restorePosition();
    }

    private void goTo(int id) {
    }

    private void remove(int id) {
    }

    private void closeConnection() {
        output.println("exit");
        loop = false;
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearScreen();
    }


    // OTHER

    private void displayArrows() {
        cursor.moveTo(screenHeight(), 1);
        String arrows = ">> ";
        if (searchMode) {
            arrows = "?> ";
        }
        System.out.print(arrows);
    }

    private void close(ServerSocket server) {
        try {
            server.close();
        } catch (IOException e) {
            System.err.println(e.getMessage() + ".");
        }
        cursor.show();
    }

    private void displayError(String error) {
        clearScreen();
        cursor.hide();
        System.err.print(error + ".\nTrying again");
        for (int i = 0; i < 3; i++) {
            threadSleep(100);
            System.out.print(".");
        }
        for (int i = 0; i < 3; i++) {
            cursor.backward();
        }
        for (int i = 0; i < 3; i++) {
            threadSleep(100);
            System.out.print(" ");
        }
        // cursor.show();
    }
}
