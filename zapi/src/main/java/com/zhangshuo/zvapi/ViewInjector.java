package com.zhangshuo.zvapi;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangshuo on 2018/3/20.
 */

public final class ViewInjector {
    private ViewInjector() {
        throw new AssertionError("no instance!");
    }

    private static final Map<Class<?>, AbstractInjector<Object>> INJECTORS = new LinkedHashMap<>();

    //注入Activity
    public static void inject(Activity activity) {
        AbstractInjector<Object> injector = findInjector(activity);
        if (injector != null)
            injector.inject(FindStrategy.ACTIVITY, activity, activity);
    }

    /**
     * 注入ViewHolder
     *
     * @param target 目标VH
     * @param view   父View
     */
    public static void inject(Object target, View view) {
        AbstractInjector<Object> injector = findInjector(target);
        if (injector != null)
            injector.inject(FindStrategy.VIEW, target, view);
    }

    @SuppressWarnings("unchecked")
    private static AbstractInjector<Object> findInjector(Object target) {
        Class<?> clazz = target.getClass();
        AbstractInjector<Object> injector = INJECTORS.get(clazz);
        if (injector == null) {
            try {
                /*生成代理类对象 可能不存在该代理类*/
                Class<?> injectorClazz = Class.forName(clazz.getName()
                        + "$$Proxy");
                injector = (AbstractInjector<Object>) injectorClazz.newInstance();
                INJECTORS.put(clazz, injector);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                Log.d("INJECT", e.getCause().toString());
            }
        }
        return injector;
    }

}
