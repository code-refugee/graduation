package com.yuhangTao.impl;


import com.yuhangTao.pojo.Bgm;

import java.util.List;

/**
 * bgm服务接口
 */
public interface BgmService {

    //通过bgmId查询bgm信息
    Bgm queryBgmById(String bgmId);

    //获取背景音乐列表
    List<Bgm> queryBgmList();

}
