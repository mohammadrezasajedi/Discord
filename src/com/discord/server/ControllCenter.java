package com.discord.server;

import com.discord.server.utils.DiscordServer;
import com.discord.server.utils.User;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ControllCenter {

    private HashMap<String, User> users;
    private HashMap<String,DiscordServer> discordServers;
    private ArrayList<UserThread> threads;
    private ExecutorService pool;

    public ControllCenter() {
        users=new HashMap<>();
        discordServers=new HashMap<>();
        threads=new ArrayList<>();
        pool= Executors.newCachedThreadPool();
    }

    public void init(Socket socket) {
        try {
            UserThread userThread=new UserThread(this,socket);
            threads.add(userThread);
            pool.execute(userThread);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
