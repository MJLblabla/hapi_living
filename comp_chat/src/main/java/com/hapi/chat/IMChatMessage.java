package com.hapi.chat;

import com.hapi.im.been.IMsgBean;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMMessageExt;

/**
 * 消息数据基类
 */
public abstract class IMChatMessage<T extends TIMElem> implements IMsgBean {
    protected final String TAG = "IMMessage";

    /**
     * SDK消息内部实体
     */
    protected TIMMessage timMessage;
    protected T timElem;
    /**
     * 用户的id
     */
    public String userId;
    /**
     * 用户的id
     */
    public String nickName;
    /**
     * 用户的头像
     */
    public String avatarUrl;
    /**
     * 消息描述信息
     */
    private String desc;



    /**
     * 发送消息的构造
     */
    public IMChatMessage() {
        this.timMessage = new TIMMessage();
        this.userId = IMManager.userinfoProvider.getUid();
        this.nickName = IMManager.userinfoProvider.getNickName();
        this.avatarUrl = IMManager.userinfoProvider.getAvatar();
    }

    @Override
    public TIMMessage getTimMsg() {
        return timMessage;
    }

    /**
     * 接受消息的构造，解析需要调用 {@link #parseFrom()}
     *
     * @param message
     */
    public IMChatMessage(TIMMessage message) {
        this.timMessage = message;
    }


    public IMChatMessage parseFrom() {
        if (isSelf()) {
            this.userId = IMManager.userinfoProvider.getUid();
            this.nickName = IMManager.userinfoProvider.getNickName();
            this.avatarUrl = IMManager.userinfoProvider.getAvatar();
        } else {
            getSenderProfile(new TIMValueCallBack<TIMUserProfile>() {
                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onSuccess(TIMUserProfile timUserProfile) {
                    IMChatMessage.this.userId = timUserProfile.getIdentifier();
                    IMChatMessage.this.nickName = timUserProfile.getNickName();
                    IMChatMessage.this.avatarUrl = timUserProfile.getFaceUrl();
                }
            });
        }
        if (timMessage.getElementCount() >= 0) {
            timElem = (T) timMessage.getElement(0);
            parseIMMessage(timElem);
        }
        return this;
    }

    /**
     * 解析数据
     */
    protected abstract void parseIMMessage(T elem);


    /**
     * 保存消息或消息文件
     */
    public abstract void save();

    /**
     * 获取消息的会话类型
     *
     * @return
     */
    public TIMConversationType getConvType() {
        if (timMessage == null||timMessage.getConversation()==null) {
            return TIMConversationType.Invalid;
        }
        return timMessage.getConversation().getType();
    }

    /**
     * 获取消息类型
     *
     * @return
     */
    public TIMElemType getMsgType() {
        return timElem.getType();
    }

    /**
     * 获取消息实体类
     *
     * @return
     */
    public TIMMessage getTimMessage() {
        return timMessage;
    }

    /**
     * 获取消息内部elem
     *
     * @return
     */
    public T getTimElem() {
        return timElem;
    }

    /**
     * 判断是否是自己发的
     */
    public boolean isSelf() {
        return timMessage.isSelf();
    }

    /**
     * 获取消息id
     *
     * @return
     */
    public String getMsgId() {
        return timMessage.getMsgId();
    }

    /**
     * 消息发送状态
     */
    public TIMMessageStatus getSendStatus() {
        return timMessage.status();
    }

    /**
     * 是否需要发送已读上报
     *
     * @return
     */
    public boolean needMsgAck() {
        return false;
    }

    /**
     * 設置消息已读
     */
    public void setRead() {
        if(timMessage.getConversation() == null) {
            return;
        }
        TIMConversationExt conExt = new TIMConversationExt(timMessage.getConversation());
        conExt.setReadMessage(timMessage, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {

            }
        });
    }

    /**
     * 是否为同一条消息
     *
     * @param other
     * @return
     */
    public boolean isTheSame(IMChatMessage other) {
        return timMessage.getMsgUniqueId() == other.timMessage.getMsgUniqueId();
    }

    /**
     * 删除消息
     */
    public boolean remove() {
        if (timMessage != null) {
            TIMMessageExt timMessageExt = new TIMMessageExt(timMessage);
            return timMessageExt.remove();
        }
        return false;
    }

    /**
     * 获取发送者
     */
    public String getSender() {
        return timMessage.getSender();
    }

    /**
     * 获取消息发送者的信息
     *
     * @return
     */
    public void getSenderProfile(TIMValueCallBack<TIMUserProfile> callBack) {
        timMessage.getSenderProfile(callBack);
    }

    /**
     * 打包消息
     */
    public abstract TIMMessage buildTimMsg();

    public interface SenderProfileCallback {
        void getSenderProfile(TIMUserProfile timUserProfile);
    }
}
