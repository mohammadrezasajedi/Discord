package com.discord.server;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread{

    private ControllCenter controllCenter;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    public UserThread(ControllCenter controllCenter, Socket socket) throws IOException {
        this.controllCenter = controllCenter;
        this.socket = socket;
        writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            System.out.println("Wellcome Sent");
            writer.write("Welcome to discord.\nPlease enter your user name.\n");
            writer.newLine();
            writer.flush();
            String userName=reader.readLine();
            System.out.println(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
