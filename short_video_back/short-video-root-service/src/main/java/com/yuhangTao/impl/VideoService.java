package com.yuhangTao.impl;

import com.yuhangTao.pojo.Videos;
import com.yuhangTao.utils.PageResult;

import java.util.List;

/*视频服务接口*/
public interface VideoService {

    /*保存视频*/
    void saveVideo(Videos video);

    /*获取符合条件的所有视频*/
    PageResult quarryAllVideos(Videos video,Integer isSaveRecords,Integer page,Integer pageSize);

    /*查询热搜词*/
    List<String> queryHot();
}
