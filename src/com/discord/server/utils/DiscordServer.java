package com.discord.server.utils;

import com.discord.server.utils.channels.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DiscordServer {

    public enum Access{
        CHANELCREATOR,
        CHANELREMOVER,
        USERREMOVER,
        USERLIMITER,
        USERBLOCKER,
        NAMECHANGER,
        HISTORYVIEWER,
        MASSAGEPINNER
    }

    private String serverName;
    private User serverOwner;
    private ArrayList<User> members;
    private HashMap<String,Set<Access>> userAccesses;
    private HashMap<User,String> userRoles;
    private HashMap<String, Channel> channels;

    public DiscordServer(String serverName, User serverOwner) {
        this.serverName = serverName;
        this.serverOwner = serverOwner;
        members=new ArrayList<>();
        members.add(serverOwner);
        userAccesses=new HashMap<>();
        userRoles=new HashMap<>();
        channels=new HashMap<>();
    }



}
