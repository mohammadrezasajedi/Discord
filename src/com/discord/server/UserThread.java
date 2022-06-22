package com.discord.server;

import com.discord.Command;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread{

    private ControllCenter controllCenter;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private User user;

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
            writer.write("Welcome to discord.\n");
            writer.newLine();
            writer.flush();
            writer.write(Command.GETUSERNAME.getStr());
            writer.newLine();
            writer.flush();

            boolean loop=true;
            String userName = null;
            String password = null;
            String email = null;

            while (loop) {
                try {
                    userName = reader.readLine();
                    loop = !controllCenter.checkUserName(userName);
                } catch (DuplicateException | WrongFormatException e) {
                    writer.write(Command.GETUSERNAMEAGAIN.getStr());
                    writer.newLine();
                    writer.flush();
                    writer.write(e.toString());
                    writer.newLine();
                    writer.flush();
                    loop = true;
                }
            }

            loop=true;
            writer.write(Command.GETPASSWORD.getStr());
            writer.newLine();
            writer.flush();
            while (loop) {
                try {
                    password = reader.readLine();
                    loop = !controllCenter.checkPassword(password);
                } catch (WrongFormatException e) {
                    writer.write(Command.GETPASSWORDAGAIN.getStr());
                    writer.newLine();
                    writer.flush();
                    writer.write(e.toString());
                    writer.newLine();
                    writer.flush();
                    loop = true;
                }
            }

            loop=true;
            writer.write(Command.GETEMAIL.getStr());
            writer.newLine();
            writer.flush();
            while (loop) {
                try {
                    email = reader.readLine();
                    loop = !controllCenter.checkEmail(email);
                } catch (WrongFormatException e) {
                    writer.write(Command.GETEMAILAGAIN.getStr());
                    writer.newLine();
                    writer.flush();
                    writer.write(e.toString());
                    writer.newLine();
                    writer.flush();
                    loop = true;
                }
            }

            user=controllCenter.createUser(userName,password,email);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
