package com.discord.server.utils;

import java.util.HashMap;

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

}
