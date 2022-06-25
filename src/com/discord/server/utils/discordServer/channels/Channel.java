package com.discord.server.utils.discordServer.channels;

import com.discord.server.utils.User;

import java.util.ArrayList;

public abstract class Channel {
    private String name;
    private ArrayList<User> users;

    public String getName() {
        return name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
