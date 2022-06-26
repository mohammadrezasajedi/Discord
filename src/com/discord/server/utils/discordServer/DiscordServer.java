package com.discord.server.utils.discordServer;

import com.discord.server.ControllCenter;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.channels.Channel;
import com.discord.server.utils.exceptions.DuplicateException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.*;

public class DiscordServer implements Serializable {

    public enum Access{
        ROLEASSIGNER("Assign a Role"),
        CHANELCREATOR("Create a Channel"),
        CHANELREMOVER("Remove a Channel"),
        USERREMOVER("Remove a User"),
        USERLIMITER("Limit a User"),
        USERBLOCKER("Block a User"),
        NAMECHANGER("Change Server Name"),
        HISTORYVIEWER("History View"),
        MASSAGEPINNER("Pin a Message"),
        SERVERREMOVER("Remove the Server"),
        ROLECREATOR("Create a Role");

        private String menu;

        Access(String menu) {
            this.menu = menu;
        }

        public String getMenu() {
            return menu;
        }
    }

    private String serverName;
    private User serverOwner;
    private HashMap<User,Member> members;
    private HashMap<String,Set<Access>> roleAccesses;
    private HashMap<User,HashSet<String>> userRoles;
    private HashMap<String, Channel> channels;
    private ControllCenter controllCenter;

    public DiscordServer(String serverName, User serverOwner,ControllCenter controllCenter) {
        this.serverName = serverName;
        this.serverOwner = serverOwner;
        this.controllCenter=controllCenter;
        members=new HashMap<>();
        Member member=new Member(serverOwner,this);
        HashSet<Access> ownerAccess = new HashSet<>();
        ownerAccess.addAll(Arrays.asList(Access.values()));

        member.getRoles().add("Owner");

        members.put(serverOwner,member);
        userRoles=new HashMap<>();
        roleAccesses = new HashMap<>();
        channels=new HashMap<>();

        roleAccesses.put("Owner",ownerAccess);
        userRoles.put(serverOwner,new HashSet<>());
        userRoles.get(serverOwner).add("Owner");
    }

    public String getServerName() {
        return serverName;
    }

    public void enterMember(User user, BufferedWriter writer, BufferedReader reader){
        Member member=members.get(user);
        member.setWriter(writer);
        member.setReader(reader);
        member.start();
    }

    public boolean changeServerName(String newName) throws DuplicateException {
        if (controllCenter.checkServerName(newName)) {
            controllCenter.changeServerName(serverName, newName, this);
            serverName = newName;
            return true;
        } else {
            return false;
        }
    }

    public Set<Access> getARoleAccesses (String str) {
        return roleAccesses.get(str);
    }

    public boolean checkRoleName (String s) {
        return !roleAccesses.containsKey(s);
    }

    public void addRole (String roleName,HashSet<Access> accesses){
        roleAccesses.put(roleName,accesses);
    }

    public HashMap<User, Member> getMembers() {
        return members;
    }

    public HashMap<String, Set<Access>> getRoleAccesses() {
        return roleAccesses;
    }

    public HashMap<User, HashSet<String>> getUserRoles() {
        return userRoles;
    }
}
