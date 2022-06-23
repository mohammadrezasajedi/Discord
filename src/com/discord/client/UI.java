package com.discord.client;

import java.util.Scanner;

public class UI {
    public static void welcome(String str){
        System.out.println(str);
    }

    public static String getUserName(){
        System.out.println("Please enter your user name");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getUserName(String str){
        System.out.println(str);
        System.out.println("Please enter your user name again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getPassword(){
        System.out.println("Please enter your password");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getPassword(String str){
        System.out.println(str);
        System.out.println("Please enter your password again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getEmail(){
        System.out.println("Please enter your e-mail");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getEmail(String str){
        System.out.println(str);
        System.out.println("Please enter your e-mail again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String initMenu (String str) {
        System.out.println(str);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public static void print(String str){
        System.out.println(str);
    }

    public static String ShowMenu (String str) {
        System.out.println(str);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }
    public static void exit (){
        System.out.println("Exiting The Application");
    }

}
