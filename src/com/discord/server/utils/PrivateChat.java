package com.discord.server.utils;

import com.discord.Command;
import com.discord.server.utils.chat.channel.ChannelChatIO;
import com.discord.server.utils.chat.privateChat.PrivateChatReader;
import com.discord.server.utils.chat.privateChat.PrivateChatWriter;
import com.discord.server.utils.discordServer.DiscordServer;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PrivateChat implements Serializable {
    private User user1;
    private User user2;
    private HashMap<Long,Massage> massages;
    private HashMap<Massage, File> files;
    private transient ArrayList<PrivateChatWriter> observers;
    private  long keepId;
    private Massage pinnedMessage;

    public void sendMassage(Massage massage){
        massages.put(massage.getId(),massage);
        Thread t = new Thread(() -> update(massage));
        t.start();
    }

    public void sendIsTyping (User user) {
        Thread t = new Thread(() -> isTyping(user.getUserName() + " is Typing...",user));
        t.start();
    }

    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.massages = new HashMap<>();
        observers = new ArrayList<>();
        keepId = 0L;
        this.files = new HashMap<>();
    }

    public Long getId () {
        return keepId++;
    }

    public Long getHis(){
        return keepId;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void startChat (BufferedWriter writer, BufferedReader reader,User user,FileStream fileStream){
        try {
            if (observers == null){
                observers = new ArrayList<>();
            }
            writer.write(Command.ENTERCHATMODE.getStr());
            writer.newLine();
            writer.flush();

            User u = user.equals(user1) ? user2 : user1;

            writer.write(u.getUserName());
            writer.newLine();
            writer.flush();

            if (u.getImageFile() != null){
                writer.write("pic");
                writer.newLine();
                writer.flush();
                fileStream.methodWrite(u.getImageFile().getName());
                fileStream.sendFile(u.getImageFile());
            } else {
                writer.write("null");
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
        PrivateChatWriter privateChatWriter = new PrivateChatWriter(writer,user,this);
        addObserver(privateChatWriter);
        for (Long l: massages.keySet()) {
            privateChatWriter.Initbroadcast(massages.get(l));
        }
        PrivateChatReader privateChatReader = new PrivateChatReader(fileStream,reader,this,user,privateChatWriter);
        privateChatReader.run();
    }

    private void addObserver (PrivateChatWriter p){
        observers.add(p);
    }

    public void endUserChat (User user) {
        for (PrivateChatWriter w: observers) {
            w.end(user);
        }
        observers.removeIf(w -> w.getUser().equals(user));
    }

    private void update(Massage massage) {
        for (PrivateChatWriter w: observers) {
            w.Initbroadcast(massage);
        }
    }

    private void isTyping(String str,User user){
        for (PrivateChatWriter w: observers) {
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
                for (PrivateChatWriter p : observers) {
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
                for (PrivateChatWriter p : observers) {
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
                for (PrivateChatWriter p : observers) {
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
            pinnedMessage = massage;
            Thread t = new Thread(() -> publicMessage(user.getUserName() + " pinned " + massage.getId()));
            t.start();
        }
    }

    public void publicMessage (String str){
        for (PrivateChatWriter p : observers){
            p.broadcast("|" + str);
        }
    }

    public void getPinned (PrivateChatWriter writer) {
        if (pinnedMessage != null){
            writer.Initbroadcast(pinnedMessage);
        }
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

    public ArrayList<PrivateChatWriter> getObservers() {
        return observers;
    }
}
