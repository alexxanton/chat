package com.chat;

import java.io.IOException;
import java.util.ArrayList;

import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class TerminalHandler {
    private final int ESC = 27;
    private final int ENTER = 13;
    private final int BACKSPACE = 127;
    private final int WIN_BACKSPACE = 8;
    private final int MAX_LENGTH = 150;
    private int width = 0;
    private int height = 0;
    private int cursorPos = 0;
    private Terminal terminal;
    private StringBuilder line;
    private NonBlockingReader reader;
    private boolean sequenceStarted = false;
    public boolean loop = true;
    public boolean ipAssigned = false;
    public CursorActions cursor;
    private ArrayList<String> msgList;


    public TerminalHandler(ArrayList<String> msgList) {
        this.cursor = new CursorActions(this);
        this.msgList = msgList;
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            reader = terminal.reader();
            terminal.enterRawMode();
            clearScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // KEY READER

    private char readKey() {
        try {
            return (char) reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String readKeys() {
        char key;
        line = new StringBuilder();
        displayCharCount();
        while ((key = readKey()) != ENTER || line.isEmpty()) {
            devCheat(key); // dev cheat TODO: remove
            if (!escapeSequenceDetected(key)) {
                if (isPrintableASCII(key) && line.length() < MAX_LENGTH) {
                    type(key);
                } else if (backspacePressed(key) && cursorPos > 0) {
                    delete();
                }
                displayCharCount();
            }
        }
        cursorPos = 0;
        cursor.moveTo(screenHeight(), 1);
        cursor.clearLineAfterCursor();
        return line.toString();
    }

    private void devCheat(char key) {
        if (key == 209) {
            line.append("192.168.1.114");
            System.out.print("win");
        }
        if (key == 241) {
            line.append("172.17.24.193");
            System.out.print("lnx");
        }
    }

    private void type(char key) {
        line.insert(cursorPos, key);
        cursorPos++;
        System.out.print(key);
        printLineAfterCursor();
    }

    private void delete() {
        cursorPos--;
        line.deleteCharAt(cursorPos);
        cursor.backward();
        printLineAfterCursor();
    }

    private boolean isPrintableASCII(char key) {
        return key >= 32 && key <= 126;
    }

    private boolean backspacePressed(int key) {
        return key == BACKSPACE || key == WIN_BACKSPACE;
    }

    private void printLineAfterCursor() {
        cursor.savePosition();
        for (int i = cursorPos; i < line.length(); i++) {
            System.out.print(line.charAt(i));
        }
        cursor.clearLineAfterCursor();
        cursor.restorePosition();
    }

    private void displayCharCount() {
        if (!ipAssigned) return; // when prompting the IP, char count isn't displayed
        String nums = Integer.toString(line.length());
        cursor.savePosition();
        cursor.moveTo(screenHeight() - 1, screenWidth() - 4 - nums.length());
        System.out.print(line.length() + "/" + MAX_LENGTH);
        cursor.restorePosition();
    }


    // ESCAPE SEQUENCES

    private boolean escapeSequenceDetected(char key) {
        if (key == ESC) {
            sequenceStarted = true;
            return true;
        } else if (sequenceStarted) {
            if (isEscapeCharacter(key)) {
                return true;
            } else {
                sequenceStarted = false;
                return sequenceExecuted(key);
            }
        }
        return false;
    }

    private boolean isEscapeCharacter(char key) {
        return key == '[' || key == ';' || key == '1' || key == '3' || (key >= '5' && key <= '8') || key == 'O';
    }

    private boolean sequenceExecuted(char key) {
        switch (key) {
            case 'A':
                scrollUpBy(1);
                break;
                
            case 'B':
                scrollDownBy(1);
                break;
            
            case 'C':
                if (cursorPos < line.length()) {
                    cursorPos++;
                    cursor.forward();
                }
                break;
            
            case 'D':
                if (cursorPos > 0) {
                    cursorPos--;
                    cursor.backward();
                }
                break;
        
            default:
                return false;
        }
        return true;
    }

    public void scrollUpBy(int amount) {
    }

    public void scrollDownBy(int amount) {
    }


    public Cursor getCursorPos() {
        return terminal.getCursorPosition(null);
    }


    // SCREEN

    public boolean screenResized() {
        return width != terminal.getWidth() || height != terminal.getHeight();
    }
    
    public void adjustScreen() {
        width = terminal.getWidth();
        height = terminal.getHeight();
        clearScreen();
        // System.out.println(width + "x" + height);
    }

    public void refreshScreen() {
        System.out.println(msgList);
    }
    
    public int screenWidth() {
        return terminal.getWidth();
    }

    public int screenHeight() {
        return terminal.getHeight();
    }

    public void handleScreenResize() {
        Thread thread = new Thread(() -> {
            while (loop) {
                if (screenResized()) {
                    adjustScreen();
                }
                threadSleep(100);
            }
        });
        thread.start();
    }

    public void clearScreen() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage() + ".");
        }
    }
}
