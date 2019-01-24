package com.yuhangTao.mapper;

import com.yuhangTao.utils.MyMapper;
import com.yuhangTao.vo.VideosVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideosCustomMapper extends MyMapper<VideosVO> {
    List<VideosVO> queryAllVideos(@Param("videoDesc")String videoDesc);
}
