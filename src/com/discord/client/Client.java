package com.discord.client;

import com.discord.Command;
import com.discord.server.UserThread;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class Client {

    private static BufferedReader reader;
    private static BufferedWriter writer;
    public static void main(String[] args) {
        try {
            Socket socket=new Socket("localhost",8989);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connected");
            String str = methodRead();
            UI.welcome(str);
            boolean end=false;
            while (!end) {
                Command command=Command.valueOfLabel(methodRead());
                String respond = null;
                switch (command){
                    case GETUSERNAME:{
                        respond = UI.getUserName();
                        break;
                    }
                    case GETUSERNAMEAGAIN:{
                        respond=UI.getUserName(methodRead());
                        break;
                    }
                    case GETPASSWORD:{
                        respond=UI.getPassword();
                        break;
                    }
                    case GETPASSWORDAGAIN:{
                        respond=UI.getPassword(methodRead());
                        break;
                    }
                    case GETEMAIL:{
                        respond=UI.getEmail();
                        break;
                    }
                    case GETEMAILAGAIN:{
                        respond=UI.getEmail(methodRead());
                        break;
                    }
                    case INITMENU:{
                        respond=UI.initMenu(methodRead());
                        break;
                    }
                    case PRINT:{
                        UI.print(methodRead());
                        break;
                    } case SHOWMENU:{
                        respond=UI.ShowMenu(methodRead());
                        break;
                    }

                    case EXIT:{
                        UI.exit();
                        System.exit(0);
                    }

                }




                if (respond != null) {
                    writer.write(respond);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e){
            System.err.println("Server Unreachable!");
            System.exit(-1);
        }
    }

    private static String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }





}
