package com.discord.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private static ControllCenter controllCenter;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file=new File("CotrollCenterFile.bin");
        if (file.exists()){
            ObjectInputStream read= null;
            try {
                 read = new ObjectInputStream(new FileInputStream(file));
                controllCenter = new ControllCenter((ControllCenter) read.readObject());
            } catch (InvalidClassException | EOFException e){
                controllCenter=new ControllCenter();
            }
            if (read != null) {
                read.close();
            }
        }else {
            controllCenter=new ControllCenter();
        }
        final boolean[] flag = {true};
        Thread thread=new Thread(() -> {
            Scanner sca=new Scanner(System.in);
            while (true){
                String string=sca.nextLine();
                if (string.equals("#exit")){
                    try {
                        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file));
                        out.writeObject(controllCenter);
                        out.close();
                        flag[0] =false;
                        System.exit(0);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        while (flag[0]) {
            try {
                ServerSocket serverSocket = new ServerSocket(8989);
                ServerSocket fileSocket = new ServerSocket(8888);
                controllCenter.setFileSocket(fileSocket);
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
