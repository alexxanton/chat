package com.chat;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class TerminalHandler extends Cursor {
    private final int ESC = 27;
    private final int ENTER = 13;
    private final int BACKSPACE = 127;
    private final int WIN_BACKSPACE = 8;
    private final int LIMIT = 250;
    private int width = 0;
    private int height = 0;
    private int cursorPos = 0;
    private Terminal terminal;
    private StringBuilder line;
    private NonBlockingReader reader;
    private boolean sequenceStarted = false;
    public boolean loop = true;
    public boolean ipAssigned = false;
    

    public TerminalHandler() {
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
            if (!escapeSequenceDetected(key)) {
                if (isPrintableASCII(key) && line.length() < LIMIT) {
                    typeKey(key);
                } else if (backspacePressed(key) && cursorPos > 0) {
                    deleteKey();
                }
                displayCharCount();
            }
        }
        cursorPos = 0;
        moveCursorTo(screenHeight(), 1);
        System.out.print(CLEAR_LINE_AFTER_CURSOR);
        return line.toString();
    }

    private void typeKey(char key) {
        line.insert(cursorPos, key);
        if (key == '1') line.append("27.0.0.1"); // dev cheat TODO: remove
        cursorPos++;
        System.out.print(key);
        printLineAfterCursor();
    }

    private void deleteKey() {
        cursorPos--;
        line.deleteCharAt(cursorPos);
        System.out.print(CURSOR_BACKWARD);
        printLineAfterCursor();
    }

    private boolean isPrintableASCII(char key) {
        return key >= 32 && key <= 126;
    }

    private boolean backspacePressed(int key) {
        return key == BACKSPACE || key == WIN_BACKSPACE;
    }

    private void printLineAfterCursor() {
        System.out.print(HIDE_CURSOR + SAVE_CURSOR_POSITION);
        for (int i = cursorPos; i < line.length(); i++) {
            System.out.print(line.charAt(i));
        }
        System.out.print(CLEAR_LINE_AFTER_CURSOR + RESTORE_CURSOR_POSITION + SHOW_CURSOR);
    }

    private void displayCharCount() {
        if (!ipAssigned) return; // when prompting the IP, char count isn't displayed
        String nums = Integer.toString(line.length());
        System.out.print(SAVE_CURSOR_POSITION + HIDE_CURSOR);
        moveCursorTo(screenHeight() - 1, screenWidth() - 4 - nums.length());
        System.out.print(line.length() + "/" + LIMIT);
        System.out.print(RESTORE_CURSOR_POSITION + SHOW_CURSOR);
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
                    System.out.print(CURSOR_FORWARD);
                }
                break;
            
            case 'D':
                if (cursorPos > 0) {
                    cursorPos--;
                    System.out.print(CURSOR_BACKWARD);
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
