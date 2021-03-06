package com.discord.server.utils;

import java.io.*;
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

    public synchronized void sendFile (File file) {
        try {
            if (file != null && file.exists()) {
                int bytes = 0;

                FileInputStream fileInputStream = new FileInputStream(file);
                out.writeLong(file.length());

                byte[] buf = new byte[8*1024];
                while ((bytes=fileInputStream.read(buf))!=-1){
                    out.write(buf,0,bytes);
                    out.flush();
                }
                fileInputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void receiveFile (File file) {
        try {
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
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

    public void methodWrite (String str) throws IOException {
        out.writeUTF(str);
        out.flush();
    }
}
