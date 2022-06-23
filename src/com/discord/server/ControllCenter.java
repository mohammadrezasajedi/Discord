package com.discord.server;

import com.discord.server.utils.DiscordServer;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

public class ControllCenter implements Serializable {

    private HashMap<String, User> users;
    private HashMap<String,DiscordServer> discordServers;
    private transient ArrayList<UserThread> threads;
    private transient ExecutorService pool;

    public ControllCenter() {
        users=new HashMap<>();
        discordServers=new HashMap<>();
        threads=new ArrayList<>();
        pool= Executors.newCachedThreadPool();
    }

    public ControllCenter(ControllCenter controllCenter){
        users=controllCenter.users;
        discordServers=controllCenter.discordServers;
        threads=new ArrayList<>();
        pool=Executors.newCachedThreadPool();
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

    public User createUser(String userName,String password,String email){
        User user=new User(userName,password,email,null,null);
        users.put(user.getUserName(),user);
        return user;
    }

    public User findUser(String userName,String password){
        User user=users.get(userName);
        if (user!=null){
            if (password.equals(user.getPassword())){
                return user;
            }else {
                return null;
            }
        }
        return null;
    }

    public boolean checkUserName(String str) throws WrongFormatException, DuplicateException {
        if (Pattern.matches("(\\S[a-zA-z0-9]{5,})",str)){
            if (users.containsKey(str)){
                throw new DuplicateException("Your user name is in use");
            }
            else {
                return true;
            }
        } else {
            throw new WrongFormatException("Your user name is not valid");
        }
    }

    public boolean checkPassword(String str) throws WrongFormatException {
        if (Pattern.matches("\\w{7,}",str)) {
            return true;
        }else {
            throw new WrongFormatException("Your password is not valid");
        }

    }

    public boolean checkEmail(String str) throws WrongFormatException {
        if (Pattern.matches("(\\S.*\\S)(@)(\\S.*\\S)(.\\S[a-z]{2,3})",str)) {
            return true;
        }else {
            throw new WrongFormatException("Your e-mail is not valid");
        }

    }
}
