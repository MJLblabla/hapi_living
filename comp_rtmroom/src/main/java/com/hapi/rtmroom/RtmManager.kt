package com.hapi.rtmroom

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import io.agora.rtm.*
import io.agora.rtm.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RtmManager {


    private val TAG = "RtmManager"
    var mRtmClient: RtmClient? = null
        private set
    private var loginStatus = false



    private var rtmClientListeners = ArrayList<RtmClientListener>()
    fun addRtmClientListeners(rtmClientListener: RtmClientListener, isAdd: Boolean) {
        if (isAdd) {
            rtmClientListeners.add(rtmClientListener)
        } else {
            rtmClientListeners.remove(rtmClientListener)
        }
    }

    private var rtmCallEventListeners = ArrayList<RtmCallEventListener>()
    fun addRtmCallEventListener(rtmCallEventListener: RtmCallEventListener, isAdd: Boolean) {
        if (isAdd) {
            rtmCallEventListeners.add(rtmCallEventListener)
        } else {
            rtmCallEventListeners.remove(rtmCallEventListener)
        }
    }

    private var mRtmCallEventListener = object : RtmCallEventListener {

        override fun onRemoteInvitationCanceled(p0: RemoteInvitation?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onRemoteInvitationCanceled(p0)
                }
            }
        }

        override fun onRemoteInvitationRefused(p0: RemoteInvitation?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onRemoteInvitationRefused(p0)
                }
            }
        }

        override fun onLocalInvitationFailure(p0: LocalInvitation?, p1: Int) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onLocalInvitationFailure(p0,p1)
                }
            }
        }

        override fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onLocalInvitationRefused(p0,p1)
                }
            }
        }

        override fun onLocalInvitationCanceled(p0: LocalInvitation?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onLocalInvitationCanceled(p0)
                }
            }
        }

        override fun onRemoteInvitationFailure(p0: RemoteInvitation?, p1: Int) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onRemoteInvitationFailure(p0,p1)
                }
            }
        }

        override fun onLocalInvitationReceivedByPeer(p0: LocalInvitation?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onLocalInvitationReceivedByPeer(p0)
                }
            }
        }

        override fun onRemoteInvitationReceived(p0: RemoteInvitation?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onRemoteInvitationReceived(p0)
                }
            }
        }

        override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onLocalInvitationAccepted(p0,p1)
                }
            }
        }

        override fun onRemoteInvitationAccepted(p0: RemoteInvitation?) {
            GlobalScope.launch(Dispatchers.Main) {
                rtmCallEventListeners.forEach {
                    it.onRemoteInvitationAccepted(p0)
                }
            }
        }
    }


    @SuppressLint("LogNotTimber")
    fun init(appId: String, context: Context) {
        try {
            mRtmClient = RtmClient.createInstance(context, appId,
                object : RtmClientListener {
                    @SuppressLint("LogNotTimber")
                    override fun onTokenExpired() {
                        Log.d(TAG, "Connection state  onTokenExpired ")
                        rtmClientListeners.forEach {
                         it.onTokenExpired()
                        }
                    }

                    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                        rtmClientListeners.forEach {
                            it.onPeersOnlineStatusChanged(p0)
                        }
                    }

                    @SuppressLint("LogNotTimber")
                    override fun onConnectionStateChanged(state: Int, reason: Int) {
                        Log.d(
                            TAG, "Connection state changes to "
                                    + state + " reason: " + reason
                        )
                        rtmClientListeners.forEach {
                            it.onConnectionStateChanged(state,reason)
                        }
                    }

                    @SuppressLint("LogNotTimber")
                    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String) {
                        val msg = rtmMessage.text
                        Log.d(TAG, "Message received  from $peerId$msg")
                       // rtmClientListener?.onMessageReceived(rtmMessage, peerId)
                        rtmClientListeners.forEach {
                            it.onMessageReceived(rtmMessage,peerId)
                        }
                    }
                })


        } catch (e: Exception) {
            Log.d(TAG, "RTM SDK init fatal error!")
            throw RuntimeException("You need to check the RTM init process.")
        }
        mRtmClient?.rtmCallManager?.setEventListener(mRtmCallEventListener)
    }


    private var lastUid = ""
    fun login(uid: String, callback: ResultCallback<Void>? = null) {
        lastUid = uid
        Log.d(TAG, "rtm　登录")
        mRtmClient?.login(null, uid, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void) {
                loginStatus = true
                callback?.onSuccess(p0)
                Log.d(TAG, "rtm　登录onSuccess")
            }

            override fun onFailure(p0: ErrorInfo) {
                loginStatus = false
                callback?.onFailure(p0)
                Log.d(TAG, "rtm　　ｌｏgin onFailure")
            }
        })
    }

    fun reLogin(callback: ResultCallback<Void>? = null) {
        login(lastUid, callback)
    }

    fun loginOut() {
        mRtmClient?.logout(null)
    }


    /**
     * 点对点消息
     */
    fun sendPeerMessage(
        peerId: String,
        content: String,
        resultCallback: ResultCallback<Void>? = null
    ) {

        val message = mRtmClient?.createMessage()
        message?.text = content

        val option = SendMessageOptions()
        option.enableOfflineMessaging = true

        mRtmClient?.sendMessageToPeer(peerId, message, option, object : ResultCallback<Void?> {

            override fun onSuccess(aVoid: Void?) {
                GlobalScope.launch(Dispatchers.Main) { resultCallback?.onSuccess(aVoid) }

            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                GlobalScope.launch(Dispatchers.Main) {
                    resultCallback?.onFailure(errorInfo)
                }
            }
        })
    }

    fun createChannel(channelId: String, rtmChannelListener: RtmChannelListener): RtmChannel? {
        return mRtmClient?.createChannel(channelId, rtmChannelListener)
    }

}