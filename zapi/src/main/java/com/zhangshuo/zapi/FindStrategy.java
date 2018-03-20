package com.zhangshuo.zapi;

import android.app.Activity;
import android.view.View;

/**
 * Created by zhangshuo on 2018/3/20.
 */

public enum FindStrategy {
    //实现ViewHolder策略
    VIEW {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends View> T findViewById(Object source, int id) {
            return (T) ((View) source).findViewById(id);
        }
    },
    //实现Activity策略
    ACTIVITY {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends View> T findViewById(Object source, int id) {
            return (T) ((Activity) source).findViewById(id);
        }
    };


    public abstract <T extends View> T findViewById(Object source, int id);
}
