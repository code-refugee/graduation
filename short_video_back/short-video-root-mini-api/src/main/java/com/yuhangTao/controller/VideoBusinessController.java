package com.yuhangTao.controller;

import com.yuhangTao.enums.VideoStatusEnum;
import com.yuhangTao.impl.BgmService;
import com.yuhangTao.impl.VideoService;
import com.yuhangTao.pojo.Bgm;
import com.yuhangTao.pojo.Videos;
import com.yuhangTao.utils.FFMpegUtils;
import com.yuhangTao.utils.IMoocJSONResult;
import com.yuhangTao.utils.PageResult;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/*对相关视频业务进行操作*/
@RestController
@Api(value = "视频相关业务接口",tags = {"视频相关业务的Controller"})
public class VideoBusinessController extends BasicController{

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    /*该接口主要负责以下几个功能：
     * 1.将用户上传的视频保存到相应的文件夹
     * 2.若用户选择了bgm则将视频与bgm合成
     * 3.若有合成视频则将它保存到相应的位置
     * 4.在数据库中更新视频的存储位置
     * 5.对视频的第一帧进行截图并保存*/
    @PostMapping(value = "/uploadVideo",headers = {"content-type=multipart/form-data"})
    @ApiOperation(value = "用户上传视频",notes = "用户上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "bgmId",value = "背景音乐Id",required = false,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "desc",value = "视频描述",required = false,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds",value = "视频时长",required = true,dataType = "float",paramType = "form"),
            @ApiImplicitParam(name = "videoWidth",value = "视频宽",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoHeight",value = "视频高",required = true,dataType = "String",paramType = "form"),
    })
    public IMoocJSONResult uploadVideo(String userId, String bgmId,String desc,
                                       float videoSeconds,int videoWidth,int videoHeight,
                                       @ApiParam(value = "视频",required = true)
                                       MultipartFile file){
        if(StringUtils.isBlank(userId))
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        //视频数据库保存的地址
        String uploadVideoDB="/"+userId+"/video";

        //封面数据库保存地址
        String uploadFaceDB="/"+userId+"/video";

        //输出流
        FileOutputStream fileOutputStream=null;

        //输入流
        InputStream inputStream=null;

        if(file!=null){
            String fileName=file.getOriginalFilename();
            if(StringUtils.isNoneBlank(fileName)){

                String finalFilePath=FILESPACE+uploadVideoDB+"/"+fileName;
//                uploadVideoDB+="/"+fileName;

                File finalVideoFile=new File(finalFilePath);

                if(finalVideoFile.getParentFile()!=null&&!finalVideoFile.getParentFile().isDirectory()){
                    finalVideoFile.getParentFile().mkdirs();
                }

                //先将原视频上传
                doCpopy(file,inputStream,fileOutputStream,finalVideoFile);

                FFMpegUtils ffMpegUtils=new FFMpegUtils(FFMPEGEXE);

                //判断用户是否添加过bgm
                if(StringUtils.isNoneBlank(bgmId)){
                    //通过数据库取得bgm存储的地址
                    Bgm result=bgmService.queryBgmById(bgmId);
                    String bgmPath=FILESPACE+result.getPath();
                    //给合并的视频取一个新的名字
                    String finalVideoName= UUID.randomUUID().toString()+".mp4";
                    String outputVideoPath=FILESPACE+uploadVideoDB+"/"+finalVideoName;
                    //更新数据库保存视频的地址
                    uploadVideoDB+="/"+finalVideoName;
                    //合并视频和音频
                    ffMpegUtils.doMerge(finalFilePath,bgmPath,videoSeconds,outputVideoPath);
                }else{
                    uploadVideoDB+="/"+fileName;
                }
                //生成视频封面
                String videoPath=FILESPACE+uploadVideoDB;
                String picName=UUID.randomUUID().toString()+".jpg";
                String picPath=FILESPACE+uploadFaceDB+"/"+picName;
                uploadFaceDB+="/"+picName;
                ffMpegUtils.createCover(videoPath,picPath);
            }
            //将数据保存到数据库中
            Videos video=new Videos();
            video.setUserId(userId);
            video.setAudioId(bgmId);
            video.setVideoDesc(desc);
            video.setVideoPath(uploadVideoDB);
            video.setVideoSeconds(videoSeconds);
            video.setVideoWidth(videoWidth);
            video.setVideoHeight(videoHeight);
            video.setCoverPath(uploadFaceDB);
            video.setStatus(VideoStatusEnum.SUCCESS.getValue());
            video.setCreateTime(new Date());

            //操作数据库
            videoService.saveVideo(video);
            return IMoocJSONResult.ok();
        }else{
            return IMoocJSONResult.errorMsg("文件上传失败");
        }
    }

    /**
     * 获取所有符合条件且未被禁播的视频信息
     * @param video
     * @param isSaveRecords 1-需要保存；0/空-不需要保存
     * @param page 当前是第几页
     * @return
     */
    @PostMapping("/quarryAll")
    @ApiOperation(value = "获取所有未禁播的视频信息",notes = "获取所有未禁播的视频信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isSaveRecords",value = "是否保存搜索记录",required = false,dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "第几页",required = false,dataType = "Integer",paramType = "query")
    })
    public IMoocJSONResult quarryAll(@RequestBody Videos video, Integer isSaveRecords, Integer page){
        if(page==null)
            page=1;
        PageResult result=videoService.quarryAllVideos(video,isSaveRecords,page,PAGESIZE);
        return IMoocJSONResult.ok(result);
    }

    /*獲取熱搜詞*/
    @GetMapping("/queryHot")
    @ApiOperation(value = "热搜词查询",notes = "热搜词查询的接口")
    public IMoocJSONResult queryHot(){
        List<String> contents=videoService.queryHot();
        return IMoocJSONResult.ok(contents);
    }

    /*用戶喜歡當前視頻*/
    @PostMapping("/userLike")
    @ApiOperation(value = "用戶喜歡當前視頻",notes="用戶喜歡當前視頻的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用戶Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "視頻Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoCreateId",value = "視頻創造者Id",required = true,dataType = "String",paramType = "query"),
    })
    public IMoocJSONResult userLikeVideo(String userId, String videoId, String videoCreateId){
        videoService.userLikeVideo(userId,videoId,videoCreateId);
        return IMoocJSONResult.ok();
    }

    /*用戶不喜歡當前視頻*/
    @PostMapping("/userUnLike")
    @ApiOperation(value = "用戶不喜歡當前視頻",notes="用戶不喜歡當前視頻的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用戶Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "視頻Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoCreateId",value = "視頻創造者Id",required = true,dataType = "String",paramType = "query"),
    })
    public IMoocJSONResult userDisLikeVideo(String userId, String videoId, String videoCreateId){
        videoService.userDisLikeVideo(userId,videoId,videoCreateId);
        return IMoocJSONResult.ok();
    }


}
