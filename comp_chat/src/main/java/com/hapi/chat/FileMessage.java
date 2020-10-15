package com.hapi.chat;

import androidx.annotation.Nullable;

import com.hapi.ut.EncryptUtil;
import com.hapi.ut.constans.FileConstants;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFileElem;
import com.tencent.imsdk.TIMMessage;

import java.io.File;

/**
 *
 * @date 2019/2/27
 */
public class FileMessage extends BaseFileMessage<TIMFileElem> {

    public FileMessage(TIMMessage message) {
        super(message);
    }



    public FileMessage(File file) {
        this(file.getPath(), file.getName(), file.getTotalSpace());
    }

    public FileMessage(String path, String fileName, long fileSize) {
        super();
        this.path = path;
        this.fileName = fileName;
        this.fileSize = fileSize;

        timElem = new TIMFileElem();
        timElem.setPath(path);
        timElem.setFileName(fileName);
        this.timMessage.addElement(timElem);
    }

    @Override
    protected void parseData(TIMFileElem elem) {
        path = elem.getPath();
        fileName = elem.getFileName();
        fileSize = elem.getFileSize();
    }

    @Override
    public void save() {

    }



    @Override
    protected String getLocalPath() {
        return FileConstants.CACHE_PATH + "/tim/file/" + EncryptUtil.MD5(path + getMsgId());
    }

    @Override
    protected void downloadImpl(final String localPath, final @Nullable FileDownloadCallback callBack) {
        timElem.getToFile(localPath, new TIMCallBack() {
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
        return ChatMsgType.FILE.getType();
    }
}
