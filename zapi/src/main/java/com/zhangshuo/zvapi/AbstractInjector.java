package com.zhangshuo.zvapi;

/**
 * Created by zhangshuo on 2018/3/20.
 */

public interface AbstractInjector<T> {
    /**
     *
     * @param finder findView策略
     * @param target 目标类对象
     * @param source 提供findView方法的类对象
     */
    void inject(FindStrategy finder,T target,Object source);
}
