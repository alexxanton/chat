package com.chat;

import org.jline.terminal.Cursor;

public class CursorActions {
    private final String CURSOR_FORWARD = "\033[C";
    private final String CURSOR_BACKWARD = "\033[D";
    private final String HIDE_CURSOR = "\033[?25l";
    private final String SHOW_CURSOR = "\033[?25h";
    private final String ENABLE_BLINK = "\033[?12h";
    private final String DISABLE_BLINK = "\033[?12l";
    private final String SAVE_CURSOR_POSITION = "\033[s";
    private final String RESTORE_CURSOR_POSITION = "\033[u";
    private final String CLEAR_LINE_AFTER_CURSOR = "\033[K";
    public final String MOVE_CURSOR_TO_1ST_COLUMN = "\033[G";
    private TerminalHandler terminalHandler;
    
    
    public CursorActions(TerminalHandler terminalHandler) {
        this.terminalHandler = terminalHandler;
        System.out.print(SHOW_CURSOR);
    }

    // public Cursor() {
    //     System.out.print(SHOW_CURSOR);
    // }
    

    public void moveTo(int row, int col) {
        System.out.print("\033[" + row + ";" + col + "H");
    }

    public int getX() {
        Cursor cursor = terminalHandler.getCursorPos();
        return cursor.getX();
    }

    public int getY() {
        Cursor cursor = terminalHandler.getCursorPos();
        return cursor.getY();
    }

    private void action(String action) { System.out.print(action); }

    public void forward() { action(CURSOR_FORWARD); }
    
    public void backward() { action(CURSOR_BACKWARD); }
    
    public void disableBlink() { action(DISABLE_BLINK); }

    public void enableBlink() { action(ENABLE_BLINK); }

    public void show() { action(SHOW_CURSOR); }
    
    public void hide() { action(HIDE_CURSOR); }

    public void clearLineAfterCursor() { action(CLEAR_LINE_AFTER_CURSOR); }

    public void savePosition() { action(HIDE_CURSOR + SAVE_CURSOR_POSITION); }

    public void restorePosition() { action(SHOW_CURSOR + RESTORE_CURSOR_POSITION); }
}
