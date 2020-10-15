package com.hapi.chat;

/**
 *
 * @date 2019/2/27
 */
public class IMUIKit {
    //elem文件是否自动下载
    private static boolean isElemAutoDownload = true;
    // 会话窗口消息列表一些点击事件的响应处理函数
    private static SessionEventListener sessionListener = null;

    public static void setIsElemAutoDownload(boolean isElemAutoDownload) {
        IMUIKit.isElemAutoDownload = isElemAutoDownload;
    }

    public static boolean isElemAutoDownload() {
        return isElemAutoDownload;
    }

    public static SessionEventListener getSessionListener() {
        return sessionListener;
    }

    public static void setSessionListener(SessionEventListener sessionListener) {
        IMUIKit.sessionListener = sessionListener;
    }
}
