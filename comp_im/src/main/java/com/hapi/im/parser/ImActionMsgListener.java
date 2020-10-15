package com.hapi.im.parser;

import com.hapi.im.been.IMsgBean;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ImActionMsgListener implements ImMsgListener {
    public ImActionMsgListener(){
    }

    private HashMap<String, Function1<IMsgBean, Unit>> hashMap=new HashMap<>();

    public <T extends IMsgBean> ImActionMsgListener onOptAction(String action, Function1<T, Unit> call){
        hashMap.put(action, (Function1<IMsgBean, Unit>) call);
        return this;
    }




    @Override
    final public void onNewMsg( IMsgBean msg) {


        Function1<IMsgBean,Unit> function1= hashMap.get(msg.getAction());

        if(function1!=null){
            function1.invoke(msg);
        }
    }
}
