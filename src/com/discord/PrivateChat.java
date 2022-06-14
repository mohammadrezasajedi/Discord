package com.discord;

import java.util.ArrayList;

public class PrivateChat {
    private User user1;
    private User user2;
    private ArrayList<Massage> massages;

    public void sendMassage(Massage massage){
        massages.add(massage);
    }

    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.massages = new ArrayList<>();
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public ArrayList<Massage> getMassages() {
        return massages;
    }

}
