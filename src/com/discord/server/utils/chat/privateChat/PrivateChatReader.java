package com.discord.server.utils.chat.privateChat;

import com.discord.Command;
import com.discord.server.utils.Massage;
import com.discord.server.utils.PrivateChat;
import com.discord.server.utils.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

public class PrivateChatReader implements Runnable, Serializable {
    private transient BufferedReader reader;
    private transient PrivateChat privateChat;
    private User user;


    public PrivateChatReader(BufferedReader reader, PrivateChat privateChat, User user) {
        this.reader = reader;
        this.privateChat = privateChat;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            String str = methodRead();
            while (!str.equals("#exit")){
                privateChat.sendMassage(new Massage(str,user, privateChat.getId()));
                str = methodRead();
            }
            privateChat.endUserChat(user);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }
}
