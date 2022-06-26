package com.discord.server.utils.discordServer.channels;

import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;

import java.util.ArrayList;

public abstract class Channel {
    private String name;
    private ArrayList<Member> members;

    public Channel(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }
}
