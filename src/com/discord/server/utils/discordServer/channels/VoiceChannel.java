package com.discord.server.utils.discordServer.channels;

import com.discord.server.utils.discordServer.Member;

public class VoiceChannel extends Channel{


    public VoiceChannel(String name,boolean history) {
        super(name,history);
    }

    @Override
    public void start(Member member,Long history) {

    }
}
