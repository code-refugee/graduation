package com.yuhangTao.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuhangTao.impl.VideoService;
import com.yuhangTao.mapper.*;
import com.yuhangTao.org.n3r.idworker.Sid;
import com.yuhangTao.pojo.Comments;
import com.yuhangTao.pojo.SearchRecords;
import com.yuhangTao.pojo.UsersLikeVideos;
import com.yuhangTao.pojo.Videos;
import com.yuhangTao.utils.PageResult;
import com.yuhangTao.utils.TimeAgoUtils;
import com.yuhangTao.vo.CommentsVO;
import com.yuhangTao.vo.VideosVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private VideosCustomMapper videosCustomMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsCustomMapper commentsCustomMapper;

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

        //这里我们不仅可以查询符合描述的视频，还能查询发布者已发布的视频
        String desc=video.getVideoDesc();
        String userId=video.getUserId();
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
        List<VideosVO> result=videosCustomMapper.queryAllVideos(desc,userId);
        PageInfo<VideosVO> info=new PageInfo<>(result);
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setAllPages(info.getPages());
        pageResult.setTotal(info.getTotal());
        pageResult.setContent(result);
        return pageResult;
    }

    /*查询我点赞（收藏）的视频列表*/
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        //进行分页查找
        PageHelper.startPage(page,pageSize);
        List<VideosVO> result=videosCustomMapper.queryMyLikeVideos(userId);
        PageInfo<VideosVO> info=new PageInfo<>(result);
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setAllPages(info.getPages());
        pageResult.setTotal(info.getTotal());
        pageResult.setContent(result);
        return pageResult;

    }

    /*查询我关注的人的视频列表*/
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        //进行分页查找
        PageHelper.startPage(page,pageSize);
        List<VideosVO> result=videosCustomMapper.showMyFollowVideos(userId);
        PageInfo<VideosVO> info=new PageInfo<>(result);
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setAllPages(info.getPages());
        pageResult.setTotal(info.getTotal());
        pageResult.setContent(result);
        return pageResult;

    }

    /*保存评论*/
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveComment(Comments comment) {
        String id=sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insertSelective(comment);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<CommentsVO> result= commentsCustomMapper.queryComments(videoId);
        //做这一步的目的是使我们得到的时间更易于客户阅读
        for (CommentsVO c : result){
            c.setTimeAgoStr(TimeAgoUtils.format(c.getCreateTime()));
        }
        PageInfo<CommentsVO> info=new PageInfo<>(result);
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setAllPages(info.getPages());
        pageResult.setTotal(info.getTotal());
        pageResult.setContent(result);
        return pageResult;
    }

    /*
    * 查詢熱搜
    * */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> queryHot() {
        return searchRecordsMapper.queryHot();
    }

    /*
    * 用戶喜歡視頻
    * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userLikeVideo(String userId, String videoId, String videoCreateId) {
        //1.保存用戶和喜歡的視頻
        String id=sid.nextShort();
        UsersLikeVideos usersLikeVideos=new UsersLikeVideos();
        usersLikeVideos.setId(id);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);
        //2.視頻創造者受喜歡的數量纍加
        usersMapper.addReceiveLikeCounts(videoCreateId);
        //3.該視頻受喜歡數量纍加
        videosCustomMapper.addVideoLikeCounts(videoId);

    }

    /*
     * 用戶不喜歡視頻
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userDisLikeVideo(String userId, String videoId, String videoCreateId) {
        //1.刪除用戶和用戶喜歡的視頻
        Example example=new Example(UsersLikeVideos.class);
        Example.Criteria criteria=example.or();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);
        //2.視頻創造者受喜歡的數量減一
        usersMapper.reduceReceiveLikeCounts(videoCreateId);
        //3.該視頻受喜歡數量減一
        videosCustomMapper.reduceVideoLikeCounts(videoId);

    }

}
