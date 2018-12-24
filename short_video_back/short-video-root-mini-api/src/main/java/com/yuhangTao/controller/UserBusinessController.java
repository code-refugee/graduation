package com.yuhangTao.controller;

import com.yuhangTao.impl.UserService;
import com.yuhangTao.pojo.Users;
import com.yuhangTao.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

                if(userfaceFile.getParentFile()!=null||userfaceFile.getParentFile().isDirectory()){
                    //创建父目录文件夹
                    userfaceFile.getParentFile().mkdirs();
                }

                try {
                    inputStream=file.getInputStream();
                    fileOutputStream=new FileOutputStream(userfaceFile);
                    IOUtils.copy(inputStream,fileOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    return IMoocJSONResult.errorMsg("文件上传失败");
                }finally {                   //关闭流
                    if(inputStream!=null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fileOutputStream!=null){
                        try {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
}
