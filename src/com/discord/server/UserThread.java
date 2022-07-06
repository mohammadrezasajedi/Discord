package com.discord.server;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.NotificationStream;
import com.discord.server.utils.discordServer.DiscordServer;
import com.discord.server.utils.PrivateChat;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserThread extends Thread{

    private final ControllCenter controllCenter;
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private User user;
    private transient FileStream fileStream;

    private transient NotificationStream notificationStream;

    public UserThread(Socket fileserverSocket, ControllCenter controllCenter, Socket socket,Socket notifSocket) throws IOException {
        this.controllCenter = controllCenter;
        this.socket = socket;
        writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        notificationStream = new NotificationStream(notifSocket);
        fileStream = new FileStream(fileserverSocket);
    }



    @Override
    public void run() {
        try {
            boolean app = true;
            System.out.println("Wellcome Sent");
            methodWrite("Welcome to discord.\n");
            Thread.sleep(5*1000);
            while (app) {
                int choose = showMenu("1.Login\n2.Signup\n3.exit", 3);
                if (choose == 1) {
                    login();
                    if (user == null){
                        exit();
                    }
                } else if (choose == 2){
                    signUp();
                } else {
                    app = false;
                    exit();
                    break;
                }

                if (user != null && user.getStatus() == User.Status.OFFLINE) {
                    user.setStatus(User.Status.ONLINE);
                }


                notificationStream.sendPopUp("WellCome",user.getUserName());


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
            if (user != null && user.getStatus()== User.Status.ONLINE){
                user.setStatus(User.Status.OFFLINE);
            }
            notificationStream.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        notificationStream.sendPopUp("Friend Request Status",str);
    }

    private void showFriends() throws IOException {
        methodWrite(Command.GETTABLE.getStr());
        methodWrite(String.valueOf(user.getFriends().size()));
        for (User u:user.getFriends()) {
            methodWrite(u.getUserName()+" : "+u.getStatus().getName());
        }
        methodRead();
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
//                methodWrite(Command.PRINT.getStr());
//                methodWrite("Accept " + u.getUserName() + "?");
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
                    notificationStream.sendPopUp("Login Failed","Your user name or password is not valid");
                    methodWrite(Command.RESETMENU.getStr());
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
        pc.startChat(writer,reader,user,fileStream);
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
        String welcome = null;
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
        methodWrite(Command.GETWELLCOME.getStr());
        welcome = methodRead();

        DiscordServer server= controllCenter.createServer(fileStream,serverName,this.user,welcome);
        server(server);
    }

    private void server(DiscordServer server) throws IOException {
        server.enterMember(user,writer,reader,fileStream);
    }

    private void profile() throws IOException {
        boolean loop = true;
        while (loop){
            int choose = showMenu("1.user name\n2.password\n3.e-mail\n4.phone\n5.status\n6.picture\n7.exit\n",7);
            switch (choose) {
                case 1 :{
                    userName();
                    break;
                }
                case 2:{
                    password();
                    break;
                }
                case 3:{
                    email();
                    break;
                }
                case 4:{
                    phone();
                    break;
                }
                case 5:{
                    status();
                    break;
                }
                case 6:{
                    picture();
                    break;
                }
                case 7:{
                    loop=false;
                    break;
                }
            }
        }
    }

    private void userName () throws IOException {
        boolean loop = true;
        while (loop) {
//            methodWrite(Command.PRINT.getStr());
//            methodWrite(user.getUserName());
            int choose = showMenu("1.change\n2.exit",2);

            switch (choose){
                case 1 : {
                    methodWrite(Command.GETUSERNAME.getStr());
                    boolean l = true;
                    String userName = null;
                    while (l) {
                        try {
                            userName = reader.readLine();
                            l = !controllCenter.checkUserName(userName);
                        } catch (DuplicateException | WrongFormatException e) {
                            methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }
                    controllCenter.changeUserName(user.getUserName(),userName);
                    break;
                }
                case 2:{
                    loop = false;
                    break;
                }
            }
        }
    }

    private void password() throws IOException {
        boolean loop = true;
        while (loop){
            int choose = showMenu("1.change\n2.exit\n",2);
            switch (choose){
                case 1:{
                    String password = null;
                    methodWrite(Command.GETPASSWORD.getStr());
                    boolean l = true;
                    while (l) {
                        try {
                            password = reader.readLine();
                            l = !controllCenter.checkPassword(password);
                        } catch (WrongFormatException e) {
                            methodWrite(Command.GETPASSWORDAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }

                    user.setPassword(password);
                    break;
                }
                case 2:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void email() throws IOException {
        boolean loop = true;
        while (loop){
//            methodWrite(Command.PRINT.getStr());
//            methodWrite(user.getEmail());
            int choose = showMenu("1.change\n2.exit\n",2);
            switch (choose){
                case 1:{
                    methodWrite(Command.GETEMAIL.getStr());
                    String email = null;
                    boolean l = true;

                    while (l) {
                        try {
                            email = reader.readLine();
                            l = !controllCenter.checkEmail(email);
                        } catch (WrongFormatException e) {
                            methodWrite(Command.GETEMAILAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }
                    user.setEmail(email);
                    break;
                }
                case 2:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void phone() throws IOException {
        boolean loop = true;
        while (loop){
            if (user.getPhoneNumber()!=null){
//                methodWrite(Command.PRINT.getStr());
//                methodWrite(user.getPhoneNumber());
            }
            int choose = showMenu("1.change\n2.remove\n3.exit\n",3);
            switch (choose){
                case 1:{
                    String phone = null;
                    methodWrite(Command.GETPHONE.getStr());
                    boolean l = true;
                    while (l) {
                        try {
                            phone = reader.readLine();
                            l = !controllCenter.checkPhone(phone);
                        } catch (WrongFormatException e) {
                            methodWrite(Command.GETPHONEAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }
                    user.setPhoneNumber(phone);
                }

                case 2: {
                    user.setPhoneNumber(null);
                }
                case 3:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void status() throws IOException {
        boolean loop = true;
        while (loop){
//            methodWrite(Command.PRINT.getStr());
//            methodWrite(user.getStatus().getName());
            int choose = showMenu("1.change\n2.exit\n",2);
            switch (choose){
                case 1: {
                    ArrayList<User.Status> statuses = new ArrayList<>(List.of(User.Status.values()));
                    statuses.removeIf(w -> w.equals(User.Status.OFFLINE));

                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (User.Status status : statuses) {
                        sb.append(i++).append(".").append(status.getName()).append("\n");
                    }
                    int choice = showMenu(sb.toString(), 4);
                    user.setStatus(statuses.get(choice - 1));
                }
                case 2:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void picture() throws IOException {
        methodWrite(Command.GETPROFILEPICTURE.getStr());
        String input = methodRead();
        if (!input.equals("#exit")) {
            String[] fname = input.split("\\.");
            File file = new File("./ProfilePics/" + user.getUserName() + "." + fname[fname.length - 1]);
            fileStream.receiveFile(file);
            user.setImageFile(file);
        }
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
        notificationStream.close();
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
