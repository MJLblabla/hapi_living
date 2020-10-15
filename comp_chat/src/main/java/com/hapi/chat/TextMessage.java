package com.hapi.chat;

import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;

/**
 * 文本消息数据
 *
 *
 */
public class TextMessage extends IMChatMessage<TIMTextElem> {
    private String text;


    public TextMessage(TIMMessage message) {
        super(message);
    }



    public TextMessage(String text) {
        super();
        this.text = text;


        timElem = new TIMTextElem();
        timElem.setText(text);
        this.timMessage.addElement(timElem);
    }

    @Override
    protected void parseIMMessage(TIMTextElem elem) {
        this.text = elem.getText();

    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }



    public String getText() {
        return text;
    }

    @Override
    public TIMMessage buildTimMsg() {
        return timMessage;
    }

    @Override
    public String getAction() {
        return ChatMsgType.TEXT.getType();
    }
}
