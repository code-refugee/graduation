package com.yuhangTao.enums;

/*枚举，用于设置视频的状态*/
public enum VideoStatusEnum {
    SUCCESS(1),//发布成功
    FORBIND(2);//禁止播放，管理员操作

    public final int value;

    VideoStatusEnum(int i) {
        this.value=i;
    }

    public int getValue(){
        return value;
    }
}
