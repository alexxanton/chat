package com.chat;

public class Main {
    
    public static void main(String[] args) {
        Peer peer = new Peer();
        peer.connect();
    }

    // public void handleScreenResize() {
    //     Thread thread = new Thread(() -> {
    //         while (loop) {
    //             if (screen.resized()) {
    //                 screen.adjust();
    //             }
    //             threadSleep();
    //         }
    //     });
    //     thread.start();
    // }
}
