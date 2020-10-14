package com.pince.im.parser;

import android.text.TextUtils;

import com.pince.im.ImMsgDispatcher;
import com.pince.im.been.GroupMsg;
import com.pince.im.been.GroupMsg;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public  class ImGroupActionMsgListener implements GroupImMsgLister {


    public String groupId="";

    /**
     * 如果要过滤群id 不过滤就传库
     * @param groupId
     */
    public ImGroupActionMsgListener(String groupId){
        this.groupId=groupId;
    }

    public ImGroupActionMsgListener(){
    }


    private HashMap<String, Function1<GroupMsg,Unit>> hashMap=new HashMap<>();

    public <T extends GroupMsg> ImGroupActionMsgListener onOptAction(String action, Function1<T, Unit> call){
        hashMap.put(action, (Function1<GroupMsg, Unit>) call);
        return this;
    }

    public boolean isAttach=false;

    public void attach(){
        isAttach = true;
        ImMsgDispatcher.INSTANCE.getGroupImMsgLister().add(this);
    }

    public void dettach(){
        isAttach=false;
        ImMsgDispatcher.INSTANCE.getGroupImMsgLister().remove(this);
    }

    @Override
    final public void onNewMsg( GroupMsg msg) {


        Function1<GroupMsg,Unit> function1= hashMap.get(msg.getAction());
         if(!TextUtils.isEmpty(groupId)){
             if(!groupId.equals(msg.getGroupId())){
                 return;
             }
         }
         if(function1!=null){
             function1.invoke(msg);
         }
    }
}

