package com.hapi.chat;

import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMMessage;

/**
 *
 * @date 2019/2/27
 */
public class PicMessage extends IMChatMessage<TIMImageElem> {
    private String path;

    public PicMessage(TIMMessage message) {
        super(message);
    }

    /**
     * @param path  图片路径
     * @param isOri 是否原图发送
     */
    public PicMessage(String path, boolean isOri) {
        super();
        this.path = path;

        timElem = new TIMImageElem();
        timElem.setPath(path);
        timElem.setLevel(isOri ? 0 : 1);
        this.timMessage.addElement(timElem);
    }

    @Override
    protected void parseIMMessage(TIMImageElem elem) {
        this.path = elem.getPath();
    }

    @Override
    public void save() {

    }



    public String getPath() {
        return path;
    }

    public TIMImage getTIMImage(TIMImageType type) {
        for (TIMImage image : timElem.getImageList()) {
            if (image.getType() == type) {
                return image;
            }
        }
        return null;
    }

    @Override
    public TIMMessage buildTimMsg() {
        return timMessage;
    }

    @Override
    public String getAction() {
        return ChatMsgType.PIC.getType();
    }
}