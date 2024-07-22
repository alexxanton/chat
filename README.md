# Local Terminal Chat

This is a peer-to-peer (P2P) chat made with Java. It works with the Linux and Windows terminals. The chat includes some features such as message history, search mode, screen resizing, and command handling.

## How to use
1. To execute you either need to have maven installed or use the jar `java -jar chat-1.0-SNAPSHOT.jar`.
2. Both users have to enter each other's IP. If you want to chat with yourself type `0.0.0.0`, `127.0.0.1` or your own IP.
3. Use the arrow keys `(up/down)` to scroll.

## Commands
- **up/down:** Instead of scrolling message by message you can for example type `up 10` and you'll scroll up by 10 messages.
- **goto:** Go to a specific message `goto 100`. goto by itself will take you to the last message.
- **rm:** removes the indicated message `rm 10`. If no number is provided, the last one will be removed.
- **count:** displays the number of messages.
- **find:** enables search mode, type anything you want to find from your messages.
- **done:** disables search mode.
- **quit:** close connection.