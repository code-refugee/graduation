package com.yuhangTao.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuhangTao.impl.VideoService;
import com.yuhangTao.mapper.SearchRecordsMapper;
import com.yuhangTao.mapper.VideosCustomMapper;
import com.yuhangTao.mapper.VideosMapper;
import com.yuhangTao.org.n3r.idworker.Sid;
import com.yuhangTao.pojo.SearchRecords;
import com.yuhangTao.pojo.Videos;
import com.yuhangTao.utils.PageResult;
import com.yuhangTao.vo.VideosVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private VideosCustomMapper videosCustomMapper;

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

    /*获取符合条件的所有视频*/
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageResult quarryAllVideos(Videos video, Integer isSaveRecords, Integer page, Integer pageSize) {

        String desc=video.getVideoDesc();
        //如果要保存搜索记录则在数据库中保存
        if(isSaveRecords!=null&&isSaveRecords==1){
            String recordId= sid.nextShort();
            SearchRecords searchRecords=new SearchRecords();
            searchRecords.setId(recordId);
            searchRecords.setContent(desc);
            searchRecordsMapper.insert(searchRecords);
        }
        //进行分页查找
        PageHelper.startPage(page,pageSize);
        List<VideosVO> result=videosCustomMapper.queryAllVideos(desc);
        PageInfo<VideosVO> info=new PageInfo<>(result);
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setAllPages(info.getPages());
        pageResult.setTotal(info.getTotal());
        pageResult.setContent(result);
        return pageResult;
    }
}
