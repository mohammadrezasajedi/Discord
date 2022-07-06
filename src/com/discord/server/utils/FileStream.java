package com.discord.server.utils;

import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.discordServer.channels.TextChannel;
import com.discord.server.utils.discordServer.channels.VoiceChannel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileStream implements Serializable{
    private transient Socket socket;
    private transient DataOutputStream out;
    private transient DataInputStream in;

    public FileStream(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void receiveFile (File file) {
        try {
            int bytes = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            long size = in.readLong();
            byte[] buf = new byte[8*1024];
            while (size > 0 && (bytes = in.read(buf, 0, (int) Math.min(buf.length, size))) != -1) {
                fileOutputStream.write(buf, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
