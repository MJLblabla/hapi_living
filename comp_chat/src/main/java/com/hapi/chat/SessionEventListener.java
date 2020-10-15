package com.hapi.chat;

import android.content.Context;

/**
 * 会话窗口消息列表一些点击事件的响应处理函数
 */
public interface SessionEventListener {

    /**
     * 头像点击事件处理，一般用于打开用户资料页面
     */

    void onAvatarClicked(Context context, IMChatMessage message);

    /**
     * 头像长按事件处理，一般用于群组@功能，或者弹出菜單，做拉黑，加好友等功能
     *
     * @param context
     * @param message
     */
    void onAvatarLongClicked(Context context, IMChatMessage message);
}