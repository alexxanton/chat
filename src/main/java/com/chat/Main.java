package com.chat;

public class Main {
    public static void main(String[] args) {
        Screen screen = new Screen();
        Thread thread = new Thread(() -> {
            while (true) {
                if (screen.resized()) {
                    screen.resetSize();
                }

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
