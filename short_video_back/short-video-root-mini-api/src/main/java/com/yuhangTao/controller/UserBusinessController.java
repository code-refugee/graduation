package com.yuhangTao.controller;

import com.yuhangTao.impl.UserService;
import com.yuhangTao.pojo.Users;
import com.yuhangTao.pojo.UsersReport;
import com.yuhangTao.utils.IMoocJSONResult;
import com.yuhangTao.vo.PublisherVO;
import com.yuhangTao.vo.UsersVO;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Api(value = "用户相关业务的接口",tags = {"用户业务相关的Controller"})
public class UserBusinessController extends BasicController{

    @Autowired
    private UserService userService;


    /*需要完成两个部分
     * 1.将头像保存到本地的文件夹内，如果没有文件夹则创建一个
     * 2.将头像保存的相对路径跟新到数据库中
     * 注意：Swagger不支持数组的形式，因此如果你有多个MultipartFile，
     * 可以用逗号一个个隔开*/
    @PostMapping(value = "/uploadface",headers = {"content-type=multipart/form-data"})
    @ApiOperation(value = "上传头像",notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query")
    public IMoocJSONResult upLoadFace(String userId,
                                      @ApiParam(value = "头像",required = true)
                                      MultipartFile file)  {

        //检查用户名是否为空（防止被黑客利用）
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户ID不能为空");
        }

        //保存到数据库中的相对路径
        String uploadPathDB="/"+userId+"/face";

        //输出流，因为要输出到文件，所以定义为FileOutputStream
        FileOutputStream fileOutputStream=null;

        //输入流
        InputStream inputStream=null;

        //判断上传的文件是否为空
        if(file==null){
            return IMoocJSONResult.errorMsg("文件上传失败");
        }else{
            //获取上传文件的原名
            String filename=file.getOriginalFilename();

            //不为空(创建保存图片的文件夹并将图片保存进去)
            if(StringUtils.isNoneBlank(filename)){

                //最终保存图片的路径
                String finalFilePath=FILESPACE+uploadPathDB+"/"+filename;

                //数据库中的路径
                uploadPathDB=uploadPathDB+"/"+filename;

                File userfaceFile=new File(finalFilePath);

                if(userfaceFile.getParentFile()!=null&&!userfaceFile.getParentFile().isDirectory()){
                    //创建父目录文件夹
                    userfaceFile.getParentFile().mkdirs();
                }

                doCpopy(file,inputStream,fileOutputStream,userfaceFile);
            }
            //将用户头像保存的路径更新到数据库中
            Users user=new Users();
            user.setId(userId);
            user.setFaceImage(uploadPathDB);
            userService.updateUserInfo(user);
            //将保存头像的路径返回
            return IMoocJSONResult.ok(uploadPathDB);
        }
    }

    @GetMapping("/query")
    @ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "fanId",value = "粉丝Id",required = false,dataType = "String",paramType = "query")
    })


    public IMoocJSONResult queryUserInfo(String userId,String fanId){

        if(StringUtils.isBlank(userId))
            return IMoocJSONResult.errorMsg("用户Id不能为空");

        Users result=userService.queryUserInfo(userId);
        if(result==null){
            return IMoocJSONResult.errorMsg("用户不存在");
        }
        /*为什么要用UsersVO，因为我们将它的password字段设置为了JsonIgnore
         * 其实你也可以用result.setPassword("")然后返回result*/
        UsersVO usersVO=new UsersVO();
        BeanUtils.copyProperties(result,usersVO);
        usersVO.setFollow(userService.queryIsFollow(userId,fanId));
        return IMoocJSONResult.ok(usersVO);
    }

    /*该接口用于查询视频发布者的信息
    * 同时还能查询用户是否点赞过该视频*/
    @PostMapping("/queryPublisher")
    @ApiOperation(value = "查询视频发布者信息",notes = "查询视频发布者信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginUserId",value = "用户Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "视频Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "publisherId",value = "发布者Id",required = true,dataType = "String",paramType = "query"),
    })
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,String publisherId){
        if(StringUtils.isBlank(publisherId))
            return IMoocJSONResult.errorMsg("必要信息丢失");
        Users user=userService.queryUserInfo(publisherId);
        boolean isuserLikeVideo=userService.isUserLikeVideo(loginUserId,videoId);
        UsersVO usersVO=new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        PublisherVO publisherVO=new PublisherVO();
        publisherVO.setUsersVO(usersVO);
        publisherVO.setUserLikeVideo(isuserLikeVideo);
        return IMoocJSONResult.ok(publisherVO);
    }

    /*用户关注视频发布者*/
    @PostMapping("/beyourfans")
    @ApiOperation(value = "关注",notes = "关注的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "publisherId",value = "视频发布者Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query"),
    })
    public IMoocJSONResult beYouFans(String publisherId,String userId){
        if(StringUtils.isBlank(publisherId)||StringUtils.isBlank(userId))
            return IMoocJSONResult.errorMsg("必要信息丢失");
        userService.followPublisher(publisherId,userId);
        return IMoocJSONResult.ok();
    }

    /*用户不再关注视频发布者*/
    @PostMapping("/notyourfans")
    @ApiOperation(value = "取消关注",notes = "取消关注的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "publisherId",value = "视频发布者Id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query"),
    })
    public IMoocJSONResult notYouFans(String publisherId,String userId){
        if(StringUtils.isBlank(publisherId)||StringUtils.isBlank(userId))
            return IMoocJSONResult.errorMsg("必要信息丢失");
        userService.deleteFollow(publisherId,userId);
        return IMoocJSONResult.ok();
    }

    @PostMapping("/reportUser")
    @ApiOperation(value = "举报用户",notes = "举报用户的接口")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){
        //当用户被举报时，视频不会立马封禁，而是交由管理员处理
        userService.saveReportReason(usersReport);
        return IMoocJSONResult.ok();
    }
}
