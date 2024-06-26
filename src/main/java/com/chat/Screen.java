package com.chat;

import java.io.IOException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Screen {
    private int width = 0;
    private int height = 0;
    private Terminal terminal;
    
    public Screen() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean resized() {
        return width != terminal.getWidth() || height != terminal.getHeight();
    }
    
    public void adjust() {
        width = terminal.getWidth();
        height = terminal.getHeight();
        System.out.println(width + "x" + height);
    }
    
    public int getWidth() {
        return terminal.getWidth();
    }

    public int getHeight() {
        return terminal.getHeight();
    }
}
