package com.discord.server;

import com.discord.Command;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;

public class UserThread extends Thread{

    private final ControllCenter controllCenter;
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;
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
            methodWrite("Welcome to discord.\n");

            int choose=0;
            methodWrite(Command.INITMENU.getStr());
            methodWrite("1.Login 2.Signup");
            boolean loop=true;
            while (loop){
                try {
                    choose=Integer.parseInt(methodRead());
                    if (!(choose==1 || choose==2)){
                        throw new WrongFormatException(null);
                    }
                    loop=false;
                }
                catch (NumberFormatException | WrongFormatException e){
                    methodWrite(Command.INITMENU.getStr());
                    methodWrite("Your input is not valid");
                }
            }
            if (choose==1){
                login();
            }else {
                signUp();
            }



        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }


    }

    private void signUp () throws IOException {
        boolean loop=true;
        String userName = null;
        String password = null;
        String email = null;

        methodWrite(Command.GETUSERNAME.getStr());

        while (loop) {
            try {
                userName = reader.readLine();
                loop = !controllCenter.checkUserName(userName);
            } catch (DuplicateException | WrongFormatException e) {
                methodWrite(Command.GETUSERNAMEAGAIN.getStr());


                methodWrite(e.toString());


                loop = true;
            }
        }

        loop=true;
        methodWrite(Command.GETPASSWORD.getStr());


        while (loop) {
            try {
                password = reader.readLine();
                loop = !controllCenter.checkPassword(password);
            } catch (WrongFormatException e) {
                methodWrite(Command.GETPASSWORDAGAIN.getStr());


                methodWrite(e.toString());


                loop = true;
            }
        }

        loop=true;
        methodWrite(Command.GETEMAIL.getStr());


        while (loop) {
            try {
                email = reader.readLine();
                loop = !controllCenter.checkEmail(email);
            } catch (WrongFormatException e) {
                methodWrite(Command.GETEMAILAGAIN.getStr());


                methodWrite(e.toString());


                loop = true;
            }
        }

        user=controllCenter.createUser(userName,password,email);
    }

    private void login(){
        boolean loop=true;
        String userName;
        String password;
        while (loop){
            try {
                methodWrite(Command.GETUSERNAME.getStr());
                userName=methodRead();
                methodWrite(Command.GETPASSWORD.getStr());
                password=methodRead();
                user=controllCenter.findUser(userName,password);
                if (user!=null){
                    loop=false;
                }else {
                    methodWrite(Command.PRINT.getStr());
                    methodWrite("Your user name or password is not valid");
                    loop=true;
                }
            }catch (IOException e){
                e.printStackTrace();
                loop=false;
            }


        }


    }

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }

    private void methodWrite (String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }
}
