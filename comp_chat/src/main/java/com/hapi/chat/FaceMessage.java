package com.hapi.chat;

import android.text.TextUtils;

import com.tencent.imsdk.TIMFaceElem;
import com.tencent.imsdk.TIMMessage;

/**
 *
 * @date 2019/2/27
 */
abstract public class FaceMessage extends IMChatMessage<TIMFaceElem> {
    private String data;
    private int index;

    public FaceMessage(TIMMessage message) {
        super(message);
    }

    public FaceMessage(String data, int index) {
        super();
        this.data = data;
        this.index = index;

        this.timElem = new TIMFaceElem();
        if (!TextUtils.isEmpty(data)) {
            this.timElem.setData(data.getBytes());
        }
        this.timElem.setIndex(index);
        this.timMessage.addElement(timElem);
    }

    @Override
    protected void parseIMMessage(TIMFaceElem elem) {
        data = new String(elem.getData());
        index = elem.getIndex();
    }

    @Override
    public void save() {

    }



    @Override
    public TIMMessage buildTimMsg() {
        return timMessage;
    }
}