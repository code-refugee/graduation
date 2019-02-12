package com.yuhangTao.mapper;

import com.yuhangTao.utils.MyMapper;
import com.yuhangTao.vo.VideosVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideosCustomMapper extends MyMapper<VideosVO> {

    List<VideosVO> queryAllVideos(@Param("videoDesc")String videoDesc,
                                  @Param("userId") String userId);

    /*
    *对视频的喜欢数量进行累加
    * */
    void addVideoLikeCounts(@Param("videoId") String videoId);

    /*
    * 对视频的喜欢数量进行累减
    * */
    void reduceVideoLikeCounts(@Param("videoId") String videoId);

    /*查询我点赞（收藏）的视频列表*/
    List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

    /*查询我关注的人的视频列表*/
    List<VideosVO> showMyFollowVideos(@Param("userId") String userId);
}
