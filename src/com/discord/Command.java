package com.discord;

public enum Command{

    INITMENU("initMenu"),
    PRINT("print"),
    GETUSERNAME("getUserName"),
    GETUSERNAMEAGAIN("getUserNameAgain"),
    GETPASSWORD("getPassword"),
    GETPASSWORDAGAIN("getPasswordAgain"),
    GETEMAIL("getEmail"),
    GETEMAILAGAIN("getEmailAgain"),
    SHOWMENU("showMenu"),
    EXIT("exit")
    ;

    private String str;

    Command(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public static Command valueOfLabel(String str) {
        for (Command e : values()) {
            if (e.getStr().equals(str)) {
                return e;
            }
        }
        return null;
    }
}