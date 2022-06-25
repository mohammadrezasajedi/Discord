package com.discord.server;

import com.discord.Command;
import com.discord.server.utils.discordServer.DiscordServer;
import com.discord.server.utils.PrivateChat;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
            boolean app = true;
            while (app) {
                System.out.println("Wellcome Sent");
                methodWrite("Welcome to discord.\n");

                int choose = showMenu("1.Login\n2.Signup\n3.exit", 3);
                if (choose == 1) {
                    login();
                } else if (choose == 2){
                    signUp();
                } else {
                    app = false;
                    exit();
                    break;
                }

                if (user.getStatus() == User.Status.OFFLINE) {
                    user.setStatus(User.Status.ONLINE);
                }

                methodWrite(Command.PRINT.getStr());
                methodWrite("Well Come " + user.getUserName());


                boolean loop = true;

                while (loop) {
                    choose = showMenu("1.Friends\n2.Private chat\n3.Discord servers\n4.Profile\n5.logout", 5);

                    switch (choose) {
                        case 1: {
                            fMenu();
                            break;
                        }
                        case 2: {
                            pChatMenu();
                            break;
                        }
                        case 3: {
                            serverMenu();
                            break;
                        }
                        case 4: {
                            profile();
                            break;
                        }
                        case 5: {
                            logOut();
                            loop = false;
                            break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            Thread.currentThread().interrupt();
            if (user.getStatus()== User.Status.ONLINE){
                user.setStatus(User.Status.OFFLINE);
            }
        }
    }

    private void fMenu() throws IOException {
        boolean loop = true;
        while (loop) {
            int choose = showMenu("1.exit\n2.create new friend\n3.show friends\n4.friend requests", 4);
            switch (choose) {
                case 1: {
                    loop=false;
                    break;
                }
                case 2: {
                    createNewFriend();
                    break;
                }
                case 3: {
                    showFriends();
                    break;
                }
                case 4: {
                    friendRequest();
                    break;
                }
            }
        }
    }

    private void createNewFriend() throws IOException {
        methodWrite(Command.CREATEFRIEND.getStr());
        String userName=methodRead();
        User user=controllCenter.findUser(userName);
        String str;
        if (user==null){
            str="User is not found.";
        }else {
            if (user.getRequests().contains(this.user)){
                str = "You already have a pending request";
            }else {
                user.addRequest(this.user);
                str="Your Friend Request has been sent.";
            }
        }
        methodWrite(Command.PRINT.getStr());
        methodWrite(str);
    }

    private void showFriends() throws IOException {
        for (User u:user.getFriends()) {
            methodWrite(Command.PRINT.getStr());
            methodWrite(u.getUserName()+" : "+u.getStatus().getName());
        }
    }
    private void friendRequest() throws IOException {
        boolean loop=true;
        while (loop){
            StringBuilder sb=new StringBuilder();
            sb.append("1.exit\n");
            int index=2;
            for (User u:user.getRequests()) {
                sb.append(index++);
                sb.append('.');
                sb.append(u.getUserName());
                sb.append("\n");
            }
            int choose=showMenu(sb.toString(),index - 1);
            if (choose==1){
                loop=false;
            }else {
                User u = user.getRequests().get(choose - 2);
                methodWrite(Command.PRINT.getStr());
                methodWrite("Accept " + u.getUserName() + "?");
                int choice=showMenu("1.YES\n2.NO",2);
                if (choice==1){
                    u.addFriend(user);
                    user.addFriend(u);
                }
                user.getRequests().remove(u);
            }
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

    private void login() throws IOException {
        boolean loop=true;
        String userName;
        String password;
        int count = 0;
        while (loop && (count < 3)){
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
                    count++;
                }
            }catch (IOException e){
                e.printStackTrace();
                loop=false;
                count++;
            }
            if(count == 3) {
                methodWrite(Command.EXIT.getStr());
                Thread.currentThread().interrupt();
            }
        }


    }

    private void pChatMenu() throws IOException {
        boolean loop = true;
        ArrayList<String> pc = new ArrayList<String>();
        pc.addAll(user.getPrivateChats().keySet());

        while (loop) {
            int num = 2 + pc.size();
            StringBuilder sb = new StringBuilder("1.exit\n2.create new chat\n");
            int i = 3;
            for (String p : pc) {
                sb.append(i++);
                sb.append('.');
                sb.append(p);
                sb.append("\n");
            }

            int choose = showMenu(sb.toString(),num);

            switch (choose){
                case (1): {
                    loop = false;
                    break;
                } case (2) : {
                    createNewChat();
                    break;
                }
                default:{
                    pChat(user.getPrivateChats().get(pc.get(choose - 3)));
                    break;
                }
            }
        }

    }

    private void createNewChat () throws IOException {
        boolean loop=true;
        while (loop) {
            StringBuilder sb = new StringBuilder();
            sb.append("1.exit\n");
            int i=2;
            for (User u:user.getFriends()) {
                sb.append(i++).append('.').append(u.getUserName()).append("\n");
            }
            int choose = showMenu(sb.toString(),i - 1);

            if (choose == 1){
                loop = false;
            } else {
                if (!user.getPrivateChats().containsKey(user.getFriends().get(choose - 2).getUserName())){
                    PrivateChat privateChat = new PrivateChat(user,user.getFriends().get(choose - 2));
                    user.addPrivateChat(user.getFriends().get(choose - 2),privateChat);
                    user.getFriends().get(choose - 2).addPrivateChat(user,privateChat);
                }
                pChat(user.getPrivateChats().get(user.getFriends().get(choose - 2).getUserName()));
            }
        }
    }

    private void pChat(PrivateChat pc) throws IOException {
        pc.startChat(writer,reader,user);
    }

    private void serverMenu() throws IOException {
        boolean loop = true;
        ArrayList<DiscordServer> se =user.getDiscordServers();

        while (loop) {
            int num = 2 + se.size();
            StringBuilder sb = new StringBuilder("1.exit\n2.create new chat\n");
            int i = 3;
            for (DiscordServer s : se) {
                sb.append(i++);
                sb.append('.');
                sb.append(s.getServerName());
                sb.append("\n");
            }

            int choose=showMenu(sb.toString(),num);
            switch (choose){
                case 1:{
                    loop=false;
                    break;
                }
                case 2:{
                    creatNewServer();
                    break;
                }
                default:{
                    server(se.get(choose-3));
                    break;
                }
            }
        }
    }

    private void creatNewServer() throws IOException {
        boolean loop=true;
        methodWrite(Command.GETSERVERNAME.getStr());
        String serverName = null;
        while (loop) {
            try {
                serverName = methodRead();
                if (controllCenter.checkServerName(serverName)){
                    loop=false;
                }
            }catch (DuplicateException e){
                loop = true;
                methodWrite(Command.GETSERVERNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }

        DiscordServer server= controllCenter.createServer(serverName,this.user);
        server(server);
    }

    private void server(DiscordServer server){
        server.enterMember(user,writer,reader);
    }

    private void profile(){

    }
    private void logOut () {
        if (user.getStatus()== User.Status.ONLINE){
            user.setStatus(User.Status.OFFLINE);
        }
        System.out.println(user.getUserName() + " Logged Out.");
    }

    private void exit() throws IOException {
        methodWrite(Command.EXIT.getStr());
        System.out.println("a Client Left.");
        Thread.currentThread().interrupt();
    }

    private int showMenu (String menu,int n) throws IOException {
        int choose=0;
        methodWrite(Command.SHOWMENU.getStr());
        methodWrite(String.valueOf(n));
        methodWrite(menu);
        boolean loop=true;
        while (loop){
            try {
                choose=Integer.parseInt(methodRead());
                if (!((choose <= n) && (choose > 0))){
                    throw new WrongFormatException(null);
                }
                loop=false;
            }
            catch (NumberFormatException | WrongFormatException e){
                methodWrite(Command.SHOWMENU.getStr());
                methodWrite("1");
                methodWrite("Your input is not valid");
            }
        }
        return choose;
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
