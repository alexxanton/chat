package com.chat;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class TerminalHandler {
    private final int ENTER = 13;
    private final int BACKSPACE = 127;
    private final int ESC = 27;
    private int cursorPos = 0;
    private boolean escapeSequenceStarted = false;
    private int width = 0;
    private int height = 0;
    private Terminal terminal;
    private NonBlockingReader reader;
    private StringBuilder line;
    
    public TerminalHandler() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            reader = terminal.reader();
            terminal.enterRawMode();
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
        while ((key = readKey()) != ENTER) {
            if (!escapeSequenceDetected(key)) {
                if (key >= 32 && key <= 126) {
                    line.insert(cursorPos, key);
                    cursorPos++;
                    System.out.print(key);
                }
                else {
                    if (key == BACKSPACE) {
                        cursorPos--;
                        line.deleteCharAt(cursorPos);
                        System.out.print((Cursor.CURSOR_BACKWARD));
                    }
                }
            }
        }
        cursorPos = 0;
        System.out.print("\n");
        return line.toString();
    }


    // ESCAPE SEQUENCES

    private boolean escapeSequenceDetected(char key) {
        if (key == ESC) {
            escapeSequenceStarted = true;
            return true;
        } else if (escapeSequenceStarted) {
            if (key == '[') {
                return true;
            } else {
                escapeSequenceStarted = false;
                return escapeSequenceExecuted(key);
            }
        }
        return false;
    }

    private boolean escapeSequenceExecuted(char key) {
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
                    System.out.print(Cursor.CURSOR_FORWARD);
                }
                break;
            
            case 'D':
                if (cursorPos > 0) {
                    cursorPos--;
                    System.out.print(Cursor.CURSOR_BACKWARD);
                }
                break;
        
            default:
                escapeSequenceStarted = false;
                return false;
        }
        return true;
    }

    public void scrollUpBy(int amount) {
    }

    public void scrollDownBy(int amount) {
    }


    // SCREEN
    
    public boolean resized() {
        return width != terminal.getWidth() || height != terminal.getHeight();
    }
    
    public void adjust() {
        width = terminal.getWidth();
        height = terminal.getHeight();
        // System.out.println(width + "x" + height);
    }
    
    public int getWidth() {
        return terminal.getWidth();
    }

    public int getHeight() {
        return terminal.getHeight();
    }
}
