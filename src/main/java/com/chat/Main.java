package com.chat;

public class Main {
    private boolean loop = true;
    private Screen screen = new Screen();
    
    public static void main(String[] args) {
        Main chat = new Main();
        Peer peer = new Peer();
        chat.handleScreenResize();
        peer.connect();
    }

    public void handleScreenResize() {
        Thread thread = new Thread(() -> {
            while (loop) {
                if (screen.resized()) {
                    screen.adjust();
                }
                threadSleep();
            }
        });
        thread.start();
    }

    private void threadSleep() {
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage() + ".");
        }
    }
}
