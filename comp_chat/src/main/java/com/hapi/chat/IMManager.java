package com.hapi.chat;

public class IMManager {

    private static IMManager instance = new IMManager();
    public static IMManager getInstance() {
        return instance;
    }
    public static UserinfoProvider userinfoProvider = UserinfoProvider.DEFAULT;
    public static void setUserinfoProvider(UserinfoProvider userinfoProvider) {
        IMManager.userinfoProvider = userinfoProvider;
    }

}
