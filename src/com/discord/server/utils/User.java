package com.discord.server.utils;

import java.awt.*;
import java.util.ArrayList;

public class User {

    public enum Status{
        ONLINE("Online"),
        IDLE("Idle"),
        DND("Do Not Disturb"),
        INVISIBLE("Invisible");

        private String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private Image imageFile;
    private Status status;
    private ArrayList<User> friends;
    private ArrayList<PrivateChat> privateChats;
    private ArrayList<User> blockUsers;
    private ArrayList<String> discordServers;
    public User(String userName, String password, String email, String phoneNumber,Image imageFile){
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageFile=imageFile;
        friends=new ArrayList<>();
        privateChats=new ArrayList<>();
        discordServers=new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Image getImageFile() {
        return imageFile;
    }

    public void addFriend(User friend){
        friends.add(friend);
    }

    public ArrayList<User> getFriends(){
        return friends;
    }

    public void removeFriend(User user){
        friends.remove(user);
    }

    public ArrayList<PrivateChat> getPrivateChats(){
        return privateChats;
    }

    public void addPrivateChat (PrivateChat privateChat){
        privateChats.add(privateChat);
    }

    public void removePrivateChat(PrivateChat privateChat){
        privateChats.remove(privateChat);
    }

    public ArrayList<User> getBlockUsers(){
        return blockUsers;
    }

    public void addBlockUser(User user){
        blockUsers.add(user);
    }

    public void removeBlockUser(User user){
        blockUsers.remove(user);
    }




}
