package com.discord.server.utils;

import com.discord.server.utils.chat.PrivateChatThread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrivateChat {
    private User user1;
    private User user2;
    private HashMap<Long,Massage> massages;
    public void sendMassage(Massage massage){
        massages.put(massage.getId(),massage);
    }

    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.massages = new HashMap<>();
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void startChat (BufferedWriter writer, BufferedReader reader,User user){
        PrivateChatThread t = new PrivateChatThread(reader,writer,user,massages);
        Thread thread= new Thread(() -> t.runWrite());
        t.runRead();
        thread.interrupt();
    }

}
