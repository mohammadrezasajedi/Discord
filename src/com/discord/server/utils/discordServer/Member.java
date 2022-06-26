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
import java.util.*;

public class Member {
    private User user;
    private DiscordServer discordServer;
    private ArrayList<Channel> channels;
    private HashSet<String> roles;
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

    public HashSet<String> getRoles() {
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
            HashSet<DiscordServer.Access> acset = new HashSet<>();
            for (String str : roles){
                Set<DiscordServer.Access> access = discordServer.getARoleAccesses(str);
                acset.addAll(access);
            }
            ArrayList<DiscordServer.Access> accesses = new ArrayList<>(acset);
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
                case ROLEASSIGNER:{
                    roleAssigner();
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
    private void roleCreator() throws IOException {
        boolean loop=true;
        methodWrite(Command.GETROLENAME.getStr());
        String roleName=null;
        while (loop){
            try {
                roleName = methodRead();
                if (!discordServer.checkRoleName(roleName)) {
                    throw new DuplicateException("This role name already exists");
                }
                loop = false;
            } catch (DuplicateException e){
                loop = true;
                methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }

        loop = true;
        ArrayList<DiscordServer.Access> accesses = new ArrayList<>(List.of(DiscordServer.Access.values()));
        accesses.removeIf(w -> (w.equals(DiscordServer.Access.ROLECREATOR) || w.equals(DiscordServer.Access.SERVERREMOVER) || w.equals(DiscordServer.Access.ROLEASSIGNER)));
        StringBuilder sb = new StringBuilder("1.confirm\n");
        HashSet<DiscordServer.Access> roleAccesses = new HashSet<>();
        while (loop) {
            int num = 1 + accesses.size();
            int i = 2;
            for (DiscordServer.Access a : accesses) {
                sb.append(i++).append(".").append(a.getMenu()).append("\n");
            }
            int choose = showMenu(sb.toString(), num);

            switch (choose) {
                case (1) : {
                    loop = false;
                    break;
                }
                default:{
                    roleAccesses.add(accesses.get( choose - 2));
                    break;
                }
            }
        }
        discordServer.addRole(roleName,roleAccesses);
    }
    private void roleAssigner() throws IOException {
        boolean loop =true;
        boolean exit=true;
        Member member = null;
        ArrayList<Member> members = new ArrayList<>(discordServer.getMembers().values());
        while (loop){
            StringBuilder sb= new StringBuilder("1.exit\n");
            int num=1+members.size();
            int i = 2;
            for (Member m : members){
                sb.append(i++).append(".").append(m.getUserName()).append("\n");
            }

            int choose = showMenu(sb.toString(),num);

            switch (choose) {
                case 1 :{
                    loop = false;
                    exit = false;
                    break;
                }
                default:{
                    member = members.get(choose - 2);
                    break;
                }
            }

            while (exit){
                sb = new StringBuilder("1.exit\n");
                i = 2;
                ArrayList<String> roles = new ArrayList<>(discordServer.getRoleAccesses().keySet());
                for (String role : roles){
                    sb.append(i++).append(".").append(role).append("\n");
                }

                choose = showMenu(sb.toString(),num);
                String role = null;

                switch (choose){
                    case 1:{
                        exit = false;
                        break;
                    }
                    default:{
                        role = roles.get(choose - 2);
                        member.getRoles().add(role);
                        discordServer.getUserRoles().get(user).add(role);
                    }
                }
            }
        }


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

    private String getUserName(){
        return user.getUserName();
    }

}
