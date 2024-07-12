package com.chat;

import org.jline.terminal.Cursor;

public class CursorActions {
    public final String CURSOR_UP = "\033[A";
    public final String CURSOR_DOWN = "\033[B";
    public final String CURSOR_FORWARD = "\033[C";
    public final String CURSOR_BACKWARD = "\033[D";
    public final String HIDE_CURSOR = "\033[?25l";
    public final String SHOW_CURSOR = "\033[?25h";
    public final String ENABLE_BLINK = "\033[?12h";
    public final String DISABLE_BLINK = "\033[?12l";
    public final String SAVE_CURSOR_POSITION = "\033[s";
    public final String RESTORE_CURSOR_POSITION = "\033[u";
    public final String CLEAR_LINE_AFTER_CURSOR = "\033[K";
    public final String CLEAR_SCREEN_AFTER_CURSOR = "\033[J";
    public final String MOVE_CURSOR_TO_1ST_COLUMN = "\033[G";
    public final String CHANGE_CURSOR_COLOR_RED = "\033]12;red\007";
    public final String CHANGE_CURSOR_COLOR_WHITE = "\033]12;white\007";
    private TerminalHandler terminal;
    
    
    public CursorActions(TerminalHandler terminal) {
        this.terminal = terminal;
        System.out.print(SHOW_CURSOR);
    }

    // public Cursor() {
    //     System.out.print(SHOW_CURSOR);
    // }
    

    public void moveTo(int row, int col) {
        System.out.print("\033[" + row + ";" + col + "H");
    }

    public int getX() {
        Cursor cursor = terminal.getCursorPos();
        return cursor.getX();
    }

    public int getY() {
        Cursor cursor = terminal.getCursorPos();
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

    public void changeColorRed() { action(CHANGE_CURSOR_COLOR_RED); }

    public void changeColorWhite() { action(CHANGE_CURSOR_COLOR_WHITE); }
}
