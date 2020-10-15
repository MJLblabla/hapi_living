package com.hapi.chat;

import androidx.annotation.Nullable;


import com.hapi.ut.EncryptUtil;
import com.hapi.ut.constans.FileConstants;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;

/**
 * 语音消息数据
 */
public class VoiceMessage extends BaseFileMessage<TIMSoundElem> {

    private long duration;

    public VoiceMessage(TIMMessage message) {
        super(message);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(String filePath, long duration) {
        super();
        this.path = filePath;
        this.duration = duration;

        timElem = new TIMSoundElem();
        timElem.setPath(filePath);
        timElem.setDuration(duration);
        this.timMessage.addElement(timElem);
    }

    @Override
    protected void parseData(TIMSoundElem elem) {
        this.path = elem.getPath();
        this.duration = elem.getDuration();
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationStr() {
        if (duration <= 0) {
            return "";
        } else if (duration <= 60) {
            return duration + "\"";
        } else if (duration > 60) {
            long m = duration / 60;
            long s = duration % 60;
            return m + "'" + s + "\"";
        } else {
            return "";
        }
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }



    @Override
    protected String getLocalPath() {
        return FileConstants.CACHE_PATH + "/tim/voice/" + EncryptUtil.MD5(path + getMsgId());
    }

    @Override
    protected void downloadImpl(final String localPath, final @Nullable FileDownloadCallback callBack) {
        timElem.getSoundToFile(localPath, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null) {
                    callBack.onError(i, s);
                }
            }

            @Override
            public void onSuccess() {
                path = localPath;
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        });
    }

    @Override
    public TIMMessage buildTimMsg() {
        return timMessage;
    }

    @Override
    public String getAction() {
        return ChatMsgType.VOICE.getType();
    }
}