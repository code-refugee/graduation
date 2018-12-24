package com.yuhangTao.controller;

import com.yuhangTao.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

public class BasicController {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION="USER-REDIS-SESSION";

    //文件保存的命名空间(绝对路径)
    public static final String FILESPACE="F:/graduation/userupload";
}
