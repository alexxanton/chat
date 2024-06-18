package com.chat;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            int width = terminal.getWidth();
            System.out.println(width);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
