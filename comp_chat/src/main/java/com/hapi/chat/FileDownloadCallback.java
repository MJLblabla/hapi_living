package com.hapi.chat;

/**
 *
 * @date 2019/2/27
 */
public interface FileDownloadCallback {

    void onSuccess(Object... objects);

    void onError(int code, String reason);
}
