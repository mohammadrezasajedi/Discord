package com.discord.server.utils;

import java.io.*;
import java.net.Socket;

public class NotificationStream {

    private Socket socket;

    private BufferedWriter out;

    public NotificationStream(Socket socket) throws IOException {
        this.socket = socket;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendPopUp (String title,String desc){
        try {
            methodWrite("popUp");
            methodWrite(title);
            methodWrite(desc);
        } catch (IOException e){
            System.err.println("User stream closed.");
        }
    }

    private void methodWrite (String str) throws IOException {
        out.write(str);
        out.newLine();
        out.flush();
    }

    public void close () {
        try {
            methodWrite("exit");
            out.close();
        } catch (IOException e){
            Thread.currentThread().interrupt();
        }
    }
}
