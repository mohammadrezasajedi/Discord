package com.discord;

import java.io.File;
import java.util.ArrayList;

public class User {

    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private File imageFile;
    private String status;
    private ArrayList<User> friends;
    private ArrayList<PrivateChat> privateChats;
    private ArrayList<User> blockUsers;
    public User(String userName, String password, String email, String phoneNumber,File imageFile) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageFile=imageFile;
        friends=new ArrayList<>();
        privateChats=new ArrayList<>();
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

    public File getImageFile() {
        return imageFile;
    }

    public void addFriend(User friend){
        friends.add(friend);
    }

    public ArrayList<User> getFriends(){
        return friends;
    }

    public ArrayList<PrivateChat> getPrivateChats(){
        return privateChats;
    }

    public ArrayList<User> getBlockUsers(){
        return blockUsers;
    }

}
