package com.zhangshuo.zvcompiler;

/**
 * Created by zhangshuo on 2018/3/20.
 */

final class ViewInfo {
    private String id;
    private String name;
    private String clickEventName;

    public ViewInfo(String id, String name, String clickEventName) {
        this.id = id;
        this.name = name;
        this.clickEventName = clickEventName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClickEventName() {
        return clickEventName;
    }

    public void setClickEventName(String clickEventName) {
        this.clickEventName = clickEventName;
    }
}
