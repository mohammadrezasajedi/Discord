package com.discord.server.utils.discordServer.channels;

import com.discord.Command;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.chat.channel.ChannelChatIO;
import com.discord.server.utils.discordServer.Member;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TextChannel extends Channel {
    private ArrayList<ChannelChatIO> observers;
    private HashMap<Long, Massage> massages;
    private  long keepId;

    public TextChannel(String name) {
        super(name);
        keepId = 0L;
        massages = new HashMap<>();
        observers = new ArrayList<>();
    }

    @Override
    public void start(Member member) {
        BufferedReader reader = member.getReader();
        BufferedWriter writer = member.getWriter();
        try {
            writer.write(Command.ENTERCHATMODE.getStr());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }

        ChannelChatIO chatIO  = new ChannelChatIO(writer,reader,member,this);
        observers.add(chatIO);
        for (Long l: massages.keySet()) {
            chatIO.Initbroadcast(massages.get(l));
        }
        chatIO.run();
    }

    public void sendMassage(Massage massage){
        massages.put(massage.getId(),massage);
        Thread t = new Thread(() -> update(massage));
        t.start();
    }

    private void update(Massage massage) {
        for (ChannelChatIO c : observers) {
            c.broadcast(massage);
        }
    }

    public void endUserChat (User user) {
        for (ChannelChatIO c: observers) {
            c.end(user);
        }
        observers.removeIf(c -> c.getMember().getUser().equals(user));
    }

    public Long getId () {
        return keepId++;
    }


}
