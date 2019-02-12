package com.yuhangTao.controller;

import com.yuhangTao.utils.IMoocJSONResult;
import com.yuhangTao.utils.RedisOperator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BasicController {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION="USER-REDIS-SESSION";

    //文件保存的命名空间(绝对路径)
    public static final String FILESPACE="F:/graduation/userupload";

    //ffmpeg工具所在的路径
    public static final String FFMPEGEXE="D:/工具/FFmpeg/ffmpeg/bin/ffmpeg.exe";

    //每次查询的数量
    public static final Integer PAGESIZE=6;

    //将视频上传至相应的文件夹下
    public void doCpopy(MultipartFile file, InputStream inputStream, FileOutputStream fileOutputStream, File toFile){
        try {
            inputStream=file.getInputStream();
            fileOutputStream=new FileOutputStream(toFile);
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
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
}
