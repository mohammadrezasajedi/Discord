package com.discord.server.utils.discordServer.channels;

import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Channel implements Serializable {
    private String name;
    protected ArrayList<Member> active;

    public Channel(String name) {
        this.name = name;
        active = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public abstract void start(Member member);

}
