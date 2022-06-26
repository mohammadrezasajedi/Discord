package com.discord.server.utils.chat.channel;

import com.discord.Command;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.discordServer.channels.TextChannel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ChannelChatIO implements Runnable{
    private transient BufferedWriter writer;
    private transient BufferedReader reader;
    private Member member;
    private TextChannel textChannel;

    public ChannelChatIO(BufferedWriter writer, BufferedReader reader, Member member, TextChannel textChannel) {
        this.writer = writer;
        this.reader = reader;
        this.member = member;
        this.textChannel = textChannel;
    }

    @Override
    public void run() {
        try {
            String str = methodRead();
            while (!str.equals("#exit")){
                textChannel.sendMassage(new Massage(str,member.getUser(),textChannel.getId()));
                str = methodRead();
            }
            textChannel.endUserChat(member.getUser());
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void broadcast (Massage massage) {
        try {
            if (!massage.getAuthor().equals(member.getUser())) {
                methodWrite(massage.toString());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    public void Initbroadcast (Massage massage) {
        try {
            methodWrite(massage.toString());
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    public void end (User user) {
        try {
            if (this.member.getUser().equals(user)) {
                methodWrite(Command.EXITCHATMODE.getStr());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }

    private void methodWrite (String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }

    public Member getMember() {
        return member;
    }
}
