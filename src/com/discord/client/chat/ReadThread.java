package com.discord.client.chat;

import com.discord.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadThread implements Runnable{
    private BufferedReader reader;

    public ReadThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String str = methodRead();
            while (!str.equals(Command.EXITCHATMODE.getStr())){
                System.out.println(str);
                str = methodRead();
            }
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
