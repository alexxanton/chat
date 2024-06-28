package com.chat;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class TerminalHandler {
    private int width = 0;
    private int height = 0;
    private Terminal terminal;
    public NonBlockingReader reader;
    
    public void screen() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            reader = terminal.reader();
            terminal.enterRawMode();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

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
