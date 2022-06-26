package com.discord.server.utils;

import com.discord.Command;
import com.discord.server.utils.chat.privateChat.PrivateChatReader;
import com.discord.server.utils.chat.privateChat.PrivateChatWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PrivateChat implements Serializable {
    private User user1;
    private User user2;
    private HashMap<Long,Massage> massages;
    private transient ArrayList<PrivateChatWriter> observers;
    private  long keepId;

    public void sendMassage(Massage massage){
        massages.put(massage.getId(),massage);
        Thread t = new Thread(() -> update(massage));
        t.start();
    }

    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.massages = new HashMap<>();
        observers = new ArrayList<>();
        keepId = 0L;
    }

    public Long getId () {
        return keepId++;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void startChat (BufferedWriter writer, BufferedReader reader,User user){
        try {
            writer.write(Command.ENTERCHATMODE.getStr());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
        PrivateChatWriter privateChatWriter = new PrivateChatWriter(writer,user);
        addObserver(privateChatWriter);
        for (Long l: massages.keySet()) {
            privateChatWriter.Initbroadcast(massages.get(l));
        }
        PrivateChatReader privateChatReader = new PrivateChatReader(reader,this,user);
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
            w.broadcast(massage);
        }
    }

}
