package com.discord;

import java.time.LocalDate;

public class Massage {
    private String text;
    private User author;
    private LocalDate date;

    public Massage(String text, User author, LocalDate date) {
        this.text = text;
        this.author = author;
        this.date = LocalDate.now();
    }

    public String getText() {
        return text;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDate getDate() {
        return date;
    }

}
