package com.discord.client;

import com.discord.server.UserThread;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class Client {
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Scanner sc;
    public static void main(String[] args) {
        try {
            Socket socket=new Socket("localhost",8989);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sc=new Scanner(System.in);
            System.out.println("Connectted");
            String str = reader.readLine();
            System.out.println(str);
            writer.write(sc.nextLine());
            writer.newLine();
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();

        }
    }





}
