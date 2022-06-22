package com.discord.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ControllCenter controllCenter;

    public static void main(String[] args) {
        controllCenter=new ControllCenter();
        boolean flag=true;
        while (flag) {
            try {
                ServerSocket serverSocket = new ServerSocket(8989);
                while (true) {
                    Socket socket = serverSocket.accept();
                    controllCenter.init(socket);
                    System.out.println("User connected");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
