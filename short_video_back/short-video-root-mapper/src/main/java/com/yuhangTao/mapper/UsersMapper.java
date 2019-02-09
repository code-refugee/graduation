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
}