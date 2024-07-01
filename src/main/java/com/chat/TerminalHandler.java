package com.chat;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class TerminalHandler {
    public final int ENTER = 13;
    public final int BACKSPACE = 127;
    public final int ESC = 27;
    private int cursorPos = 0;
    private boolean escapeSequenceStarted = false;
    private int width = 0;
    private int height = 0;
    private Terminal terminal;
    private NonBlockingReader reader;
    
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

    public int readKey() {
        try {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String readKeys() {
        char key;
        StringBuilder line = new StringBuilder();
        while ((key = (char) readKey()) != ENTER) {
            if (!escapeSequenceDetected(key)) {
                if (key >= 32 && key <= 126) {
                    line.append(key);
                    System.out.print(key);
                }
                else {
                    if (key == BACKSPACE) System.out.print((Cursor.CURSOR_BACKWARD));
                }
            }
        }
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
                System.out.print(Cursor.CURSOR_FORWARD);
                cursorPos++;
                break;
            
            case 'D':
                if (cursorPos > 0) cursorPos--;
                System.out.print(Cursor.CURSOR_FORWARD);
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
