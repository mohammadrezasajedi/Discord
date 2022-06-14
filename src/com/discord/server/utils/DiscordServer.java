package com.discord.server.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DiscordServer {

    public enum Role{
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
    private HashMap<User,Set<Role>> userRoles;

    public DiscordServer(String serverName, User serverOwner) {
        this.serverName = serverName;
        this.serverOwner = serverOwner;
        members=new ArrayList<>();
        members.add(serverOwner);
        userRoles=new HashMap<>();
    }
}
