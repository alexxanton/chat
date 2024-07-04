package com.chat;

public class Cursor {
    public Cursor() {
        System.out.print(SHOW_CURSOR);
    }
    
    public static final String CURSOR_UP = "\033[A";
    public static final String CURSOR_DOWN = "\033[B";
    public static final String CURSOR_FORWARD = "\033[C";
    public static final String CURSOR_BACKWARD = "\033[D";
    public static final String HIDE_CURSOR = "\033[?25l";
    public static final String SHOW_CURSOR = "\033[?25h";
    public static final String ENABLE_BLINK = "\033[?12h";
    public static final String DISABLE_BLINK = "\033[?12l";
    public static final String SAVE_CURSOR_POSITION = "\033[s";
    public static final String RESTORE_CURSOR_POSITION = "\033[u";
    public static final String CLEAR_LINE_AFTER_CURSOR = "\033[K";
    public static final String CLEAR_SCREEN_AFTER_CURSOR = "\033[J";
    public static final String MOVE_CURSOR_TO_1ST_COLUMN = "\033[G";
    public static final String CHANGE_CURSOR_COLOR_RED = "\033]12;red\007";
    public static final String CHANGE_CURSOR_COLOR_WHITE = "\033]12;white\007";
}
