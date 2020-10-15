package com.hapi.chat;

import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMMessage;

/**
 *
 * @date 2019/2/28
 */
public class UnkownMessage extends IMChatMessage {

    public UnkownMessage(TIMMessage message) {
        super(message);
    }

    @Override
    protected void parseIMMessage(TIMElem elem) {

    }

    @Override
    public void save() {

    }


    @Override
    public TIMMessage buildTimMsg() {
        return timMessage;
    }

    @Override
    public String getAction() {
        return ChatMsgType.UNKOWN.getType();
    }
}
