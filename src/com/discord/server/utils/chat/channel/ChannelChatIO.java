package com.discord.server.utils.chat.channel;

import com.discord.Command;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.discordServer.channels.TextChannel;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

public class ChannelChatIO implements Runnable, Serializable {
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
                try {
                    if (str.contains("#like")) {
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.like(id,member.getUser());
                    } else if (str.contains("#dislike")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.dislike(id,member.getUser());
                    }else if (str.contains("#laughter")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.laughter(id,member.getUser());
                    }else if (str.contains("#pin")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.pin(id,member.getUser());
                    } else if (str.contains("#getpm")){
                        textChannel.getPinned(this);
                    } else {
                        textChannel.sendMassage(new Massage(str,member.getUser(),textChannel.getId()));
                    }
                } catch (NumberFormatException | WrongFormatException | ArrayIndexOutOfBoundsException e){
                    System.err.println(member.getUser().getUserName() + " sent an unacceptable command");
                }
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

    public void broadcast (String str) {
        try {
            methodWrite(str);
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
