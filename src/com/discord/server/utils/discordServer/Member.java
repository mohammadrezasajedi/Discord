package com.discord.server.utils.discordServer;

import com.discord.Command;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.channels.Channel;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import javax.management.relation.Role;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Member {
    private User user;
    private DiscordServer discordServer;
    private ArrayList<Channel> channels;
    private HashSet<DiscordServer.Access> roles;
    private BufferedWriter writer;
    private BufferedReader reader;
    public Member(User user,DiscordServer discordServer) {
        this.user = user;
        this.discordServer=discordServer;
        channels=new ArrayList<>();
        roles=new HashSet<>();
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public HashSet<DiscordServer.Access> getRoles() {
        return roles;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void start(){
        boolean loop=true;
        while (loop){
            try {
                StringBuilder sb=new StringBuilder("1.exit\n2.Settings\n");
                int num=2+channels.size();
                int i=3;
                for (Channel channel:channels) {
                    sb.append(i++).append(".").append(channel.getName()).append("\n");
                }
                int choose=showMenu(sb.toString(),num);
                switch (choose){
                    case 1:{
                        loop=false;
                        break;
                    }
                    case 2:{
                        setting();
                        break;
                    }
                    default:{
                        channel(channels.get(choose-3));
                        break;
                    }
                }
            }catch (IOException e){
                Thread.currentThread().interrupt();
                if (user.getStatus()== User.Status.ONLINE){
                    user.setStatus(User.Status.OFFLINE);
                }
            }
        }
    }

    private void setting() throws IOException {
        boolean loop=true;
        while (loop){
            ArrayList<DiscordServer.Access> accesses = new ArrayList<>(roles);
            accesses.removeIf(w -> w.equals(DiscordServer.Access.MASSAGEPINNER));
            StringBuilder sb=new StringBuilder("1.exit\n");
            int i=2;
            int num=1+accesses.size();
            for (DiscordServer.Access access:accesses) {
                sb.append(i++).append(".").append(access.getMenu()).append("\n");
            }
            int choose=showMenu(sb.toString(),num);
            DiscordServer.Access access=accesses.get(choose-2);
            switch (access){
                case NAMECHANGER:{
                    nameChanger();
                    break;
                }
                case ROLECREATOR:{
                    roleCreator();
                    break;
                }
                case USERBLOCKER:{
                    userBlocker();
                    break;
                }
                case USERLIMITER:{
                    userLimiter();
                    break;
                }
                case USERREMOVER:{
                    userRemover();
                    break;
                }
                case CHANELCREATOR:{
                    chanelCreator();
                    break;
                }
                case CHANELREMOVER:{
                    chanelRemover();
                    break;
                }
                case HISTORYVIEWER:{
                    historyViewer();
                    break;
                }
                case SERVERREMOVER:{
                    serverRemover();
                    break;
                }

            }
        }
    }

    private void nameChanger() throws IOException {
        boolean loop=true;
        methodWrite(Command.GETSERVERNAME.getStr());
        String serverName = null;
        while (loop) {
            try {
                serverName = methodRead();
                if (discordServer.changeServerName(serverName)){
                    loop=false;
                }
            }catch (DuplicateException e){
                loop = true;
                methodWrite(Command.GETSERVERNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }
    }
    private void roleCreator(){

    }
    private void userBlocker(){

    }
    private void userLimiter(){

    }
    private void userRemover(){

    }
    private void chanelCreator(){

    }
    private void chanelRemover(){

    }
    private void historyViewer(){

    }
    private void serverRemover(){

    }



    private void channel(Channel channel){

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
