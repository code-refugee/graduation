package com.yuhangTao.mapper;

import com.yuhangTao.pojo.Comments;
import com.yuhangTao.utils.MyMapper;
import com.yuhangTao.vo.CommentsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsCustomMapper extends MyMapper<CommentsVO> {
    List<CommentsVO> queryComments(@Param("videoId") String videoId);
}
