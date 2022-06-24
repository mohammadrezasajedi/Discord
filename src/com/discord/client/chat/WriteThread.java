package com.discord.client.chat;

import com.discord.client.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class WriteThread implements Runnable{

    private BufferedWriter writer;

    public WriteThread(BufferedWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true){
            String str = scanner.nextLine();
            try {
                writer.write(str);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
