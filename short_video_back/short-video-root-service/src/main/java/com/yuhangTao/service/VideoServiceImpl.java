package com.yuhangTao.service;

import com.yuhangTao.impl.VideoService;
import com.yuhangTao.mapper.VideosMapper;
import com.yuhangTao.org.n3r.idworker.Sid;
import com.yuhangTao.pojo.Videos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private Sid sid;

    /*保存视频*/
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveVideo(Videos video) {
        String id=sid.nextShort();
        video.setId(id);
        /*这里使用videosMapper的insertSelective方法，因为他会使用
         * 数据库中我们设置的默认值，而使用insert则不会,因为我们的
         * like_counts默认为0所以用insertSelective方法*/
        videosMapper.insertSelective(video);
    }
}
