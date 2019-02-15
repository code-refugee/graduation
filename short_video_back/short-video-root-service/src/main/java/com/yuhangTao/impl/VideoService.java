package com.yuhangTao.impl;

import com.yuhangTao.pojo.Comments;
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

    /*用戶喜歡視頻*/
    void userLikeVideo(String userId,String videoId,String videoCreateId);

    /*用戶不喜歡視頻*/
    void userDisLikeVideo(String userId,String videoId,String videoCreateId);

    /*查询我点赞（收藏）的视频列表*/
    PageResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);

    /*查询我关注的人的视频列表*/
    PageResult queryMyFollowVideos(String userId,Integer page,Integer pageSize);

    /*保存评论*/
    void saveComment(Comments comment);

    /*查询所有的评论*/
    PageResult getAllComments(String videoId,Integer page,Integer pageSize);
}
