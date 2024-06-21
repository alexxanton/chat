package com.chat;

public class Main {
    
    private boolean loop = true;
    private Screen screen = new Screen();
    
    public static void main(String[] args) {
        // Linux: 172.17.24.193
        // Windows: 192.168.1.114
        Main chat = new Main();
        Peer peer = new Peer();
        chat.handleScreenResize();
        peer.connect();
    }

    public void handleScreenResize() {
        Thread thread = new Thread(() -> {
            while (loop) {
                if (screen.resized()) {
                    screen.resetSize();
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
