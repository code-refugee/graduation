package com.yuhangTao.mapper;


import com.yuhangTao.pojo.Users;
import com.yuhangTao.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersMapper extends MyMapper<Users> {

    /*
    * 用戶受喜歡數加一
    * */
    void addReceiveLikeCounts(@Param("userId") String userId);

    /*
    * 用戶受喜歡數減一
    * */
    void reduceReceiveLikeCounts(@Param("userId") String userId);

    /*
    * 视频发布者粉丝数量加一
    * */
    void addFansCounts(@Param("publisherId") String publisherId);

    /*
     * 视频发布者粉丝数量减一
     * */
    void reduceFansCounts(@Param("publisherId") String publisherId);

    /*
    * 用户关注数加一
    * */
    void addFollowCounts(@Param("userId") String userId);

    /*
     * 用户关注数减一
     * */
    void reduceFollowCounts(@Param("userId") String userId);
}