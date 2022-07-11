package com.discord.server.utils.discordServer.channels;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.chat.channel.ChannelChatIO;
import com.discord.server.utils.chat.privateChat.PrivateChatWriter;
import com.discord.server.utils.discordServer.DiscordServer;
import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.exceptions.WrongFormatException;

import javax.management.relation.Role;
import java.io.*;
import java.util.*;

public class TextChannel extends Channel {
    private HashMap<Member,Integer> tags;
    private DiscordServer discordServer;
    private ArrayList<ChannelChatIO> observers;
    private HashMap<Long, Massage> massages;
    private HashMap<Massage, File> files;
    private Massage pinnedMessage;

    public TextChannel(String name, DiscordServer discordServer, boolean history) {
        super(name,history);

        this.discordServer = discordServer;
        massages = new HashMap<>();
        observers = new ArrayList<>();
        files = new HashMap<>();

        tags=new HashMap<>();
        for (Member m:discordServer.getMembers().values()) {
            tags.put(m,0);
        }
    }

    @Override
    public void start(Member member,Long history) {
        BufferedReader reader = member.getReader();
        BufferedWriter writer = member.getWriter();
        FileStream fileStream = member.getFileStream();
        try {
            writer.write(Command.ENTERCHATMODE.getStr());
            writer.newLine();
            writer.flush();

            writer.write(getName());
            writer.newLine();
            writer.flush();

            writer.write("null");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }

        ChannelChatIO chatIO  = new ChannelChatIO(fileStream,writer,reader,member,this);
        observers.add(chatIO);
        for (Long l: massages.keySet()) {
            if (l >= history) {
                chatIO.Initbroadcast(massages.get(l));
            }
        }
        chatIO.run();
    }

    public void sendMassage(Massage massage){
        massages.put(massage.getId(),massage);
        Thread t = new Thread(() -> update(massage));
        t.start();
    }

    public void sendIsTyping (User user) {
        Thread t = new Thread(() -> isTyping(user.getUserName() + " is Typing...",user));
        t.start();
    }

    private void isTyping(String str,User user){
        for (ChannelChatIO w: observers) {
            w.broadcast("$$" + str,user);
        }
    }

    public void like (Long id,User user) throws WrongFormatException {
        Massage massage = massages.get(id);
        if (massage == null){
            throw new WrongFormatException("Message Doesn't Exist");
        } else {
            massage.addLike(user);
            Thread t = new Thread(() -> {
                publicMessage(user.getUserName() + " liked " + massage.getAuthor().getUserName());
                for (ChannelChatIO p : observers) {
                    p.broadcast("##" + "like" + "-" + id + "-" + user.getUserName());
                }
            });
            t.start();
        }
    }
    public void dislike(Long id,User user) throws WrongFormatException {
        Massage massage=massages.get(id);
        if (massage==null) {
            throw new WrongFormatException("Message Doesn't Exist");
        }else {
            massage.addDislike(user);
            Thread t = new Thread(() -> {
                publicMessage(user.getUserName() + " disliked " + massage.getAuthor().getUserName());
                for (ChannelChatIO p : observers) {
                    p.broadcast("##" + "dislike" + "-" + id + "-" + user.getUserName());
                }
            });
            t.start();
        }
    }
    public void laughter(Long id,User user) throws WrongFormatException {
        Massage massage=massages.get(id);
        if (massage==null) {
            throw new WrongFormatException("Message Doesn't Exist");
        }else {
            massage.addLaughter(user);
            Thread t = new Thread(() -> {
                publicMessage(user.getUserName() + " laughed at " + massage.getAuthor().getUserName());
                for (ChannelChatIO p : observers) {
                    p.broadcast("##" + "laugh" + "-" + id + "-" + user.getUserName());
                }
            });
            t.start();
        }
    }

    public void pin(Long id,User user) throws WrongFormatException {
        Massage massage=massages.get(id);
        if (massage==null) {
            throw new WrongFormatException("Message Doesn't Exist");
        }else {
            HashSet<DiscordServer.Access> acset = new HashSet<>();
            for (String str : discordServer.getUserRoles().get(user)) {
                Set<DiscordServer.Access> access = discordServer.getARoleAccesses(str);
                acset.addAll(access);
            }
            ArrayList<DiscordServer.Access> accesses = new ArrayList<>(acset);

            if (accesses.contains(DiscordServer.Access.MASSAGEPINNER)) {
                pinnedMessage = massage;
                Thread t = new Thread(() -> publicMessage(user.getUserName() + " pinned " + massage.getId()));
                t.start();
            }
        }
    }

    public void getPinned (ChannelChatIO chatIO) {
        if (pinnedMessage != null){
            chatIO.Initbroadcast(pinnedMessage);
        }
    }




    private void publicMessage (String str){
        for (ChannelChatIO c : observers) {
            c.broadcast("|" + str);
        }
    }

    private void update(Massage massage) {
        for (ChannelChatIO c : observers) {
            c.Initbroadcast(massage);
        }
    }

    public void endUserChat (User user) {
        for (ChannelChatIO c: observers) {
            c.end(user);
        }
        observers.removeIf(c -> c.getMember().getUser().equals(user));
        tags.replace(discordServer.getMembers().get(user), 0);
    }

    public HashMap<Massage, File> getFiles() {
        return files;
    }

    public File getFileMessage (long id){
        Massage massage = massages.get(id);
        if (massage != null){
            File file = files.get(massage);
            if ((file != null) && (file.exists())){
                return file;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public HashMap<Member, Integer> getTags() {
        return tags;
    }

    public ArrayList<ChannelChatIO> getObservers() {
        return observers;
    }
}
