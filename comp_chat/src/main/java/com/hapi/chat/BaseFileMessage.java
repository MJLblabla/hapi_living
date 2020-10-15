package com.hapi.chat;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.hapi.ut.FileUtil;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMMessage;

/**
 *
 * @date 2019/2/27
 */
public abstract class BaseFileMessage<T extends TIMElem> extends IMChatMessage<T> {

    protected String path;
    protected String fileName;
    protected long fileSize;

    public BaseFileMessage() {
        super();
    }

    public BaseFileMessage(TIMMessage message) {
        super(message);
    }

    @Override
    protected final void parseIMMessage(T elem) {
        parseData(elem);
        if (IMUIKit.isElemAutoDownload()) {
            download(null);
        }
    }

    /**
     * 解析数据
     *
     * @param elem
     */
    protected abstract void parseData(T elem);

    @Override
    public void save() {

    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    /**
     * 下载文件
     *
     * @return true表示下载成功或者文件已存在  false表示文件未下载，并开始下载
     */
    public final boolean download(@Nullable FileDownloadCallback callBack) {
//        if (TextUtils.isEmpty(path)) {
//            if (callBack != null) {
//                callBack.onError(-1, "path is empty");
//            }
//            return false;
//        }
        Uri uri = Uri.parse(path);
        //先判断是不是本地文件,如果是本地文件跳过下载
        if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())
                || ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            return true;
        }
        String localPath = getLocalPath();
        //路径转化后，再次判断本地有没有文件
        if (FileUtil.exists(localPath)) {
            path = localPath;
            return true;
        }
        FileUtil.createFile(localPath);
        //最后进行网络下载, 下载实现交给子类
        downloadImpl(localPath, callBack);
        return false;
    }

    protected abstract String getLocalPath();

    protected abstract void downloadImpl(String localPath, @Nullable FileDownloadCallback callBack);







}
