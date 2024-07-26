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
    private PrintWriter sender;
    private BufferedReader receiver;
    private static boolean ipAssigned = false;
    private static ArrayList<String> msgList = new ArrayList<>();


    public Peer() {
        super(msgList, ipAssigned);
    }
    
    public void connect() {
        ipAssign();
        startSender();
        handleScreenResize();
        startReceiver();
    }
    

    // IP ADDRESS

    private void ipAssign() {
        cursor.moveTo(screenHeight(), 1);
        System.out.print("Enter the IP: ");
        while (!ipAssigned) {
            ipAddress = readKeys();
            ipAssigned = ipValidation();
        }
    }

    private boolean ipValidation() {
        ipAddress = ipAddress.replaceAll("^\\s*|\\s*$", "");
        if (ipAddress.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|(\\@[a-z]+)*$)){4}$")) {
            if (ipAddress.contains("@")) {
                String[] splits = ipAddress.split("\\@");
                ipAddress = splits[0];
                String userName = splits[1];
                System.out.println("Chating with " + userName);
            }
            return true;
        }
        System.out.print("Invalid IP, try again: ");
        return false;
    }


    // SENDER

    private void startSender() {
        Thread thread = new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(PORT);
                while (loop) {
                    try {
                        Socket client = server.accept();
                        sender = new PrintWriter(client.getOutputStream(), true);
                        displayArrows();
                        String msg = readKeys();
    
                        if (!keywordDetected(msg)) {
                            if (!searchMode) {
                                msgList.add(msg);
                                cursor.moveTo(msgCount(), screenWidth() / 2);
                                System.out.println(msg);
                                cursor.moveTo(screenHeight(), 1);
                                sender.println(msg);
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


    // RECEIVER

    private void startReceiver() {
        Thread thread = new Thread(() -> {
            while (loop) {
                try {
                    Socket socket = new Socket(ipAddress, PORT);
                    InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                    receiver = new BufferedReader(streamReader);
                    String msg = receiver.readLine();

                    if (msg != null) {
                        msgList.add(msg);
                        cursor.savePosition();
                        cursor.moveTo(msgCount(), 1);
                        System.out.println(msg);
                        cursor.restorePosition();
                        if (msg.equals("quit")) {
                            stopListeningKeys();
                            closeConnection();
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
            case "find" : searchMode = true;    break;
            case "done" : searchMode = false;   break;
            case "count": displayCount();       break;
            case "quit" : closeConnection();    break;
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
        String keyword = splits[0];
        int num = 1; // default amount
        int id = msgCount(); // defaults to last message

        if (splits.length > 1) {
            num = Integer.parseInt(splits[1]);
            id = num;
        }

        switch (keyword) {
            case "goto" :   goTo(id);           break;
            case "rm"   :   remove(id);         break;
            case "up"   :   scrollUpBy(num);    break;
            default     :   scrollDownBy(num);  break;
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
        sender.println("quit");
        loop = false;
        try {
            receiver.close();
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
