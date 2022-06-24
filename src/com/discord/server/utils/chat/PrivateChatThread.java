package com.discord.server.utils.chat;

import com.discord.Command;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PrivateChatThread {
    private BufferedReader reader;
    private BufferedWriter writer;
    private User user;
    private HashMap<Long,Massage> massages;

    public PrivateChatThread(BufferedReader reader, BufferedWriter writer, User user, HashMap<Long,Massage> massages) {
        this.reader = reader;
        this.writer = writer;
        this.user = user;
        this.massages = massages;
    }


    public void runRead() {
        try {
            String str = methodRead();
            while (!str.equals("#exit")){
                Massage massage = new Massage(str,user);
                massages.put(massage.getId(),massage);
                str = methodRead();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runWrite(){
        try {
            methodWrite(massages.toString());
            methodWrite("WellCome To Chat");
            ArrayList<Long> ids = new ArrayList<>(massages.keySet());
            Collections.sort(ids);
            int size = massages.size();
            for (Long id: ids) {
                methodWrite(massages.get(id).toString());
            }
            while (true) {
                if (massages.size() != size) {
                    ids.addAll(massages.keySet());
                    Collections.sort(ids);
                    Massage m = massages.get(ids.get(ids.size() - 1));
                    if (!m.getAuthor().equals(user)) {
                        methodWrite(m.toString());
                    }
                    size = massages.size();
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
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
}
